package org.kpmp.packages;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PackageService {

	private PackageRepository packageRepository;

	@Autowired
	public PackageService(PackageRepository packageRepository) {
		this.packageRepository = packageRepository;
	}

	public List<Package> findAllPackages() {
		return packageRepository.findAll();
	}

}
