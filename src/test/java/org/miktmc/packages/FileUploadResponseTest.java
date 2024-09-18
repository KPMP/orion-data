package org.miktmc.packages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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
