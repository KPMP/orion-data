package org.kpmp.dao;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProtocolTest extends Protocol {

	private Protocol protocol;

	@Before
	public void setUp() throws Exception {
		protocol = new Protocol();
	}

	@After
	public void tearDown() throws Exception {
		protocol = null;
	}

	@Test
	public void testSetId() {
		protocol.setId(4);
		assertEquals(4, protocol.getId());
	}

	@Test
	public void testSetProtocol() {
		protocol.setProtocol("my protocol");
		assertEquals("my protocol", protocol.getProtocol());
	}

	@Test
	public void testSetUploadPackages() throws Exception {
		List<UploadPackage> uploadPackages = Arrays.asList(new UploadPackage());
		protocol.setUploadPackages(uploadPackages);
		assertEquals(uploadPackages, protocol.getUploadPackages());
	}

}
