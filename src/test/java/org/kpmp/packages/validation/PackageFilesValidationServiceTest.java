package org.kpmp.packages.validation;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.globus.GlobusFileListing;
import org.kpmp.globus.GlobusService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.JsonProcessingException;

public class PackageFilesValidationServiceTest {

	@Mock
	private GlobusService globus;
	private PackageFilesValidationService service;
	private AutoCloseable mocks;

	@Before
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		service = new PackageFilesValidationService(globus);
	}

	@After
	public void tearDown() throws Exception {
		mocks.close();
		service = null;
	}


	@Test
	public void testProcessGlobusDirectory() throws JsonProcessingException, IOException {
		Map<String, List<String>> actualListing = new HashMap<String, List<String>>();
		GlobusFileListing globusFile1 = new GlobusFileListing();
		globusFile1.setName("file1");
		globusFile1.setType("file");
		GlobusFileListing globusFile2 = new GlobusFileListing();
		globusFile2.setName("file2");
		globusFile2.setType("file");
		when(globus.getFilesAndDirectoriesAtEndpoint("123/directory1")).thenReturn(Arrays.asList(globusFile1, globusFile2));
		GlobusFileListing globusFile3 = new GlobusFileListing();
		globusFile3.setName("file3");
		globusFile3.setType("file");
		GlobusFileListing globusFile4 = new GlobusFileListing();
		globusFile4.setName("file4");
		globusFile4.setType("file");
		when(globus.getFilesAndDirectoriesAtEndpoint("123/directory2")).thenReturn(Arrays.asList(globusFile3, globusFile4));
		Map<String, List<String>> expectedResults = new HashMap<>();
		expectedResults.put("/directory1", Arrays.asList("file1", "file2"));
		expectedResults.put("/directory2", Arrays.asList("file3", "file4"));

		actualListing = service.processGlobusDirectory(new HashMap<String, List<String>>(), Arrays.asList("directory1", "directory2"), "123", "");

		assertEquals(expectedResults, actualListing);
	}


	@Test
	public void testProcessGlobusDirectory_withSubdirectories() throws JsonProcessingException, IOException {
		Map<String, List<String>> actualListing = new HashMap<String, List<String>>();
		GlobusFileListing globusFile1 = new GlobusFileListing();
		globusFile1.setName("file1");
		globusFile1.setType("file");
		GlobusFileListing globusFile2 = new GlobusFileListing();
		globusFile2.setName("file2");
		globusFile2.setType("file");
		GlobusFileListing globusSubdirectory = new GlobusFileListing();
		globusSubdirectory.setName("subdirectory");
		globusSubdirectory.setType("dir");
		when(globus.getFilesAndDirectoriesAtEndpoint("123/directory1")).thenReturn(Arrays.asList(globusFile1, globusFile2, globusSubdirectory));
		GlobusFileListing globusFile3 = new GlobusFileListing();
		globusFile3.setName("file3");
		globusFile3.setType("file");
		GlobusFileListing globusFile4 = new GlobusFileListing();
		globusFile4.setName("file4");
		globusFile4.setType("file");
		when(globus.getFilesAndDirectoriesAtEndpoint("123/directory1/subdirectory")).thenReturn(Arrays.asList(globusFile3, globusFile4));
		Map<String, List<String>> expectedResults = new HashMap<>();
		expectedResults.put("/directory1", Arrays.asList("file1", "file2"));
		expectedResults.put("/directory1/subdirectory", Arrays.asList("file3", "file4"));

		actualListing = service.processGlobusDirectory(new HashMap<String, List<String>>(), Arrays.asList("directory1"), "123", "");

		assertEquals(expectedResults, actualListing);
	}

	@Test
	public void testProcessGlobusDirectory_withEmptySubdirectories() throws JsonProcessingException, IOException {
		Map<String, List<String>> actualListing = new HashMap<String, List<String>>();
		GlobusFileListing globusFile1 = new GlobusFileListing();
		globusFile1.setName("file1");
		globusFile1.setType("file");
		GlobusFileListing globusFile2 = new GlobusFileListing();
		globusFile2.setName("file2");
		globusFile2.setType("file");
		GlobusFileListing globusSubdirectory = new GlobusFileListing();
		globusSubdirectory.setName("subdirectory");
		globusSubdirectory.setType("dir");
		when(globus.getFilesAndDirectoriesAtEndpoint("123/directory1")).thenReturn(Arrays.asList(globusFile1, globusFile2, globusSubdirectory));
		when(globus.getFilesAndDirectoriesAtEndpoint("123/directory1/subdirectory")).thenReturn(Arrays.asList());
		Map<String, List<String>> expectedResults = new HashMap<>();
		expectedResults.put("/directory1", Arrays.asList("file1", "file2"));
		expectedResults.put("/directory1/subdirectory", Arrays.asList());

		actualListing = service.processGlobusDirectory(new HashMap<String, List<String>>(), Arrays.asList("directory1"), "123", "");

		assertEquals(expectedResults, actualListing);
	}

    @Test
	public void testFilenamesNotInGlobus_nothingInGlobus() {
		Map<String, List<String>> globusListing = new HashMap<>();
		List<String> actualFilenamesNotFound = service.filenamesNotInGlobus(globusListing , Arrays.asList("file1", "file2"));

		assertEquals(Arrays.asList("file1", "file2"), actualFilenamesNotFound);
	}

	@Test 
	public void testFilenamesNotInGlobus_perfectMatch() {
		Map<String, List<String>> globusListing = new HashMap<>();
		globusListing.put("", Arrays.asList("file1", "file2"));
		List<String> actualFilenamesNotFound = service.filenamesNotInGlobus(globusListing , Arrays.asList("file1", "file2"));

		assertEquals(Arrays.asList(), actualFilenamesNotFound);
	}

	@Test
	public void testFilenamesNotInGlobus_filesAtMultipleLevelsMatch() {
		Map<String, List<String>> globusListing = new HashMap<>();
		globusListing.put("", Arrays.asList("file1", "file2"));
		globusListing.put("directory1/", Arrays.asList("file3", "file4"));
		List<String> actualFilenamesNotFound = service.filenamesNotInGlobus(globusListing , Arrays.asList("file1", "file2", "directory1/file3", "directory1/file4"));

		assertEquals(Arrays.asList(), actualFilenamesNotFound);
	}

	// @Test
	// public void testMatchFiles() throws JsonProcessingException, IOException {
	// 	PackageFilesRequest request = new PackageFilesRequest();
	// 	request.setPackageId("323");
	// 	request.setFilenames("file1, file2.xls");
	// 	GlobusFileListing globusFile1 = new GlobusFileListing();
	// 	globusFile1.setName("file2.xls");
	// 	GlobusFileListing globusFile2 = new GlobusFileListing();
	// 	globusFile2.setName("file1");
	// 	when(globus.getFilesAndDirectoriesAtEndpoint("323")).thenReturn(Arrays.asList(globusFile1, globusFile2));

	// 	PackageValidationResponse result = service.matchFiles(request);

	// 	assertEquals(null, result.getGlobusFilesNotFoundInMetadata());
	// 	assertEquals(null, result.getMetadataFilesNotFoundInGlobus());
	// 	assertEquals("323", result.getPackageId());
	// 	assertEquals(Arrays.asList("file1", "file2.xls"), result.getFilesFromMetadata());
	// 	assertEquals(Arrays.asList("file2.xls", "file1"), result.getFilesInGlobus());
	// }

	// @Test
	// public void testMatchFilesWhenUserUploadsToSubdir() throws JsonProcessingException, IOException {
	// 	PackageFilesRequest request = new PackageFilesRequest();
	// 	request.setPackageId("323");
	// 	request.setFilenames("file1, file2.xls");
	// 	GlobusFileListing globusFile1 = new GlobusFileListing();
	// 	globusFile1.setName("directory");
	// 	globusFile1.setType("dir");
	// 	when(globus.getFilesAndDirectoriesAtEndpoint("323")).thenReturn(Arrays.asList(globusFile1));
	// 	when(globus.getFilesAndDirectoriesAtEndpoint("323/dir")).thenReturn(Arrays.asList(globusFile1));

	// 	PackageValidationResponse result = service.matchFiles(request);

	// 	verify(globus, times(2)).getFilesAndDirectoriesAtEndpoint(any(String.class));
	// 	assertEquals(null, result.getGlobusFilesNotFoundInMetadata());
	// 	assertEquals(Arrays.asList("file1", "file2.xls"), result.getMetadataFilesNotFoundInGlobus());
	// 	assertEquals("323", result.getPackageId());
	// 	assertEquals(Arrays.asList("file1", "file2.xls"), result.getFilesFromMetadata());
	// 	assertEquals(Arrays.asList(), result.getFilesInGlobus());
	// }

	// @Test
	// public void testMatchFilesIgnoresMetadataFile() throws JsonProcessingException, IOException {
	// 	PackageFilesRequest request = new PackageFilesRequest();
	// 	request.setPackageId("323");
	// 	request.setFilenames("file1, file2.xls");
	// 	GlobusFileListing globusFile1 = new GlobusFileListing();
	// 	globusFile1.setName("file2.xls");
	// 	GlobusFileListing globusFile2 = new GlobusFileListing();
	// 	globusFile2.setName("file1");
	// 	GlobusFileListing globusFile3 = new GlobusFileListing();
	// 	globusFile3.setName("METADATA_stuff.xls");
	// 	when(globus.getFilesAndDirectoriesAtEndpoint("323")).thenReturn(Arrays.asList(globusFile1, globusFile2, globusFile3));

	// 	PackageValidationResponse result = service.matchFiles(request);

	// 	assertEquals(null, result.getGlobusFilesNotFoundInMetadata());
	// 	assertEquals(null, result.getMetadataFilesNotFoundInGlobus());
	// 	assertEquals("323", result.getPackageId());
	// 	assertEquals(Arrays.asList("file1", "file2.xls"), result.getFilesFromMetadata());
	// 	assertEquals(Arrays.asList("file2.xls", "file1", "METADATA_stuff.xls"), result.getFilesInGlobus());
	// }

	// @Test
	// public void testMatchFilesExtraFilesInGlobus() throws JsonProcessingException, IOException {
	// 	PackageFilesRequest request = new PackageFilesRequest();
	// 	request.setPackageId("323");
	// 	request.setFilenames("file1, file2.xls");
	// 	GlobusFileListing globusFile1 = new GlobusFileListing();
	// 	globusFile1.setName("file2.xls");
	// 	GlobusFileListing globusFile2 = new GlobusFileListing();
	// 	globusFile2.setName("file1");
	// 	GlobusFileListing globusFile3 = new GlobusFileListing();
	// 	globusFile3.setName("extraGlobusFile.bam");
	// 	when(globus.getFilesAndDirectoriesAtEndpoint("323")).thenReturn(Arrays.asList(globusFile1, globusFile2, globusFile3));

	// 	PackageValidationResponse result = service.matchFiles(request);

	// 	assertEquals(Arrays.asList("extraGlobusFile.bam"), result.getGlobusFilesNotFoundInMetadata());
	// 	assertEquals(null, result.getMetadataFilesNotFoundInGlobus());
	// 	assertEquals("323", result.getPackageId());
	// 	assertEquals(Arrays.asList("file1", "file2.xls"), result.getFilesFromMetadata());
	// 	assertEquals(Arrays.asList("file2.xls", "file1", "extraGlobusFile.bam"), result.getFilesInGlobus());
	// }

	// @Test
	// public void testMatchFilesExtraFilesInMetadata() throws JsonProcessingException, IOException {
	// 	PackageFilesRequest request = new PackageFilesRequest();
	// 	request.setPackageId("323");
	// 	request.setFilenames("file1, file2.xls, extraFile.txt");
	// 	GlobusFileListing globusFile1 = new GlobusFileListing();
	// 	globusFile1.setName("file2.xls");
	// 	GlobusFileListing globusFile2 = new GlobusFileListing();
	// 	globusFile2.setName("file1");
	// 	when(globus.getFilesAndDirectoriesAtEndpoint("323")).thenReturn(Arrays.asList(globusFile1, globusFile2));

	// 	PackageValidationResponse result = service.matchFiles(request);

	// 	assertEquals(null, result.getGlobusFilesNotFoundInMetadata());
	// 	assertEquals(Arrays.asList("extraFile.txt"), result.getMetadataFilesNotFoundInGlobus());
	// 	assertEquals("323", result.getPackageId());
	// 	assertEquals(Arrays.asList("file1", "file2.xls", "extraFile.txt"), result.getFilesFromMetadata());
	// 	assertEquals(Arrays.asList("file2.xls", "file1"), result.getFilesInGlobus());
	// }

}
