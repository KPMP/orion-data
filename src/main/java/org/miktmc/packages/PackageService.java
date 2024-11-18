package org.miktmc.packages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.miktmc.logging.LoggingService;
import org.miktmc.users.User;
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
                        String command = "mogrify -strip " + path;
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
		return myPackage.getPackageId();
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

	public Boolean deleteFile(String packageId, String fileId, String shibId){
		Boolean fileFound = false;
		Package thePackage = findPackage(packageId);
		List<Attachment> files = thePackage.getAttachments();
		Attachment theFile = null;
		// Because we have to modify it, use an iterator.
		Iterator<Attachment> i = files.iterator();
		while (i.hasNext()) {
			Attachment file = i.next();
			if (file.getId().equals(fileId)) {
				theFile = file;
				i.remove();
				fileFound = true;
			}
		}
		if (fileFound) {
			packageRepository.updateField(packageId, "files", files);
			packageRepository.addModification(packageId, shibId, "DELETE");
			String filePath = filePathHelper.getFilePath(packageId, thePackage.getStudy(), theFile.getFileName());
			System.out.println(filePath);
			File file = new File(filePath);
			file.delete();
		}
		return fileFound;
	}

	public List<Attachment> addFiles(String packageId, JSONArray newFiles, String shibId, Boolean replaced){
		Package thePackage = findPackage(packageId);
		List<Attachment> files = thePackage.getAttachments();
		List<String> filenames = thePackage.getOriginalFilenames();
		// Remove any files that have already been uploaded.
		for (int i=0; i < newFiles.length(); i++) {
			JSONObject file = newFiles.getJSONObject(i);
			String originalFileName = file.getString(PackageKeys.FILE_NAME.getKey());
			if (filenames.contains(originalFileName)) {
				newFiles.remove(i);
			}
		}
		packageRepository.setRenamedFiles(newFiles, thePackage.getStudy(), thePackage.getBiopsyId());
		for (int i=0; i < newFiles.length(); i++) {
			JSONObject file = newFiles.getJSONObject(i);
			Attachment newFile = new Attachment();
			newFile.setFileName((String) file.get(PackageKeys.FILE_NAME.getKey()));
			newFile.setOriginalFileName((String) file.get(PackageKeys.ORIGINAL_FILE_NAME.getKey()));
			newFile.setId((String) file.get(PackageKeys.ID.getKey()));
			newFile.setSize((Integer) file.get(PackageKeys.SIZE.getKey()));
			if (replaced) {
				newFile.setReplacedOn(new Date());
			}
			files.add(newFile);
		}
		packageRepository.updateField(packageId, "files", files);
		packageRepository.addModification(packageId, shibId, "ADD");
		return files;
	}

	public boolean validatePackage(String packageId, User user) {
		Package packageInformation = findPackage(packageId);
		List<Attachment> filesNoChecksums = new ArrayList<>();
		List<String> filesNamesNoChecksums = new ArrayList<>();
		List<String> filesNamesChecksums = new ArrayList<>();

		// We only want to check new files, i.e. those without checksums
		for (Attachment file: packageInformation.getAttachments()) {
			if (file.getMd5checksum() == null) {
				filesNoChecksums.add(file);
				filesNamesNoChecksums.add(file.getFileName());
			} else {
				filesNamesChecksums.add(file.getFileName());
			}
		}
		String packagePath = filePathHelper.getPackagePath(packageInformation.getPackageId(), packageInformation.getStudy());
		List<String> filesOnDisk = filePathHelper.getFilenames(packagePath);
		filesOnDisk.removeAll(filesNamesChecksums);
		Collections.sort(filesOnDisk);
		Collections.sort(filesNamesNoChecksums);
		return checkFilesExist(filesOnDisk, filesNamesNoChecksums, packageId, user)
				&& validateFileLengthsMatch(filesNoChecksums, packagePath, packageId, user);
	}

	public void calculateAndSaveChecksums(String packageId) throws IOException {
		Package myPackage = packageRepository.findByPackageId(packageId);
		List<Attachment> updatedFiles = calculateChecksums(packageId);
		myPackage.setAttachments(updatedFiles);
		packageRepository.updateField(packageId, "files", updatedFiles);
	}

	public List<Attachment> calculateChecksums(String packageId) throws IOException {
        Package myPackage = packageRepository.findByPackageId(packageId);
		List<Attachment> files = myPackage.getAttachments();
		String packageID = myPackage.getPackageId();
        String study = myPackage.getStudy();
        InputStream is = null;
		if (files.size() > 0) {
			for (Attachment file : files) {
				if (file.getMd5checksum() == null) {
                    try{
                        String filePath = filePathHelper.getFilePath(packageID, study, file.getFileName());
                        Path path = Path.of(filePath);
                        if(path == null || !Files.exists(path)){
                            logger.logErrorMessage(PackageService.class, null, packageID,
                                PackageService.class.getSimpleName() + ".calculateChecksums", "Could not find file in " + filePath);
                        }else{
                            try {
                                is = Files.newInputStream(Path.of(filePath));
                                String md5 = DigestUtils.md5Hex(is);
                                file.setMd5checksum(md5); 
                            }catch(IOException e){
                                logger.logErrorMessage(PackageService.class, null, packageID,
                                    PackageService.class.getSimpleName() + ".calculateChecksums",
                                    "Error processing file at " + filePath + ": " + e.getMessage());
                            }
                        }
                    }
                    finally{
                        try {
                            if(is != null){
                                is.close();
                            }
                        }catch(IOException e){
                            logger.logErrorMessage(PackageService.class, null, packageID,
                                PackageService.class.getSimpleName() + ".calculateChecksums",
                                "There was a problem closing the InputStream after calculating checksum for file "
                                + file.getFileName() + ": " + e.getMessage());
                        }
                    }
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
					fileIssue.format(new Object[] { "File list in metadata does not match file list on disk: " + String.join(",", filesInPackage) + " vs " +  String.join(",", filesOnDisk)}));
		}
		return sameFiles;
	}

	public Boolean canReplaceFile(String packageId, String fileId, String originalFileName) {
		List<Attachment> files = findPackage(packageId).getAttachments();
		boolean canReplace = true;
		for (Attachment attachment : files) {
			// It's okay to replace a file with the same name if it's the one being replaced.
			if (attachment.getOriginalFileName().equals(originalFileName)) {
				canReplace = Objects.equals(attachment.getId(), fileId);
			}
		}
		return canReplace;
	}

}
