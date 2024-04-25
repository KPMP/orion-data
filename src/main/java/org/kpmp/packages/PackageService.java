package org.kpmp.packages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.kpmp.dmd.DmdService;
import org.kpmp.logging.LoggingService;
import org.kpmp.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PackageService {

	@Value("${package.state.upload.succeeded}")
	private String uploadSucceededState;
	@Value("${package.state.upload.failed}")
	private String uploadFailedState;

	private static final MessageFormat packageIssue = new MessageFormat("{0} {1}");
	private static final MessageFormat fileIssue = new MessageFormat("ERROR|zip|{0}");
	private PackageFileHandler packageFileHandler;
	private FilePathHelper filePathHelper;
	private CustomPackageRepository packageRepository;
	private DmdService dmdService;
	private LoggingService logger;
	private StateHandlerService stateHandler;
	@Value("${packageType.exclusions}")
	private String packageTypeToExclude;

	@Autowired
	public PackageService(PackageFileHandler packageFileHandler, FilePathHelper filePathHelper,
						  CustomPackageRepository packageRepository, StateHandlerService stateHandler, DmdService dmdService, LoggingService logger) {
		this.filePathHelper = filePathHelper;
		this.packageFileHandler = packageFileHandler;
		this.packageRepository = packageRepository;
		this.stateHandler = stateHandler;
		this.dmdService = dmdService;
		this.logger = logger;
	}

	public List<PackageView> findAllPackages() throws JSONException, IOException {
		List<JSONObject> jsons = packageRepository.findAll();
		List<PackageView> packageViews = new ArrayList<>();
		Map<String, State> stateMap = stateHandler.getState();
		for (JSONObject packageToCheck : jsons) {
			PackageView packageView = new PackageView(packageToCheck);
			String packageId = packageToCheck.getString("_id");
			packageView.setState(stateMap.get(packageId));
			//packageView.setGlobusMoveStatus(dmdService.getPackageStatus(packageId));
			packageViews.add(packageView);
		}
		return packageViews;
	}

	public List<PackageView> findMostPackages() throws JSONException, IOException {
		List<JSONObject> jsons = packageRepository.findAll();
		List<PackageView> packageViews = new ArrayList<>();
		Map<String, State> stateMap = stateHandler.getState();
		for (JSONObject packageToCheck : jsons) {
			String packageType = packageToCheck.getString("packageType");
			if (!packageTypeToExclude.equalsIgnoreCase(packageType)) {
				PackageView packageView = new PackageView(packageToCheck);
				String packageId = packageToCheck.getString("_id");
				packageView.setState(stateMap.get(packageId));
				packageViews.add(packageView);
			}
		}
		return packageViews;
	}

	public String savePackageInformation(JSONObject packageMetadata, User user, String packageId) throws JSONException {
		packageRepository.saveDynamicForm(packageMetadata, user, packageId);
		Package myPackage = packageRepository.findByPackageId(packageId);
		// Remove DMD code for now
		// dmdService.convertAndSendNewPackage(myPackage);
		return packageId;
	}

	public Package findPackage(String packageId) {
		return packageRepository.findByPackageId(packageId);
	}

	public void saveFile(MultipartFile file, String packageId, String filename, String study, boolean shouldAppend) throws Exception {

		if (filename.equalsIgnoreCase("metadata.json")) {
			filename = filename.replace(".", "_user.");
		}
		packageFileHandler.saveMultipartFile(file, packageId, filename, study, shouldAppend);
	}
	private double calculateUploadRate(long duration, List<Attachment> attachments) {
		double fileSizeInMeg = calculateFileSizeInMeg(attachments);
		return (double) fileSizeInMeg / duration;
	}

	private long calculateDurationInSeconds(Date startTime, Date endTime) {
		LocalDateTime start = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault());
		LocalDateTime end = LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.systemDefault());
		return ChronoUnit.SECONDS.between(start, end);
	}

	private long getTotalSizeOfAttachmentsInBytes(List<Attachment> attachments) {
		long totalSize = 0;
		for (Attachment attachment : attachments) {
			totalSize += attachment.getSize();
		}
		return totalSize;
	}

	private double calculateFileSizeInMeg(List<Attachment> attachments) {
		long totalSize = getTotalSizeOfAttachmentsInBytes(attachments);
		long megabyteValue = 1024L * 1024L;
		return (double) totalSize / megabyteValue;
	}

	public boolean validatePackage(String packageId, User user) {
		Package packageInformation = findPackage(packageId);
		String packagePath = filePathHelper.getPackagePath(packageInformation.getPackageId(), packageInformation.getStudy());
		List<String> filesOnDisk = filePathHelper.getFilenames(packagePath);
		List<String> filesInPackage = getAttachmentFilenames(packageInformation);
		Collections.sort(filesOnDisk);
		Collections.sort(filesInPackage);
		return checkFilesExist(filesOnDisk, filesInPackage, packageId, user)
				&& validateFileLengthsMatch(packageInformation.getAttachments(), packagePath, packageId, user);
	}

	public void calculateAndSaveChecksums(String packageId) throws IOException {
		Package myPackage = packageRepository.findByPackageId(packageId);
		List<Attachment> updatedFiles = calculateChecksums(myPackage);
		myPackage.setAttachments(updatedFiles);
		// dmdService.sendPackageFiles(myPackage);
		packageRepository.updateField(packageId, "files", updatedFiles);
	}

	public List<Attachment> calculateChecksums(Package myPackage) throws IOException {
		List<Attachment> files = myPackage.getAttachments();
		String packageID = myPackage.getPackageId();
        String study = myPackage.getStudy();
		if (files.size() > 0) {
			for (Attachment file : files) {
				if (file.getMd5checksum() == null) {
					String filePath = filePathHelper.getFilePath(packageID, study, file.getFileName());
					InputStream is = Files.newInputStream(Paths.get(filePath));
					String md5 = DigestUtils.md5Hex(is);
					file.setMd5checksum(md5);
				} else {
					logger.logInfoMessage(PackageService.class, null, packageID,
							PackageService.class.getSimpleName() + ".calculateFileChecksums",
							packageIssue.format(new Object[] { "Checksum already exists for file " + file.getFileName(),
									packageID }));
				}
			}
		} else {
			logger.logInfoMessage(PackageService.class, null, packageID,
					PackageService.class.getSimpleName() + ".calculateFileChecksums",
					packageIssue.format(new Object[] { "No files found in this package", packageID }));
		}
		return files;
	}

	@CacheEvict(value = "packages", allEntries = true)
	public void sendStateChangeEvent(String packageId, String stateString, String largeFilesChecked, String origin) {
		stateHandler.sendStateChange(packageId, stateString, largeFilesChecked, null, origin);
	}

	@CacheEvict(value = "packages", allEntries = true)
	public void sendStateChangeEvent(String packageId, String stateString, String largeFilesChecked, String codicil,
			String origin) {
		stateHandler.sendStateChange(packageId, stateString, largeFilesChecked, codicil, origin);
	}

	protected boolean validateFileLengthsMatch(List<Attachment> filesInPackage, String packagePath, String packageId,
			User user) {
		boolean everythingMatches = true;
		for (Attachment attachment : filesInPackage) {
			String filename = attachment.getFileName();
			if (new File(packagePath + filename).length() != attachment.getSize()) {
				logger.logErrorMessage(this.getClass(), user, packageId,
						this.getClass().getSimpleName() + ".validateFileLengthsMatch", fileIssue.format(new Object[] {
								"File size in metadata does not match file size on disk for file: " + filename }));
				everythingMatches = false;
			}
		}
		return everythingMatches;
	}

	protected boolean checkFilesExist(List<String> filesOnDisk, List<String> filesInPackage, String packageId,
			User user) {
		boolean sameFiles = filesOnDisk.equals(filesInPackage);
		if (!sameFiles) {
			logger.logErrorMessage(this.getClass(), user, packageId,
					this.getClass().getSimpleName() + ".checkFilesExist",
					fileIssue.format(new Object[] { "File list in metadata does not match file list on disk" }));
		}
		return sameFiles;
	}
	private List<String> getAttachmentFilenames(Package packageInformation) {
		ArrayList<String> filenames = new ArrayList<>();
		List<Attachment> attachments = packageInformation.getAttachments();
		for (Attachment attachment : attachments) {
			filenames.add(attachment.getFileName());
		}
		return filenames;
	};

}
