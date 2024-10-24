package org.kpmp.packages.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		service = new PackageFilesValidationService(globus);
	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
		service = null;
	}

	@Test
	public void testGetGlobusFilePaths() {
		Map<String, List<String>> globusFilesInDirectories = new HashMap<>();
		globusFilesInDirectories.put("", Arrays.asList("file1", "file2"));
		globusFilesInDirectories.put("directory1", Arrays.asList("file3", "file4"));

		List<String> expectedFilePaths = Arrays.asList("file1", "file2", "directory1/file3", "directory1/file4");

		assertEquals(expectedFilePaths, service.getGlobusFilePaths(globusFilesInDirectories, ""));
	}

	@Test
	public void testGetGlobusFilePaths_emptyDirectory() {
		Map<String, List<String>> globusFilesInDirectories = new HashMap<>();
		globusFilesInDirectories.put("", Arrays.asList("file1", "file2"));
		globusFilesInDirectories.put("directory1",Collections.emptyList());

		List<String> expectedFilePaths = Arrays.asList("file1", "file2", "directory1");

		assertEquals(expectedFilePaths, service.getGlobusFilePaths(globusFilesInDirectories, ""));
	}

	@Test
	public void testGetGlobusFilePaths_multipleDirectories() {
		Map<String, List<String>> globusFilesInDirectories = new HashMap<>();
		globusFilesInDirectories.put("", Arrays.asList("file1", "file2"));
		globusFilesInDirectories.put("directory1/directory2/directory3",Arrays.asList("file4"));


		List<String> expectedFilePaths = Arrays.asList("file1", "file2", "directory1/directory2/directory3/file4");

		assertEquals(expectedFilePaths, service.getGlobusFilePaths(globusFilesInDirectories, ""));
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
		expectedResults.put("directory1", Arrays.asList("file1", "file2"));
		expectedResults.put("directory2", Arrays.asList("file3", "file4"));

		actualListing = service.processGlobusDirectory(new HashMap<String, List<String>>(), Arrays.asList("directory1", "directory2"), "123", "", "");

		assertEquals(expectedResults, actualListing);
	}

	@Test
	public void testProcessGlobusDirectory_spacesInDirectoryNames() throws JsonProcessingException, IOException {
		Map<String, List<String>> actualListing = new HashMap<String, List<String>>();
		GlobusFileListing globusFile1 = new GlobusFileListing();
		globusFile1.setName("file1");
		globusFile1.setType("file");
		GlobusFileListing globusFile2 = new GlobusFileListing();
		globusFile2.setName("file2");
		globusFile2.setType("file");
		when(globus.getFilesAndDirectoriesAtEndpoint("123/directory 1")).thenReturn(Arrays.asList(globusFile1, globusFile2));
		GlobusFileListing globusFile3 = new GlobusFileListing();
		globusFile3.setName("file3");
		globusFile3.setType("file");
		GlobusFileListing globusFile4 = new GlobusFileListing();
		globusFile4.setName("file4");
		globusFile4.setType("file");
		when(globus.getFilesAndDirectoriesAtEndpoint("123/directory 1/subfolder")).thenReturn(Arrays.asList(globusFile3, globusFile4));
		GlobusFileListing globusFile5 = new GlobusFileListing();
		globusFile5.setName("file5");
		globusFile5.setType("file");
		GlobusFileListing globusFile6 = new GlobusFileListing();
		globusFile6.setName("file6");
		globusFile6.setType("file");
		when(globus.getFilesAndDirectoriesAtEndpoint("123/directory 1/subfolder")).thenReturn(Arrays.asList(globusFile5, globusFile6));
		Map<String, List<String>> expectedResults = new HashMap<>();
		expectedResults.put("directory 1", Arrays.asList("file1", "file2"));
		expectedResults.put("directory2", Arrays.asList("file3", "file4"));
		expectedResults.put("directory 1/subfolder", Arrays.asList("file5", "file6"));

		actualListing = service.processGlobusDirectory(new HashMap<String, List<String>>(), Arrays.asList("directory 1", "directory2", "directory 1/subfolder"), "123", "", "");

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
		expectedResults.put("directory1", Arrays.asList("file1", "file2"));
		expectedResults.put("directory1/subdirectory", Arrays.asList("file3", "file4"));

		actualListing = service.processGlobusDirectory(new HashMap<String, List<String>>(), Arrays.asList("directory1"), "123", "", "");

		assertEquals(expectedResults, actualListing);
	}

	@Test
	public void testProcessGlobusDirectory_withEmptySubdirectories() throws JsonProcessingException, IOException {
		// directory1/file1
		// directory1/file2
		// directory1/subdirectory
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
		GlobusFileListing globusFile3 = new GlobusFileListing();
		globusFile3.setName("metadata.xlsx");
		globusFile3.setType("file");
		GlobusFileListing globusDirectory = new GlobusFileListing();
		globusDirectory.setName("directory1");
		globusDirectory.setType("dir");
		when(globus.getFilesAndDirectoriesAtEndpoint("123")).thenReturn(Arrays.asList(globusDirectory, globusFile3));
		when(globus.getFilesAndDirectoriesAtEndpoint("123/directory1")).thenReturn(Arrays.asList(globusFile1, globusFile2, globusSubdirectory));
		when(globus.getFilesAndDirectoriesAtEndpoint("123/directory1/subdirectory")).thenReturn(Arrays.asList());
		Map<String, List<String>> expectedResults = new HashMap<>();
		expectedResults.put("directory1", Arrays.asList("file1", "file2"));
		expectedResults.put("directory1/subdirectory", Arrays.asList());

		actualListing = service.processGlobusDirectory(new HashMap<String, List<String>>(), Arrays.asList("directory1"), "123", "", "");

		assertEquals(expectedResults.size(), actualListing.size());
		Set<String> expectedKeys = expectedResults.keySet();
		Set<String> actualKeys = actualListing.keySet();
		assertEquals(true, expectedKeys.equals(actualKeys));
		for (String key : expectedResults.keySet()) {
			assertEquals(expectedResults.get(key), actualListing.get(key));
		}
	}

    @Test
	public void testFilenamesNotInGlobus_nothingInGlobus() {
		List<String> emptyGlobus = new ArrayList<>();
		List<String> actualFilenamesNotFound = service.filenamesNotInGlobus(emptyGlobus , Arrays.asList("file1", "file2"));

		assertEquals(Arrays.asList("file1", "file2"), actualFilenamesNotFound);
	}

	@Test 
	public void testFilenamesNotInGlobus_perfectMatch() {
		List<String> filesInGlobus = Arrays.asList("file1", "file2");
		List<String> actualFilenamesNotFound = service.filenamesNotInGlobus(filesInGlobus , Arrays.asList("file1", "file2"));

		assertEquals(Arrays.asList(), actualFilenamesNotFound);
	}

	@Test
	public void testFilenamesNotInGlobus_filesAtMultipleLevelsMatch() {
		List<String> filesInGlobus = Arrays.asList("file1", "file2", "directory1/file3", "directory1/file4");
		List<String> actualFilenamesNotFound = service.filenamesNotInGlobus(filesInGlobus , Arrays.asList("file1", "file2", "directory1/file3", "directory1/file4"));

		assertEquals(Arrays.asList(), actualFilenamesNotFound);
	}

	@Test
	public void testFilenamesNotInGlobus_filesAtMultipleLevelsDoNotMatch() {
		List<String> filesInGlobus = Arrays.asList("file1", "file2", "directory1/file3", "directory2");
		List<String> actualFilenamesNotFound = service.filenamesNotInGlobus(filesInGlobus , Arrays.asList("file1", "file2", "directory1/file3", "directory2/file4"));

		assertEquals(Arrays.asList("directory2/file4"), actualFilenamesNotFound);
	}

	@Test
	public void testFilenamesNotInGlobus_missingEmptyDirectories() {
		List<String> filesInGlobus = Arrays.asList("file1", "file2", "directory1/file3");
		List<String> actualFilenamesNotFound = service.filenamesNotInGlobus(filesInGlobus , Arrays.asList("file1", "file2", "directory1/file3", "directory2", "directory3/"));

		assertEquals(Arrays.asList("directory2", "directory3/"), actualFilenamesNotFound);
	}

	@Test
	public void testFilesNotInMetadata_perfectMatch() {
		List<String> globusListing = Arrays.asList("file1.txt", "subdirectory/file2", "subdirectory/file3");
		List<String> metadataFiles = Arrays.asList("file1.txt", "subdirectory/file2", "subdirectory/file3");

		assertEquals(Collections.emptyList(), service.filenamesNotInMetadata(globusListing, metadataFiles));
	}

	@Test
	public void testFilesNotInMetadata_missingFileAtTopLevel() {
		List<String> globusListing = Arrays.asList("file1.txt", "subdirectory/file2", "subdirectory/file3");
		List<String> metadataFiles = Arrays.asList("subdirectory/file2", "subdirectory/file3");

		assertEquals(Arrays.asList("file1.txt"), service.filenamesNotInMetadata(globusListing, metadataFiles));
	}

	@Test
	public void testFilesNotInMetadata_missingFileAtSecondLevel() {
		List<String> globusListing = Arrays.asList("file1.txt", "subdirectory/file2", "subdirectory/file3");
		List<String> metadataFiles = Arrays.asList("file1.txt", "subdirectory/file3");

		assertEquals(Arrays.asList("subdirectory/file2"), service.filenamesNotInMetadata(globusListing, metadataFiles));
	}

	@Test
	public void testFilesNotInMetadata_ignoreMetadataFile() {
		List<String> globusListing = Arrays.asList("file1.txt", "METADATA_file.xlsx", "subdirectory/file2", "subdirectory/file3");
		List<String> metadataFiles = Arrays.asList("file1.txt", "subdirectory/file2", "subdirectory/file3");

		assertEquals(Collections.emptyList(), service.filenamesNotInMetadata(globusListing, metadataFiles));
	}

	@Test
	public void testMatchFile_srtipsEmptyParentDirectory() throws JsonProcessingException, IOException {
		
		GlobusFileListing parent = new GlobusFileListing();
		parent.setName("parent");
		parent.setType("dir");
		GlobusFileListing globusFile1 = new GlobusFileListing();
		globusFile1.setName("file1");
		globusFile1.setType("file");
		GlobusFileListing globusFile2 = new GlobusFileListing();
		globusFile2.setName("file2");
		globusFile2.setType("file");
		GlobusFileListing globusFile3 = new GlobusFileListing();
		globusFile3.setName("file3");
		globusFile3.setType("file");
		GlobusFileListing globusFolder1 = new GlobusFileListing();
		globusFolder1.setName("folder1");
		globusFolder1.setType("dir");
		GlobusFileListing subFile1 = new GlobusFileListing();
		subFile1.setName("subFile1");
		subFile1.setType("file");
		List<GlobusFileListing> globusFileListing = Arrays.asList(globusFile1, globusFile2, globusFile3, globusFolder1);
		when(globus.getFilesAndDirectoriesAtEndpoint("package123")).thenReturn(Arrays.asList(parent));
		when(globus.getFilesAndDirectoriesAtEndpoint("package123/parent")).thenReturn(globusFileListing);
		when(globus.getFilesAndDirectoriesAtEndpoint("package123/parent/folder1")).thenReturn(Arrays.asList(subFile1));
		PackageFilesRequest request = new PackageFilesRequest();
		request.setFilenames("file1\nfile2\nfile3\nfolder1/subFile1");
		request.setPackageId("package123");


		PackageValidationResponse response = service.matchFiles(request);
		assertEquals(true, response.getDirectoryExists());
		assertEquals(Arrays.asList("file1", "file2", "file3", "folder1/subFile1"), response.getFilesFromMetadata());
		assertEquals("package123", response.getPackageId());
		assertEquals(Arrays.asList("file1", "file2", "file3", "folder1/subFile1"), response.getFilesInGlobus());
		assertEquals(null, response.getMetadataFilesNotFoundInGlobus());
		assertEquals(null, response.getGlobusFilesNotFoundInMetadata());
	}

	@Test
	public void testMatchFile_perfectMatchOneLevel() throws JsonProcessingException, IOException {

		GlobusFileListing globusFile1 = new GlobusFileListing();
		globusFile1.setName("file1");
		globusFile1.setType("file");
		GlobusFileListing globusFile2 = new GlobusFileListing();
		globusFile2.setName("file2");
		globusFile2.setType("file");
		GlobusFileListing globusFile3 = new GlobusFileListing();
		globusFile3.setName("file3");
		globusFile3.setType("file");
		List<GlobusFileListing> globusFileListing = Arrays.asList(globusFile1, globusFile2, globusFile3);
		when(globus.getFilesAndDirectoriesAtEndpoint("package123")).thenReturn(globusFileListing);
		PackageFilesRequest request = new PackageFilesRequest();
		request.setFilenames("file1\nfile2,file3");
		request.setPackageId("package123");


		PackageValidationResponse response = service.matchFiles(request);
		assertEquals(true, response.getDirectoryExists());
		assertEquals(Arrays.asList("file1", "file2", "file3"), response.getFilesFromMetadata());
		assertEquals("package123", response.getPackageId());
		assertEquals(Arrays.asList("file1", "file2", "file3"), response.getFilesInGlobus());
		assertEquals(null, response.getMetadataFilesNotFoundInGlobus());
		assertEquals(null, response.getGlobusFilesNotFoundInMetadata());
	}

	@Test
	public void testMatchFile_misatchOneLevel() throws JsonProcessingException, IOException {
		GlobusFileListing globusFile1 = new GlobusFileListing();
		globusFile1.setName("file1");
		globusFile1.setType("file");
		GlobusFileListing globusFile2 = new GlobusFileListing();
		globusFile2.setName("file2");
		globusFile2.setType("file");
		List<GlobusFileListing> globusFileListing = Arrays.asList(globusFile1, globusFile2);
		when(globus.getFilesAndDirectoriesAtEndpoint("package123")).thenReturn(globusFileListing);
		PackageFilesRequest request = new PackageFilesRequest();
		request.setFilenames("file1\nfile2,file3");
		request.setPackageId("package123");


		PackageValidationResponse response = service.matchFiles(request);
		assertEquals(true, response.getDirectoryExists());
		assertEquals(Arrays.asList("file1", "file2", "file3"), response.getFilesFromMetadata());
		assertEquals("package123", response.getPackageId());
		assertEquals(Arrays.asList("file1", "file2"), response.getFilesInGlobus());
		assertEquals(Arrays.asList("file3"), response.getMetadataFilesNotFoundInGlobus());
		assertEquals(null, response.getGlobusFilesNotFoundInMetadata());
	}

	@Test
	public void testMatchFile_perfectMatchMultiLevel() throws JsonProcessingException, IOException {
		GlobusFileListing globusFile1 = new GlobusFileListing();
		globusFile1.setName("file1");
		globusFile1.setType("file");
		GlobusFileListing directory = new GlobusFileListing();
		directory.setName("directory");
		directory.setType("dir");
		GlobusFileListing globusFile2 = new GlobusFileListing();
		globusFile2.setName("file2");
		globusFile2.setType("file");
		List<GlobusFileListing> globusFileListing = Arrays.asList(globusFile1, directory);
		when(globus.getFilesAndDirectoriesAtEndpoint("package123")).thenReturn(globusFileListing);
		when(globus.getFilesAndDirectoriesAtEndpoint("package123/directory")).thenReturn(Arrays.asList(globusFile2));
		PackageFilesRequest request = new PackageFilesRequest();
		request.setFilenames("file1\ndirectory/file2");
		request.setPackageId("package123");


		PackageValidationResponse response = service.matchFiles(request);
		assertEquals(true, response.getDirectoryExists());
		assertEquals(Arrays.asList("file1", "directory/file2"), response.getFilesFromMetadata());
		assertEquals("package123", response.getPackageId());
		assertEquals(Arrays.asList("file1", "directory/file2"), response.getFilesInGlobus());
		assertEquals(null, response.getMetadataFilesNotFoundInGlobus());
		assertEquals(null, response.getGlobusFilesNotFoundInMetadata());
	}

	@Test
	public void testMatchFile_misatchMultiLevel() throws JsonProcessingException, IOException {
		
		GlobusFileListing globusFile1 = new GlobusFileListing();
		globusFile1.setName("file1");
		globusFile1.setType("file");
		GlobusFileListing globusFile2 = new GlobusFileListing();
		globusFile2.setName("directory");
		globusFile2.setType("dir");

		List<GlobusFileListing> globusFileListing = Arrays.asList(globusFile1, globusFile2);
		when(globus.getFilesAndDirectoriesAtEndpoint("package123")).thenReturn(globusFileListing);
		when(globus.getFilesAndDirectoriesAtEndpoint("package123/directory")).thenReturn(Collections.emptyList());
		PackageFilesRequest request = new PackageFilesRequest();
		request.setFilenames("file1\ndirectory/file2");
		request.setPackageId("package123");

		PackageValidationResponse response = service.matchFiles(request);

		assertEquals(true, response.getDirectoryExists());
		assertEquals(Arrays.asList("file1", "directory/file2"), response.getFilesFromMetadata());
		assertEquals("package123", response.getPackageId());
		assertEquals(Arrays.asList("file1", "directory"), response.getFilesInGlobus());
		assertEquals(Arrays.asList("directory/file2"), response.getMetadataFilesNotFoundInGlobus());
		assertEquals(Arrays.asList("directory"), response.getGlobusFilesNotFoundInMetadata());
	}

}
