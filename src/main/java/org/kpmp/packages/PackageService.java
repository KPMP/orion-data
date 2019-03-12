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

import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kpmp.UniversalIdGenerator;
import org.kpmp.users.User;
import org.kpmp.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.DBRef;
import com.mongodb.client.MongoCollection;

@Service
public class PackageService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final MessageFormat zipPackage = new MessageFormat("Service|{0}|{1}");
	private static final MessageFormat fileUploadStartTiming = new MessageFormat("Timing|start|{0}|{1}|{2}|{3} files");
	private static final MessageFormat fileUploadFinishTiming = new MessageFormat(
			"Timing|end|{0}|{1}|{2}|{3} files|{4}|{5}|{6}");
	private static final MessageFormat zipTiming = new MessageFormat("Timing|zip|{0}|{1}|{2}|{3} files|{4}|{5}");

	private PackageRepository packageRepository;
	private UserRepository userRepository;
	private UniversalIdGenerator universalIdGenerator;
	private PackageFileHandler packageFileHandler;
	private PackageZipService packageZipper;
	private FilePathHelper filePathHelper;
	private MongoTemplate mongoTemplate;

	@Autowired
	public PackageService(PackageRepository packageRepository, UserRepository userRepository,
			UniversalIdGenerator universalIdGenerator, PackageFileHandler packageFileHandler,
			PackageZipService packageZipper, FilePathHelper filePathHelper, MongoTemplate mongoTemplate) {
		this.packageRepository = packageRepository;
		this.userRepository = userRepository;
		this.filePathHelper = filePathHelper;
		this.universalIdGenerator = universalIdGenerator;
		this.packageFileHandler = packageFileHandler;
		this.packageZipper = packageZipper;
		this.mongoTemplate = mongoTemplate;

	}

	public List<PackageView> findAllPackages() {
		List<Package> packages = packageRepository.findAll(new Sort(Sort.Direction.DESC, "createdAt"));
		List<PackageView> packageViews = new ArrayList<>();
		for (Package packageToCheck : packages) {
			PackageView packageView = new PackageView(packageToCheck);
			String zipFileName = filePathHelper.getZipFileName(packageToCheck.getPackageId());
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

	public String savePackageInformation(String packageInfo) throws JSONException {
		Date startTime = new Date();
		String packageId = universalIdGenerator.generateUniversalId();
		log.info("-----------" + packageInfo + "------------");
		JSONObject json = new JSONObject(packageInfo);
		JSONArray files = json.getJSONArray("files");
		for (int i = 0; i < files.length(); i++) {
			JSONObject file = files.getJSONObject(i);
			file.put("_id", universalIdGenerator.generateUniversalId());
		}
		json.put("_id", packageId);
		json.put("regenerateZip", false);
		String submitterEmail = json.getString("submitterEmail");
		User user = userRepository.findByEmail(submitterEmail);
		if (user == null) {
			User newUser = new User();
			JSONObject submitter = json.getJSONObject("submitter");
			newUser.setDisplayName(submitter.getString("displayName"));
			newUser.setEmail(submitter.getString("email"));
			newUser.setFirstName(submitter.getString("firstName"));
			newUser.setLastName(submitter.getString("lastName"));
			user = userRepository.save(newUser);
			log.info("created new user");
		}
		json.remove("submitter");
		json.remove("submitterFirstName");
		json.remove("submitterLastName");
		json.remove("submitterEmail");

		DBRef userRef = new DBRef("users", new ObjectId(user.getId()));
		String jsonString = json.toString();

		Document document = Document.parse(jsonString);
		document.put("submitter", userRef);
		document.put("createdAt", startTime);

		MongoCollection<Document> collection = mongoTemplate.getCollection("packages");
		collection.insertOne(document);

		return packageId;
	}

	public Package savePackageInformation(Package packageInfo) {
		Date startTime = new Date();
		String packageId = universalIdGenerator.generateUniversalId();
		packageInfo.setPackageId(packageId);
		packageInfo.setCreatedAt(startTime);
		log.info(fileUploadStartTiming.format(new Object[] { startTime, packageInfo.getSubmitter().getEmail(),
				packageId, packageInfo.getAttachments().size() }));

		List<Attachment> attachments = packageInfo.getAttachments();
		for (Attachment attachment : attachments) {
			attachment.setId(universalIdGenerator.generateUniversalId());
		}
		User user = userRepository.findByEmail(packageInfo.getSubmitter().getEmail());
		if (user == null) {
			user = userRepository.save(packageInfo.getSubmitter());
		}
		packageInfo.setSubmitter(user);
		Package savedPackage = packageRepository.save(packageInfo);
		return savedPackage;
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

	public void createZipFile(String packageId) {

		Package packageInfo = packageRepository.findByPackageId(packageId);
		List<Attachment> attachments = packageInfo.getAttachments();
		String displaySize = FileUtils.byteCountToDisplaySize(getTotalSizeOfAttachmentsInBytes(attachments));
		Date finishUploadTime = new Date();
		long duration = calculateDurationInSeconds(packageInfo.getCreatedAt(), finishUploadTime);
		double uploadRate = calculateUploadRate(duration, attachments);
		DecimalFormat rateFormat = new DecimalFormat("###.###");

		log.info(fileUploadFinishTiming.format(
				new Object[] { finishUploadTime, packageInfo.getSubmitter().getEmail(), packageId, attachments.size(),
						displaySize, duration + " seconds", rateFormat.format(uploadRate) + " MB/sec" }));

		new Thread() {
			public void run() {
				try {
					packageZipper.createZipFile(packageInfo);
				} catch (IOException e) {
					e.printStackTrace();
				}
				log.info(zipPackage.format(new Object[] { "createZipFile", packageId }));
				long zipDuration = calculateDurationInSeconds(finishUploadTime, new Date());
				log.info(zipTiming.format(
						new Object[] { packageInfo.getCreatedAt(), packageInfo.getSubmitter().getEmail(), packageId,
								packageInfo.getAttachments().size(), displaySize, zipDuration + " seconds" }));
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

	public Boolean checkFilesExist(Package packageInformation) {
		String packagePath = filePathHelper.getPackagePath(packageInformation.getPackageId());
		List<String> filesOnDisk = filePathHelper.getFilenames(packagePath);
		List<String> filesInPackage = getAttachmentFilenames(packageInformation);
		Collections.sort(filesOnDisk);
		Collections.sort(filesInPackage);
		return filesOnDisk.equals(filesInPackage);
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
