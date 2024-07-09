package org.miktmc.shibboleth;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

@Component
class UTF8Encoder {

	public String convertFromLatin1(String value) {
		return new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
	}

}