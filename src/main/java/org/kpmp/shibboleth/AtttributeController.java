package org.kpmp.shibboleth;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AtttributeController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final MessageFormat attributesDisplayName = new MessageFormat("Request|{0}");
	private UTF8Encoder encoder;

	@Autowired
	public AtttributeController(UTF8Encoder encoder) {
		this.encoder = encoder;
	}

	@RequestMapping(value = "/v1/attributes", method = RequestMethod.GET)
	public @ResponseBody User getAttributes(HttpServletRequest request) throws UnsupportedEncodingException {
		log.info(attributesDisplayName.format(new Object[] { "getDisplayName" }));

		User user = new User(request, encoder);

		return user;
	}
}
