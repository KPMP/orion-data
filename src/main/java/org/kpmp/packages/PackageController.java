package org.kpmp.packages;

import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.kpmp.UniversalIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PackageController {

	private PackageService packageService;
	private UniversalIdGenerator idGenerator;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final MessageFormat packageInfoPost = new MessageFormat("Request|{0}|{1}");
	private static final MessageFormat finish = new MessageFormat("Request|{0}|{1}");
	private static final MessageFormat fileUploadRequest = new MessageFormat("Request|{0}|{1}|{2}|{3}|{4}|{5}");
	private static final MessageFormat fileDownloadRequest = new MessageFormat("Request|{0}|{1}");
	private static final MessageFormat fileUploadStartTiming = new MessageFormat("Timing|start|{0}|{1}|{2}|{3} files");
	private static final MessageFormat fileUploadFinishTiming = new MessageFormat(
			"Timing|end|{0}|{1}|{2}|{3} files|{4}|{5}|{6}");
	private static final MessageFormat zipTiming = new MessageFormat("Timing|zip|{0}|{1}|{2}|{3} files|{4}|{5}");

	@Autowired
	public PackageController(PackageService packageService, UniversalIdGenerator idGenerator) {
		this.packageService = packageService;
		this.idGenerator = idGenerator;

	}

	@RequestMapping(value = "/v1/packages", method = RequestMethod.GET)
	public @ResponseBody List<PackageView> getAllPackages() {
		return packageService.findAllPackages();
	}

	@RequestMapping(value = "/v1/packages", method = RequestMethod.POST)
	public @ResponseBody String postPackageInfo(@RequestBody Package packageInfo) {
		Date startTime = new Date();
		String packageId = idGenerator.generateUniversalId();
		packageInfo.setPackageId(packageId);
		packageInfo.setCreatedAt(startTime);
		log.info(packageInfoPost.format(new Object[] { "postPackageInfo", packageInfo }));
		log.info(fileUploadStartTiming.format(new Object[] { startTime, packageInfo.getSubmitter().getEmail(),
				packageId, packageInfo.getAttachments().size() }));

		Package savedPackage = packageService.savePackageInformation(packageInfo);
		return savedPackage.getPackageId();
	}

	@RequestMapping(value = "/v1/packages/{packageId}/files", method = RequestMethod.POST, consumes = {
			"multipart/form-data" })
	public @ResponseBody FileUploadResponse postFilesToPackage(@PathVariable("packageId") String packageId,
			@RequestParam("qqfile") MultipartFile file, @RequestParam("qqfilename") String filename,
			@RequestParam("qqtotalfilesize") long fileSize,
			@RequestParam(name = "qqtotalparts", defaultValue = "1") int chunks,
			@RequestParam(name = "qqpartindex", defaultValue = "0") int chunk) throws Exception {

		log.info(fileUploadRequest
				.format(new Object[] { "postFilesToPackage", filename, packageId, fileSize, chunks, chunk }));

		packageService.saveFile(file, packageId, filename, shouldAppend(chunk));

		return new FileUploadResponse(true);
	}

	@RequestMapping(value = "/v1/packages/{packageId}/files", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Resource> downloadPackage(@PathVariable String packageId) {
		Resource resource = null;
		try {
			resource = new UrlResource(packageService.getPackageFile(packageId).toUri());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		log.info(fileDownloadRequest.format(new Object[] { packageId, resource.toString() }));
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@RequestMapping(value = "/v1/packages/{packageId}/files/finish", method = RequestMethod.POST)
	public @ResponseBody FileUploadResponse finishUpload(@PathVariable("packageId") String packageId) {
		Package packageInformation = packageService.findPackage(packageId);
		List<Attachment> attachments = packageInformation.getAttachments();
		String displaySize = FileUtils.byteCountToDisplaySize(getTotalSizeOfAttachmentsInBytes(attachments));
		Date finishUploadTime = new Date();
		long duration = calculateDurationInSeconds(packageInformation.getCreatedAt(), finishUploadTime);
		double uploadRate = calculateUploadRate(duration, attachments);
		DecimalFormat rateFormat = new DecimalFormat("###.###");

		log.info(finish.format(new Object[] { "finishUpload", packageId }));
		log.info(fileUploadFinishTiming.format(new Object[] { finishUploadTime,
				packageInformation.getSubmitter().getEmail(), packageId, attachments.size(), displaySize,
				duration + " seconds", rateFormat.format(uploadRate) + " MB/sec" }));

		packageService.createZipFile(packageId);

		long zipDuration = calculateDurationInSeconds(finishUploadTime, new Date());
		log.info(zipTiming.format(new Object[] { packageInformation.getCreatedAt(),
				packageInformation.getSubmitter().getEmail(), packageId, packageInformation.getAttachments().size(),
				displaySize, zipDuration + " seconds" }));
		return new FileUploadResponse(true);
	}

	private boolean shouldAppend(int chunk) {
		return chunk != 0;
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

}
