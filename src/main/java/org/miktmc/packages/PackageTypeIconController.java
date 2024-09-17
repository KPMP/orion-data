package org.miktmc.packages;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.miktmc.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PackageTypeIconController {

	private PackageTypeIconRepository packageTypeIconRepository;
	private LoggingService loggingService;

	@Autowired
	public PackageTypeIconController(PackageTypeIconRepository packageTypeIconRepository,
			LoggingService loggingService) {
		this.packageTypeIconRepository = packageTypeIconRepository;
		this.loggingService = loggingService;
	}

	@RequestMapping(value = "/v1/packageTypeIcons", method = RequestMethod.GET)
	public @ResponseBody List<PackageTypeIcon> getAllPackageTypeIcons(HttpServletRequest request) {
		loggingService.logInfoMessage(this.getClass(), null, "Getting list of package type icons", request);

		return packageTypeIconRepository.findAll();
	}

}
