package org.kpmp.packages.validation;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PackageFilesValidationController {

	private PackageFilesValidationService service;

	@Autowired
	public PackageFilesValidationController(PackageFilesValidationService service) {
		this.service = service;
	}

	@RequestMapping(value = "/v1/package/files/validation", method = RequestMethod.POST)
	public @ResponseBody PackageValidationResponse validateFilesInPackage(@RequestBody PackageFilesRequest packageFiles,
			HttpServletRequest request) throws JSONException, IOException {
		return service.matchFiles(packageFiles);
	}

}
