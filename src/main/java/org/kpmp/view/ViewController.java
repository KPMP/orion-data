package org.kpmp.view;

import java.util.ArrayList;
import java.util.List;

import org.kpmp.dao.UploadPackageRepository;
import org.kpmp.dao.deprecated.FileSubmission;
import org.kpmp.dao.deprecated.FileSubmissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ViewController {

	private FileSubmissionsRepository fileSubmissionsRepository;
	private UploadPackageRepository uploadPackageRepository;

	@Autowired
	public ViewController(FileSubmissionsRepository fileSubmissionsRepository,
			UploadPackageRepository uploadPackageRepository) {
		this.fileSubmissionsRepository = fileSubmissionsRepository;
		this.uploadPackageRepository = uploadPackageRepository;
	}

	@RequestMapping(value = "/uploader/viewFiles", method = RequestMethod.GET)
	public @ResponseBody List<FileUpload> getFileUploads() {
		List<FileUpload> files = new ArrayList<>();
		List<FileSubmission> submissions = fileSubmissionsRepository.findAllByOrderByCreatedAtDesc();
		for (FileSubmission fileSubmission : submissions) {
			FileUpload file = new FileUpload(fileSubmission);
			files.add(file);
		}
		return files;
	}

	@RequestMapping(value = "/v1/packages", method = RequestMethod.GET)
	public @ResponseBody List<UploadPackage> getUploadPackages() {
		return uploadPackageRepository.findAll();
	}

}
