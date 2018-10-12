package org.kpmp.shibboleth;

import java.io.UnsupportedEncodingException;

import org.apache.commons.compress.utils.Charsets;
import org.springframework.stereotype.Component;

@Component
public class UTF8Encoder {

	public String convertFromLatin1(String value) throws UnsupportedEncodingException {
		return new String(value.getBytes(Charsets.ISO_8859_1), Charsets.UTF_8);
	}

}
