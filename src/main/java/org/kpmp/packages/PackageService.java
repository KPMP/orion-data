package org.kpmp.packages;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PackageService {

	private PackageRepository packageRepository;
	private FilePathHelper filePathHelper;

	@Autowired
	public PackageService(PackageRepository packageRepository, FilePathHelper filePathHelper) {
		this.packageRepository = packageRepository;
		this.filePathHelper = filePathHelper;
	}

	public List<Package> findAllPackages() {
		return packageRepository.findAll();
	}

	public Path getPackageFile(String packageId) {
		String packagePath = filePathHelper.getPackagePath("", packageId);
		Path filePath = Paths.get(packagePath, packageId + ".zip");
		if (!filePath.toFile().exists()) {
			throw new RuntimeException("The file was not found: " + filePath.getFileName().toString());
		}
		return filePath;
	}

}
