package org.kpmp.packages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;
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
    @Value("${file.base.path}")
    private String basePath;

	private static final MessageFormat packageIssue = new MessageFormat("{0} {1}");
	private static final MessageFormat fileIssue = new MessageFormat("ERROR|zip|{0}");
	private PackageFileHandler packageFileHandler;
	private FilePathHelper filePathHelper;
	private CustomPackageRepository packageRepository;
	private LoggingService logger;
	private StateHandlerService stateHandler;
	@Value("${packageType.exclusions}")
	private String packageTypeToExclude;

	@Autowired
	public PackageService(PackageFileHandler packageFileHandler, FilePathHelper filePathHelper,
						  CustomPackageRepository packageRepository, StateHandlerService stateHandler,
						  LoggingService logger) {
		this.filePathHelper = filePathHelper;
		this.packageFileHandler = packageFileHandler;
		this.packageRepository = packageRepository;
		this.stateHandler = stateHandler;
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

    public int stripMetadata(Package packageInformation) {
        List<Attachment> files = packageInformation.getAttachments();
        int successCode = 0;
        if (packageInformation != null && files != null && !files.isEmpty()){
            for (Attachment file : files){
                String ext = FilenameUtils.getExtension(file.toString());
                if (ext != null){
                    try {
                        String path = basePath + File.separator + packageInformation.getStudyFolderName() + File.separator + "package_" + packageInformation.getPackageId() + File.separator + file.getFileName();
                        String command = "exiftool -all= " + path;
                        Runtime runTime = Runtime.getRuntime();
                        runTime.exec(command);
                        successCode = 1;
                        String successMessage = packageIssue.format(new Object[] {"Successfully stripped metadata from file " + path, packageInformation.getPackageId()});
                        logger.logInfoMessage(this.getClass(), null, packageInformation.getPackageId(), "/v1/packages/" + packageInformation.getPackageId() + "/files/finish", successMessage);
                    } catch (Exception e) {
                        String errorMessage = packageIssue.format(new Object[] {"There was a problem stripping the metadata from file " + file, packageInformation.getPackageId()});
                        logger.logErrorMessage(this.getClass(), null, packageInformation.getPackageId(), "/v1/packages/" + packageInformation.getPackageId() + "/files/finish", errorMessage);
                        successCode = 0;
                    }
                }
            }
        }
        return successCode;
    }

	public String savePackageInformation(JSONObject packageMetadata, User user, String packageId) throws JSONException {
		packageRepository.saveDynamicForm(packageMetadata, user, packageId);
		Package myPackage = packageRepository.findByPackageId(packageId);
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
