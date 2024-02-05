package org.kpmp.packages.validation;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

	@Test
	public void testFilenamesNotInGlobus_filesAtMultipleLevelsDoNotMatch() {
		Map<String, List<String>> globusListing = new HashMap<>();
		globusListing.put("", Arrays.asList("file1", "file2"));
		globusListing.put("directory1/", Arrays.asList("file3"));
		globusListing.put("directory2/",new ArrayList<String>());
		List<String> actualFilenamesNotFound = service.filenamesNotInGlobus(globusListing , Arrays.asList("file1", "file2", "directory1/file3", "directory2/file4"));

		assertEquals(Arrays.asList("directory2/file4"), actualFilenamesNotFound);
	}

	@Test
	public void testFilenamesNotInGlobus_missingEmptyDirectories() {
		Map<String, List<String>> globusListing = new HashMap<>();
		globusListing.put("", Arrays.asList("file1", "file2"));
		globusListing.put("directory1/", Arrays.asList("file3"));
		List<String> actualFilenamesNotFound = service.filenamesNotInGlobus(globusListing , Arrays.asList("file1", "file2", "directory1/file3", "directory2", "directory3/"));

		assertEquals(Arrays.asList("directory2", "directory3/"), actualFilenamesNotFound);
	}

	@Test
	public void testFilesNotInMetadata_perfectMatch() {
		Map<String, List<String>> globusListing = new HashMap<>();
		globusListing.put("", Arrays.asList("file1.txt"));
		globusListing.put("subdirectory", Arrays.asList("file2", "file3"));
		List<String> metadataFiles = Arrays.asList("file1.txt", "subdirectory/file2", "subdirectory/file3");

		assertEquals(Collections.emptyList(), service.filesNotInMetadata(globusListing, metadataFiles));
	}

	@Test
	public void testFilesNotInMetadata_missingFileAtTopLevel() {
		Map<String, List<String>> globusListing = new HashMap<>();
		globusListing.put("", Arrays.asList("file1.txt"));
		globusListing.put("subdirectory", Arrays.asList("file2", "file3"));
		List<String> metadataFiles = Arrays.asList("subdirectory/file2", "subdirectory/file3");

		assertEquals(Arrays.asList("file1.txt"), service.filesNotInMetadata(globusListing, metadataFiles));
	}

	@Test
	public void testFilesNotInMetadata_missingFileAtSecondLevel() {
		Map<String, List<String>> globusListing = new HashMap<>();
		globusListing.put("", Arrays.asList("file1.txt"));
		globusListing.put("subdirectory", Arrays.asList("file2", "file3"));
		List<String> metadataFiles = Arrays.asList("file1.txt", "subdirectory/file3");

		assertEquals(Arrays.asList("subdirectory/file2"), service.filesNotInMetadata(globusListing, metadataFiles));
	}

	@Test
	public void testFilesNotInMetadata_ignoreMetadataFile() {
		Map<String, List<String>> globusListing = new HashMap<>();
		globusListing.put("", Arrays.asList("file1.txt", "METADATA_file.xlsx"));
		globusListing.put("subdirectory", Arrays.asList("file2", "file3"));
		List<String> metadataFiles = Arrays.asList("file1.txt", "subdirectory/file2", "subdirectory/file3");

		assertEquals(Collections.emptyList(), service.filesNotInMetadata(globusListing, metadataFiles));
	}

}
