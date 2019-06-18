package org.kpmp.packages;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kpmp.JWTHandler;
import org.kpmp.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PackageTypeIconController {

	private PackageTypeIconRepository packageTypeIconRepository;
	private JWTHandler jwtHandler;
	private LoggingService loggingService;

	@Autowired
	public PackageTypeIconController(PackageTypeIconRepository packageTypeIconRepository, JWTHandler jwtHandler,
			LoggingService loggingService) {
		this.packageTypeIconRepository = packageTypeIconRepository;
		this.jwtHandler = jwtHandler;
		this.loggingService = loggingService;
	}

	@RequestMapping(value = "/v1/packageTypeIcons", method = RequestMethod.GET)
	public @ResponseBody List<PackageTypeIcon> getAllPackageTypeIcons(HttpServletRequest request) {
		loggingService.logInfoMessage(this.getClass(), jwtHandler.getUserIdFromHeader(request), null,
				request.getRequestURI(), "Getting list of package type icons");

		return packageTypeIconRepository.findAll();
	}

}
