package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public class PackageControllerTest {

	@Mock
	private PackageService packageService;
	private PackageController controller;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new PackageController(packageService);
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testGetAllPackages() throws JSONException, IOException {
		List<PackageView> expectedPackages = Arrays.asList(new PackageView(new JSONObject()));
		when(packageService.findAllPackages()).thenReturn(expectedPackages);

		List<PackageView> packages = controller.getAllPackages();

		assertEquals(expectedPackages, packages);
		verify(packageService).findAllPackages();
	}

	@Test
	public void testPostPackageInfo() throws Exception {
		String packageInfoString = "{}";
		when(packageService.savePackageInformation(any(JSONObject.class))).thenReturn("universalId");

		String universalId = controller.postPackageInformation(packageInfoString);

		assertEquals("universalId", universalId);
		verify(packageService).savePackageInformation(any(JSONObject.class));
	}

	@Test
	public void testPostFilesToPackage_whenNotInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 2);

		verify(packageService).saveFile(file, "packageId", "filename", 1234, true);
	}

	@Test
	public void testPostFilesToPackage_whenInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 0);

		verify(packageService).saveFile(file, "packageId", "filename", 1234,false);
	}

	@Test
	public void testFinishUpload() throws Exception {
		when(packageService.checkFilesExist("3545")).thenReturn(true);

		FileUploadResponse result = controller.finishUpload("3545");

		verify(packageService).createZipFile("3545");
		verify(packageService).checkFilesExist("3545");
		assertEquals(true, result.isSuccess());
	}

	@Test
	public void testFinishUpload_whenCreateZipThrows() throws Exception {
		when(packageService.checkFilesExist("3545")).thenReturn(true);
		doThrow(new JSONException("OOF")).when(packageService).createZipFile("3545");

		FileUploadResponse result = controller.finishUpload("3545");

		verify(packageService).createZipFile("3545");
		verify(packageService).checkFilesExist("3545");
		assertEquals(false, result.isSuccess());
	}

	@Test
	public void testFinishUpload_whenMismatchedFiles() throws Exception {
		when(packageService.checkFilesExist("3545")).thenReturn(false);

		FileUploadResponse result = controller.finishUpload("3545");

		verify(packageService, times(0)).createZipFile("3545");
		verify(packageService).checkFilesExist("3545");
		assertEquals(false, result.isSuccess());
	}

	@Test
	public void testDownloadPackage() throws Exception {
		String packageId = "1234";
		when(packageService.getPackageFile(packageId)).thenReturn(Paths.get("foo", "1234.zip"));

		ResponseEntity<Resource> response = controller.downloadPackage(packageId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).iterator().next().contains("1234.zip"));
	}

	@Test
	public void testDownloadPackage_serviceException() throws Exception {
		String packageId = "1234";
		when(packageService.getPackageFile(packageId)).thenThrow(new RuntimeException("angry"));

		try {
			controller.downloadPackage(packageId);
			fail("expected RuntimeException");
		} catch (RuntimeException expectedException) {
			assertEquals("angry", expectedException.getMessage());
		}
	}

}
