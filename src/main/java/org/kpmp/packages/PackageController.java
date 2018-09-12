package org.kpmp.packages;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PackageController {

	private PackageService packageService;

	@Autowired
	public PackageController(PackageService packageService) {
		this.packageService = packageService;
	}

	@RequestMapping(value = "/v1/packages", method = RequestMethod.GET)
	public @ResponseBody List<Package> getAllPackages() {
		System.err.println("in get");
		return packageService.findAllPackages();
	}

	@RequestMapping(value = "/v1/packages", method = RequestMethod.POST)
	public @ResponseBody String postPackageInfo(@RequestBody Package packageInfo) {
		System.err.println("in post");
		System.err.println(packageInfo);
		Package savedPackage = packageService.savePackageInformation(packageInfo);
		return savedPackage.getPackageId();
	}

	@RequestMapping(value = "/v1/packages/{packageId}/files", method = RequestMethod.POST)
	public @ResponseBody String postFilesToPackage(@PathVariable("packageId") String packageId) {
		// look up package based on id
		// add file to file-list
		return null;
	}
}
