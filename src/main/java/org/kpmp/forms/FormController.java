package org.kpmp.forms;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

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
	private static final MessageFormat formRequest = new MessageFormat("Request|{0}");

	private FormRepository repository;

	@Autowired
	public FormController(FormRepository repository, JWTHandler jwtHandler) {
		this.repository = repository;
	}

	@RequestMapping(value = "/v1/form", method = RequestMethod.GET)
	public @ResponseBody Form getFormDTD(HttpServletRequest request) {
		log.info(formRequest.format(new Object[] { "getFormDTD" }));
		return this.repository.findTopByOrderByVersionDesc();
	}

	@RequestMapping(value = "/v1/form/version/{version}", method = RequestMethod.GET)
	public @ResponseBody Form getFormDTD(@PathVariable Double version) {
		log.info(formRequest.format(new Object[] { "getFormDTD" }));
		return this.repository.findByVersion(version).get(0);
	}

}
