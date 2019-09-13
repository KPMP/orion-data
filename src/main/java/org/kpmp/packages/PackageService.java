package org.kpmp.packages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.kpmp.externalProcess.CommandBuilder;
import org.kpmp.externalProcess.ProcessExecutor;
import org.kpmp.logging.LoggingService;
import org.kpmp.packages.state.State;
import org.kpmp.packages.state.StateHandlerService;
import org.kpmp.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PackageService {

	@Value("${package.state.upload.succeeded}")
	private String uploadSucceededState;

	private static final MessageFormat zipPackage = new MessageFormat("{0} {1}");
	private static final MessageFormat fileUploadFinishTiming = new MessageFormat(
			"Timing|end|{0}|{1}|{2}|{3} files|{4}|{5}|{6}");
	private static final MessageFormat zipTiming = new MessageFormat("Timing|zip|{0}|{1}|{2}|{3} files|{4}|{5}");
	private static final MessageFormat zipIssue = new MessageFormat("ERROR|zip|{0}");

	private PackageFileHandler packageFileHandler;
	private FilePathHelper filePathHelper;
	private CustomPackageRepository packageRepository;
	private LoggingService logger;
	private StateHandlerService stateHandler;
	private CommandBuilder commandBuilder;
	private ProcessExecutor processExecutor;

	@Autowired
	public PackageService(PackageFileHandler packageFileHandler, FilePathHelper filePathHelper,
			CustomPackageRepository packageRepository, StateHandlerService stateHandler, CommandBuilder commandBuilder,
			ProcessExecutor processExecutor, LoggingService logger) {
		this.filePathHelper = filePathHelper;
		this.packageFileHandler = packageFileHandler;
		this.packageRepository = packageRepository;
		this.stateHandler = stateHandler;
		this.commandBuilder = commandBuilder;
		this.processExecutor = processExecutor;
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
			String zipFileName = filePathHelper.getZipFileName(packageId);
			if (new File(zipFileName).exists()) {
				packageView.setIsDownloadable(true);
			} else {
				packageView.setIsDownloadable(false);
			}
			packageViews.add(packageView);
		}
		return packageViews;
	}

	public Path getPackageFile(String packageId) {
		String zipFileName = filePathHelper.getZipFileName(packageId);
		Path filePath = Paths.get(zipFileName);
		if (!filePath.toFile().exists()) {
			throw new RuntimeException("The file was not found: " + filePath.getFileName().toString());
		}
		return filePath;
	}

	public String savePackageInformation(JSONObject packageMetadata, User user, String packageId) throws JSONException {
		packageRepository.saveDynamicForm(packageMetadata, user, packageId);
		return packageId;
	}

	public Package findPackage(String packageId) {
		return packageRepository.findByPackageId(packageId);
	}

	public void saveFile(MultipartFile file, String packageId, String filename, boolean shouldAppend) throws Exception {

		if (filename.equalsIgnoreCase("metadata.json")) {
			filename = filename.replace(".", "_user.");
		}
		packageFileHandler.saveMultipartFile(file, packageId, filename, shouldAppend);
	}

	public void createZipFile(String packageId, String origin, User user) throws Exception {

		Package packageInfo = packageRepository.findByPackageId(packageId);

		List<Attachment> attachments = packageInfo.getAttachments();
		String displaySize = FileUtils.byteCountToDisplaySize(getTotalSizeOfAttachmentsInBytes(attachments));
		Date finishUploadTime = new Date();
		long duration = calculateDurationInSeconds(packageInfo.getCreatedAt(), finishUploadTime);
		double uploadRate = calculateUploadRate(duration, attachments);
		DecimalFormat rateFormat = new DecimalFormat("###.###");

		logger.logInfoMessage(this.getClass(), user, packageId, this.getClass().getSimpleName() + ".createZipFile",
				fileUploadFinishTiming
						.format(new Object[] { finishUploadTime, user.toString(), packageId, attachments.size(),
								displaySize, duration + " seconds", rateFormat.format(uploadRate) + " MB/sec" }));

		new Thread() {
			public void run() {
				try {
					String packageMetadata = packageRepository.getJSONByPackageId(packageId);
					String[] zipCommand = commandBuilder.buildZipCommand(packageId, packageMetadata);
					boolean success = processExecutor.executeProcess(zipCommand);
					if (success) {
						logger.logInfoMessage(PackageService.class, null, packageId,
								PackageService.class.getSimpleName() + ".createZipFile",
								zipPackage.format(new Object[] { "Zip file created for package: ", packageId }));
						long zipDuration = calculateDurationInSeconds(finishUploadTime, new Date());
						logger.logInfoMessage(PackageService.class, user, packageId,
								PackageService.class.getSimpleName() + ".createZipFile",
								zipTiming.format(new Object[] { packageInfo.getCreatedAt(), user.toString(), packageId,
										packageInfo.getAttachments().size(), displaySize, zipDuration + " seconds" }));

						stateHandler.sendStateChange(packageId, uploadSucceededState);

						stateHandler.sendNotification(packageId, packageInfo.getPackageType(),
								packageInfo.getCreatedAt(), packageInfo.getSubmitter().getFirstName(),
								packageInfo.getSubmitter().getLastName(), packageInfo.getSubjectId(), origin);
					} else {
						logger.logErrorMessage(PackageService.class, user, packageId,
								PackageService.class.getSimpleName(), "Unable to zip package");
					}
				} catch (Exception e) {
					logger.logErrorMessage(PackageService.class, user, packageId, PackageService.class.getSimpleName(),
							e.getMessage());
				}
			}

		}.start();
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

	public boolean validatePackageForZipping(String packageId, User user) {
		Package packageInformation = findPackage(packageId);
		String packagePath = filePathHelper.getPackagePath(packageInformation.getPackageId());
		List<String> filesOnDisk = filePathHelper.getFilenames(packagePath);
		List<String> filesInPackage = getAttachmentFilenames(packageInformation);
		Collections.sort(filesOnDisk);
		Collections.sort(filesInPackage);
		return checkFilesExist(filesOnDisk, filesInPackage, packageId, user)
				&& validateFileLengthsMatch(packageInformation.getAttachments(), packagePath, packageId, user);
	}

	public void sendStateChangeEvent(String packageId, String stateString, String codicil) {
		stateHandler.sendStateChange(packageId, stateString, codicil);
	}

	protected boolean validateFileLengthsMatch(List<Attachment> filesInPackage, String packagePath, String packageId,
			User user) {
		boolean everythingMatches = true;
		for (Attachment attachment : filesInPackage) {
			String filename = attachment.getFileName();
			if (new File(packagePath + filename).length() != attachment.getSize()) {
				logger.logErrorMessage(this.getClass(), user, packageId,
						this.getClass().getSimpleName() + ".validateFileLengthsMatch", zipIssue.format(new Object[] {
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
					zipIssue.format(new Object[] { "File list in metadata does not match file list on disk" }));
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
