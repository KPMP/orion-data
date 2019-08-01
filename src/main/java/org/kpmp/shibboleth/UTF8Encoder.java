package org.kpmp.shibboleth;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

@Component
class UTF8Encoder {

	public String convertFromLatin1(String value) throws UnsupportedEncodingException {
		return new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
	}

}