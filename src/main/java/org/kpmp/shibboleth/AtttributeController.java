package org.kpmp.shibboleth;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AtttributeController {

	@RequestMapping(value = "/v1/attributes/displayName", method = RequestMethod.GET)
	public String getDisplayName(HttpServletRequest request) throws UnsupportedEncodingException {
		String value = request.getHeader("displayName");
		String displayName = new String(value.getBytes("ISO-8859-1"), "UTF-8");
		return displayName;
	}
}
