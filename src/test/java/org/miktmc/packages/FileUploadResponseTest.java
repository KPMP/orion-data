package org.miktmc.packages;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FileUploadResponseTest {

	@Test
	public void testIsSuccess_whenSet() {
		FileUploadResponse response = new FileUploadResponse(false);
		response.setSuccess(true);
		assertEquals(true, response.isSuccess());
	}

	@Test
	public void testIsSuccess() throws Exception {
		FileUploadResponse response = new FileUploadResponse(true);
		assertEquals(true, response.isSuccess());
	}
}
