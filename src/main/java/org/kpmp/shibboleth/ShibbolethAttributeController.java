package org.kpmp.shibboleth;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.kpmp.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ShibbolethAttributeController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final MessageFormat attributesDisplayName = new MessageFormat("Request|{0}");
	private UTF8Encoder encoder;
	private ShibbolethUserService shibbolethUserService;

	@Autowired
	public ShibbolethAttributeController(UTF8Encoder encoder, ShibbolethUserService shibbolethUserService) {
		this.encoder = encoder;
		this.shibbolethUserService = shibbolethUserService;
	}

	// userMap(request)
	// if env var 'userInformation_displayName' then use that otherwise pull it
	// from the request

	@RequestMapping(value = "/v1/userInformation", method = RequestMethod.GET)
	public @ResponseBody
	User getAttributes(HttpServletRequest request) throws UnsupportedEncodingException {
		log.info(attributesDisplayName.format(new Object[] { "getAttributes" }));

		// request -> userMap

		User user = shibbolethUserService.getUser(request, encoder);
		return user;
	}
}
