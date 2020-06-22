package org.kpmp.packages.validation;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.kpmp.globus.GlobusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PackageFilesValidationController {

	private GlobusService globus;

	@Autowired
	public PackageFilesValidationController(GlobusService globus) {
		this.globus = globus;
	}

	@RequestMapping(value = "/v1/packages/files/validation", method = RequestMethod.POST)
	public @ResponseBody String validateFilesInPackage(@RequestBody PackageFilesRequest packageFiles,
			HttpServletRequest request) throws JSONException, IOException {
		globus.getFilesAtEndpoint("0857a04e-9f44-4b8b-94d4-8c56c3902e29");
		return globus.getFilesAtEndpoint("0857a04e-9f44-4b8b-94d4-8c56c3902e29");
	}

}
