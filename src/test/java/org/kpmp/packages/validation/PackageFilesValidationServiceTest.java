package org.kpmp.packages.validation;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;

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

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new PackageFilesValidationService(globus);
	}

	@After
	public void tearDown() throws Exception {
		service = null;
	}

	@Test
	public void testMatchFiles() throws JsonProcessingException, IOException {
		PackageFilesRequest request = new PackageFilesRequest();
		request.setPackageId("323");
		request.setFilenames("file1, file2.xls");
		GlobusFileListing globusFile1 = new GlobusFileListing();
		globusFile1.setName("file2.xls");
		GlobusFileListing globusFile2 = new GlobusFileListing();
		globusFile2.setName("file1");
		when(globus.getFilesAtEndpoint("323")).thenReturn(Arrays.asList(globusFile1, globusFile2));

		PackageValidationResponse result = service.matchFiles(request);

		assertEquals(null, result.getGlobusFilesNotFoundInMetadata());
		assertEquals(null, result.getMetadataFilesNotFoundInGlobus());
		assertEquals("323", result.getPackageId());
		assertEquals(Arrays.asList("file1", "file2.xls"), result.getFilesFromMetadata());
		assertEquals(Arrays.asList("file2.xls", "file1"), result.getFilesInGlobus());
	}

	@Test
	public void testMatchFilesWhenUserUploadsToSubdir() throws JsonProcessingException, IOException {
		PackageFilesRequest request = new PackageFilesRequest();
		request.setPackageId("323");
		request.setFilenames("file1, file2.xls");
		GlobusFileListing globusFile1 = new GlobusFileListing();
		globusFile1.setName("directory");
		globusFile1.setType("dir");
		when(globus.getFilesAtEndpoint("323")).thenReturn(Arrays.asList(globusFile1));
		when(globus.getFilesAtEndpoint("323/dir")).thenReturn(Arrays.asList(globusFile1));

		PackageValidationResponse result = service.matchFiles(request);

		verify(globus, times(2)).getFilesAtEndpoint(any(String.class));
		assertEquals(null, result.getGlobusFilesNotFoundInMetadata());
		assertEquals(Arrays.asList("file1", "file2.xls"), result.getMetadataFilesNotFoundInGlobus());
		assertEquals("323", result.getPackageId());
		assertEquals(Arrays.asList("file1", "file2.xls"), result.getFilesFromMetadata());
		assertEquals(Arrays.asList(), result.getFilesInGlobus());
	}

	@Test
	public void testMatchFilesIgnoresMetadataFile() throws JsonProcessingException, IOException {
		PackageFilesRequest request = new PackageFilesRequest();
		request.setPackageId("323");
		request.setFilenames("file1, file2.xls");
		GlobusFileListing globusFile1 = new GlobusFileListing();
		globusFile1.setName("file2.xls");
		GlobusFileListing globusFile2 = new GlobusFileListing();
		globusFile2.setName("file1");
		GlobusFileListing globusFile3 = new GlobusFileListing();
		globusFile3.setName("METADATA_stuff.xls");
		when(globus.getFilesAtEndpoint("323")).thenReturn(Arrays.asList(globusFile1, globusFile2, globusFile3));

		PackageValidationResponse result = service.matchFiles(request);

		assertEquals(null, result.getGlobusFilesNotFoundInMetadata());
		assertEquals(null, result.getMetadataFilesNotFoundInGlobus());
		assertEquals("323", result.getPackageId());
		assertEquals(Arrays.asList("file1", "file2.xls"), result.getFilesFromMetadata());
		assertEquals(Arrays.asList("file2.xls", "file1", "METADATA_stuff.xls"), result.getFilesInGlobus());
	}

	@Test
	public void testMatchFilesExtraFilesInGlobus() throws JsonProcessingException, IOException {
		PackageFilesRequest request = new PackageFilesRequest();
		request.setPackageId("323");
		request.setFilenames("file1, file2.xls");
		GlobusFileListing globusFile1 = new GlobusFileListing();
		globusFile1.setName("file2.xls");
		GlobusFileListing globusFile2 = new GlobusFileListing();
		globusFile2.setName("file1");
		GlobusFileListing globusFile3 = new GlobusFileListing();
		globusFile3.setName("extraGlobusFile.bam");
		when(globus.getFilesAtEndpoint("323")).thenReturn(Arrays.asList(globusFile1, globusFile2, globusFile3));

		PackageValidationResponse result = service.matchFiles(request);

		assertEquals(Arrays.asList("extraGlobusFile.bam"), result.getGlobusFilesNotFoundInMetadata());
		assertEquals(null, result.getMetadataFilesNotFoundInGlobus());
		assertEquals("323", result.getPackageId());
		assertEquals(Arrays.asList("file1", "file2.xls"), result.getFilesFromMetadata());
		assertEquals(Arrays.asList("file2.xls", "file1", "extraGlobusFile.bam"), result.getFilesInGlobus());
	}

	@Test
	public void testMatchFilesExtraFilesInMetadata() throws JsonProcessingException, IOException {
		PackageFilesRequest request = new PackageFilesRequest();
		request.setPackageId("323");
		request.setFilenames("file1, file2.xls, extraFile.txt");
		GlobusFileListing globusFile1 = new GlobusFileListing();
		globusFile1.setName("file2.xls");
		GlobusFileListing globusFile2 = new GlobusFileListing();
		globusFile2.setName("file1");
		when(globus.getFilesAtEndpoint("323")).thenReturn(Arrays.asList(globusFile1, globusFile2));

		PackageValidationResponse result = service.matchFiles(request);

		assertEquals(null, result.getGlobusFilesNotFoundInMetadata());
		assertEquals(Arrays.asList("extraFile.txt"), result.getMetadataFilesNotFoundInGlobus());
		assertEquals("323", result.getPackageId());
		assertEquals(Arrays.asList("file1", "file2.xls", "extraFile.txt"), result.getFilesFromMetadata());
		assertEquals(Arrays.asList("file2.xls", "file1"), result.getFilesInGlobus());
	}

}
