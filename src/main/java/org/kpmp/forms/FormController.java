package org.kpmp.forms;

import javax.servlet.http.HttpServletRequest;

import org.kpmp.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FormController {

	private FormRepository repository;
	private LoggingService logger;

	@Autowired
	public FormController(FormRepository repository, LoggingService logger) {
		this.repository = repository;
		this.logger = logger;
	}

	@RequestMapping(value = "/v1/form", method = RequestMethod.GET)
	public @ResponseBody Form getFormDTD(HttpServletRequest request) {
		logger.logInfoMessage(this.getClass(), null, null, request.getRequestURI(), "Request for all forms");
		return this.repository.findTopByOrderByVersionDesc();
	}

	@RequestMapping(value = "/v1/form/version/{version}", method = RequestMethod.GET)
	public @ResponseBody Form getFormDTD(@PathVariable Double version, HttpServletRequest request) {
		logger.logInfoMessage(this.getClass(), null, null, request.getRequestURI(),
				"Request for form with version: " + version);
		return this.repository.findByVersion(version).get(0);
	}

}
