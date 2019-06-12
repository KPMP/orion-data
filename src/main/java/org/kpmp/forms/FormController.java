package org.kpmp.forms;

import javax.servlet.http.HttpServletRequest;

import org.kpmp.ApplicationConstants;
import org.kpmp.JWTHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FormController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private FormRepository repository;
	private JWTHandler jwtHandler;

	@Autowired
	public FormController(FormRepository repository, JWTHandler jwtHandler) {
		this.repository = repository;
		this.jwtHandler = jwtHandler;
	}

	@RequestMapping(value = "/v1/form", method = RequestMethod.GET)
	public @ResponseBody Form getFormDTD(HttpServletRequest request) {
		String userId = jwtHandler.getUserIdFromHeader(request);
		log.info(ApplicationConstants.LOG_MESSAGE_FORMAT, userId, null, request.getRequestURI(),
				"Request for all forms");
		return this.repository.findTopByOrderByVersionDesc();
	}

	@RequestMapping(value = "/v1/form/version/{version}", method = RequestMethod.GET)
	public @ResponseBody Form getFormDTD(@PathVariable Double version, HttpServletRequest request) {
		String userId = jwtHandler.getUserIdFromHeader(request);
		log.info(ApplicationConstants.LOG_MESSAGE_FORMAT, userId, null, request.getRequestURI(),
				"Request for form with version: " + version);
		return this.repository.findByVersion(version).get(0);
	}

}
