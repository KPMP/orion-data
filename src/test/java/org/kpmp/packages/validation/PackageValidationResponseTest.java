package org.kpmp.packages.validation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PackageValidationResponseTest {

	private PackageValidationResponse response;

	@Before
	public void setUp() throws Exception {
		response = new PackageValidationResponse();
	}

	@After
	public void tearDown() throws Exception {
		response = null;
	}

	@Test
	public void testAddMetadataFileNotFoundInGlobus() {
		List<String> expected = Arrays.asList("file1", "file2");
		response.addMetadataFileNotFoundInGlobus("file1");
		response.addMetadataFileNotFoundInGlobus("file2");

		assertEquals(expected, response.getMetadataFilesNotFoundInGlobus());
	}

	@Test
	public void testSetGlobusFilesNotFoundInMetadata() {
		List<String> expected = Arrays.asList("example1", "example2");
		response.addGlobusFileNotFoundInMetadata("example1");
		response.addGlobusFileNotFoundInMetadata("example2");

		assertEquals(expected, response.getGlobusFilesNotFoundInMetadata());
	}

	@Test
	public void testSetPackageId() {
		response.setPackageId("packageid");

		assertEquals("packageid", response.getPackageId());
	}

}
