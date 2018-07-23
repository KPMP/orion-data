package org.kpmp.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.kpmp.dao.FileSubmission;
import org.kpmp.dao.FileSubmissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ViewController {

	private FileSubmissionsRepository fileSubmissionsRepository;

	@Autowired
	public ViewController(FileSubmissionsRepository fileSubmissionsRepository) {
		this.fileSubmissionsRepository = fileSubmissionsRepository;
	}

	@RequestMapping(value = "/viewUploads", method = RequestMethod.GET)
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
	public @ResponseBody List<UploadPackageInfo> getUploadPackages() {

		UploadPackageInfo package1 = new UploadPackageInfo("987987", "mRNA", new Date(), "Joe Schmoe", "Michigan");
		UploadPackageInfo package2 = new UploadPackageInfo("98fsfsd7987", "mRNA stuff", new Date(), "Joe Schmoe, Jr.",
				"Michigan");
		return Arrays.asList(package1, package2);
	}

}
