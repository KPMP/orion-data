package org.kpmp.shibboleth;

import java.io.UnsupportedEncodingException;

import jakarta.servlet.http.HttpServletRequest;

import org.kpmp.logging.LoggingService;
import org.kpmp.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ShibbolethAttributeController {

	private ShibbolethUserService shibbolethUserService;
	private LoggingService logger;

	@Autowired
	public ShibbolethAttributeController(ShibbolethUserService shibbolethUserService, LoggingService logger) {
		this.shibbolethUserService = shibbolethUserService;
		this.logger = logger;
	}

	@RequestMapping(value = "/v1/userInformation", method = RequestMethod.GET)
	public @ResponseBody User getAttributes(HttpServletRequest request) throws UnsupportedEncodingException {

		logger.logInfoMessage(this.getClass(), null, "Retrieving user information from shibboleth", request);

		User user = shibbolethUserService.getUser(request);
		return user;
	}
}
