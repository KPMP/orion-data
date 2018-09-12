package org.kpmp.packages;

import java.util.Date;
import java.util.List;

import org.kpmp.UniversalIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PackageService {

	private PackageRepository packageRepository;
	private UniversalIdGenerator universalIdGenerator;

	@Autowired
	public PackageService(PackageRepository packageRepository, UniversalIdGenerator universalIdGenerator) {
		this.packageRepository = packageRepository;
		this.universalIdGenerator = universalIdGenerator;
	}

	public List<Package> findAllPackages() {
		return packageRepository.findAll();
	}

	public Package savePackageInformation(Package packageInfo) {
		packageInfo.setPackageId(universalIdGenerator.generateUniversalId());
		packageInfo.setCreatedAt(new Date());
		Package savedPackage = packageRepository.save(packageInfo);
		return savedPackage;
	}

}
