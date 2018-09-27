package org.kpmp.shibboleth;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AtttributeController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final MessageFormat attributesDisplayName = new MessageFormat("Request|{0}");
	private static final MessageFormat headers = new MessageFormat("HEADERS|{0}|{1}");

	@RequestMapping(value = "/v1/attributes/displayName", method = RequestMethod.GET)
	public @ResponseBody String getDisplayName(HttpServletRequest request) throws UnsupportedEncodingException {

		log.info(attributesDisplayName.format(new Object[] { "getDisplayName" }));

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String header = headerNames.nextElement();
			String value = request.getHeader(header);
			log.info(headers.format(new Object[] { header, value }));
		}

		String value = request.getHeader("displayname");
		if (value != null) {
			String displayName = new String(value.getBytes("ISO-8859-1"), "UTF-8");
			return displayName;
		}
		return "";
	}
}
