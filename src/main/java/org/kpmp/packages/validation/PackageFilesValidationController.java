package org.kpmp.packages.validation;

import jakarta.servlet.http.HttpServletRequest;

import org.kpmp.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PackageFilesValidationController {

	private PackageFilesValidationService service;
	private LoggingService logger;

	@Autowired
	public PackageFilesValidationController(PackageFilesValidationService service, LoggingService logger) {
		this.service = service;
		this.logger = logger;
	}

	@RequestMapping(value = "/v1/package/files/validation", method = RequestMethod.POST)
	public @ResponseBody PackageValidationResponse validateFilesInPackage(@RequestBody PackageFilesRequest packageFiles,
			HttpServletRequest request) throws Exception {
		logger.logInfoMessage(this.getClass(), packageFiles.getPackageId(), "Checking files match", request);
		try {
			return service.matchFiles(packageFiles);
		} catch (Exception exception) {
			logger.logErrorMessage(this.getClass(), packageFiles.getPackageId(),
					"Unable to validate package files: " + exception.getMessage(), request);
			throw exception;
		}
	}

}
