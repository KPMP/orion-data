package org.miktmc.shibboleth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UTF8EncoderTest {

	private UTF8Encoder encoder;

	@BeforeEach
	public void setUp() throws Exception {
		encoder = new UTF8Encoder();
	}

	@AfterEach
	public void tearDown() throws Exception {
		encoder = null;
	}

	@Test
	public void testConvertFromLatin1() throws UnsupportedEncodingException {
		String latin1Encoded = new String("latin".getBytes(), "ISO-8859-1");

		String utf8Encoded = encoder.convertFromLatin1(latin1Encoded);

		assertEquals("latin", utf8Encoded);
	}

}