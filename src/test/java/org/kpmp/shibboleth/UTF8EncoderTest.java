package org.kpmp.shibboleth;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UTF8EncoderTest {

	private UTF8Encoder encoder;

	@Before
	public void setUp() throws Exception {
		encoder = new UTF8Encoder();
	}

	@After
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