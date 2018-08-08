package org.kpmp.view;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.kpmp.dao.FileSubmission;
import org.kpmp.dao.FileSubmissionsRepository;
import org.kpmp.dao.UploadPackage;
import org.kpmp.upload.FilePathHelper;
import org.kpmp.upload.UploadPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ViewUploadsController {

	private FileSubmissionsRepository fileSubmissionsRepository;
	private UploadPackageRepository uploadPackageRepository;
	private FilePathHelper filePathHelper;

	@Autowired
	public ViewUploadsController(FileSubmissionsRepository fileSubmissionsRepository,
			UploadPackageRepository uploadPackageRepository, FilePathHelper filePathHelper) {
		this.fileSubmissionsRepository = fileSubmissionsRepository;
		this.uploadPackageRepository = uploadPackageRepository;
		this.filePathHelper = filePathHelper;
	}

	@RequestMapping(value = "/viewUploads", method = RequestMethod.GET)
	public @ResponseBody List<PackageView> getPackages() {
		List<PackageView> packages = new ArrayList<>();
		List<Integer> packageIds = new ArrayList<>();
		List<FileSubmission> submissions = fileSubmissionsRepository.findAllByOrderByCreatedAtDesc();
		for (FileSubmission fileSubmission : submissions) {
			int packageId = fileSubmission.getUploadPackage().getId();
			if (packageIds.contains(packageId)) {
				continue;
			}
			PackageView packageView = new PackageView(fileSubmission);
			packages.add(packageView);
			packageIds.add(packageId);
		}
		return packages;
	}

	@RequestMapping(value = "/download/{packageId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Resource> downloadPackage(@PathVariable int packageId)
			throws MalformedURLException {
		UploadPackage uploadPackage = uploadPackageRepository.findById(packageId);
		String uuid = uploadPackage.getUniversalId();
		System.err.println(uuid);
		System.err.println(uploadPackage.getFileSubmissions().get(0).getSubmitter().getFirstName());
		String packagePath = filePathHelper.getPackagePath("", Integer.toString(packageId));
		System.err.println(packagePath);
		Path filePath = Paths.get(packagePath, uuid + ".zip");
		System.err.println(filePath.getFileName());
		Resource resource = new UrlResource(filePath.toUri());
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}
