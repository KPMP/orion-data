package org.kpmp.globus;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GlobusTransferRequestTest {

	private GlobusTransferRequest request;

	@Before
	public void setUp() throws Exception {
		request = new GlobusTransferRequest();
	}

	@After
	public void tearDown() throws Exception {
		request = null;
	}

	@Test
	public void testSetPath() {
		request.setPath("path/to/wherever");

		assertEquals("path/to/wherever", request.getPath());
	}

	@Test
	public void testSetDataType() {
		request.setDataType("data type");

		assertEquals("data type", request.getDataType());
	}

}
