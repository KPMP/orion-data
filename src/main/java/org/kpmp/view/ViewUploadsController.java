package org.kpmp.view;

import java.util.ArrayList;
import java.util.List;

import org.kpmp.dao.FileSubmission;
import org.kpmp.dao.FileSubmissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ViewUploadsController {

	private FileSubmissionsRepository fileSubmissionsRepository;

	@Autowired
	public ViewUploadsController(FileSubmissionsRepository fileSubmissionsRepository) {
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

	public List<PackageView> getPackages() {
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
}
