package org.kpmp.packages.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class PackageValidationResponseTest {

	private PackageValidationResponse response;

	@BeforeEach
	public void setUp() throws Exception {
		response = new PackageValidationResponse();
	}

	@AfterEach
	public void tearDown() throws Exception {
		response = null;
	}

	@Test
	public void testGetDirectoriesInGlobus() {
		List<String> expected = Arrays.asList("dir1", "dir2");
		response.setDirectoriesInGlobus(expected);

		assertEquals(expected, response.getDirectoriesInGlobus());
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

	@Test
	public void testSetFilesFromMetadata() throws Exception {
		List<String> metadataFiles = Arrays.asList("file1");

		response.setFilesFromMetadata(metadataFiles);

		assertEquals(metadataFiles, response.getFilesFromMetadata());
	}

	@Test
	public void testSetFilesInGlobus() throws Exception {
		List<String> globusFiles = Arrays.asList("globus1");

		response.setFilesInGlobus(globusFiles);

		assertEquals(globusFiles, response.getFilesInGlobus());
	}

	@Test
	public void testSetPackageExists() throws Exception {
		response.setDirectoryExists(false);
		assertFalse(response.getDirectoryExists());
	}
}
