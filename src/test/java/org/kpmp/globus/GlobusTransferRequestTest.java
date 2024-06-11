package org.kpmp.globus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class GlobusTransferRequestTest {

	private GlobusTransferRequest request;

	@BeforeEach
	public void setUp() throws Exception {
		request = new GlobusTransferRequest();
	}

	@AfterEach
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
