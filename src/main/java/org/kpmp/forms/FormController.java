package org.kpmp.forms;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FormController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final MessageFormat formRequest = new MessageFormat("Request|{0}");

	private FormRepository repository;

	@Autowired
	public FormController(FormRepository repository) {
		this.repository = repository;
	}

	@RequestMapping(value = "/v1/form", method = RequestMethod.GET)
	public @ResponseBody Form getFormDTD() {
		log.info(formRequest.format(new Object[] { "getFormDTD" }));

		// We only have one dtd in the db
		return this.repository.findAll().get(0);
	}

}
