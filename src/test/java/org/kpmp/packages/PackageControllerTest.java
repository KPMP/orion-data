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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.JWTHandler;
import org.kpmp.logging.LoggingService;
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
	@Mock
	private LoggingService logger;
	@Mock
	private JWTHandler jwtHandler;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new PackageController(packageService, logger, jwtHandler);
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testGetAllPackages() throws JSONException, IOException {
		List<PackageView> expectedPackages = Arrays.asList(new PackageView(new JSONObject()));
		when(packageService.findAllPackages()).thenReturn(expectedPackages);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");
		when(request.getRequestURI()).thenReturn("/v1/packages");

		List<PackageView> packages = controller.getAllPackages(request);

		assertEquals(expectedPackages, packages);
		verify(packageService).findAllPackages();
		verify(logger).logInfoMessage(PackageController.class, "userID", null, "/v1/packages",
				"Request for all packages");
	}

	@Test
	public void testPostPackageInfo() throws Exception {
		String packageInfoString = "{}";
		when(packageService.savePackageInformation(any(JSONObject.class))).thenReturn("universalId");
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/v1/packages");
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");

		String universalId = controller.postPackageInformation(packageInfoString, request);

		assertEquals("universalId", universalId);
		verify(packageService).savePackageInformation(any(JSONObject.class));
		verify(logger).logInfoMessage(PackageController.class, "userID", null, "/v1/packages",
				"Posting package info: {}");
	}

	@Test
	public void testPostFilesToPackage_whenNotInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/v1/packages/packageId/files");
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");

		controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 2, request);

		verify(packageService).saveFile(file, "packageId", "filename", true);
		verify(logger).logInfoMessage(PackageController.class, "userID", "packageId", "/v1/packages/packageId/files",
				"Posting file: filename to package with id: packageId, filesize: 1,234, chunk: 2 out of 3 chunks");
	}

	@Test
	public void testPostFilesToPackage_whenInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/v1/packages/packageId/files");
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");

		controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 0, request);

		verify(packageService).saveFile(file, "packageId", "filename", false);
		verify(logger).logInfoMessage(PackageController.class, "userID", "packageId", "/v1/packages/packageId/files",
				"Posting file: filename to package with id: packageId, filesize: 1,234, chunk: 0 out of 3 chunks");
	}

	@Test
	public void testFinishUpload() throws Exception {
		when(packageService.validatePackageForZipping("3545")).thenReturn(true);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/v1/packages/3545/files/finish");
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");

		FileUploadResponse result = controller.finishUpload("3545", request);

		verify(packageService).createZipFile("3545");
		verify(packageService).validatePackageForZipping("3545");
		assertEquals(true, result.isSuccess());
		verify(logger).logInfoMessage(PackageController.class, "userID", "3545", "/v1/packages/3545/files/finish",
				"Finishing file upload with packageId:  3545");
	}

	@Test
	public void testFinishUpload_whenCreateZipThrows() throws Exception {
		when(packageService.validatePackageForZipping("3545")).thenReturn(true);
		doThrow(new JSONException("OOF")).when(packageService).createZipFile("3545");
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/v1/packages/3545/files/finish");
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");

		FileUploadResponse result = controller.finishUpload("3545", request);

		verify(packageService).createZipFile("3545");
		verify(packageService).validatePackageForZipping("3545");
		assertEquals(false, result.isSuccess());
		verify(logger).logErrorMessage(PackageController.class, "userID", "3545", "/v1/packages/3545/files/finish",
				"error getting metadata for package id:  3545");
	}

	@Test
	public void testFinishUpload_whenMismatchedFiles() throws Exception {
		when(packageService.validatePackageForZipping("3545")).thenReturn(false);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/v1/packages/3545/files/finish");
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");

		FileUploadResponse result = controller.finishUpload("3545", request);

		verify(packageService, times(0)).createZipFile("3545");
		verify(packageService).validatePackageForZipping("3545");
		assertEquals(false, result.isSuccess());
		verify(logger).logErrorMessage(PackageController.class, "userID", "3545", "/v1/packages/3545/files/finish",
				"Unable to zip package with package id:  3545");
	}

	@Test
	public void testDownloadPackage() throws Exception {
		String packageId = "1234";
		when(packageService.getPackageFile(packageId)).thenReturn(Paths.get("foo", "1234.zip"));
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/v1/packages/1234/files");
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");

		ResponseEntity<Resource> response = controller.downloadPackage(packageId, request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).iterator().next().contains("1234.zip"));
		verify(logger).logInfoMessage(PackageController.class, "userID", packageId, "/v1/packages/1234/files",
				"Requesting package download with id 1234, filename URL [file:/Users/rlreamy/git/kpmp/orion-data/foo/1234.zip]");
	}

	@Test
	public void testDownloadPackage_serviceException() throws Exception {
		String packageId = "1234";
		Path packagePath = mock(Path.class);
		when(packagePath.toUri()).thenThrow(new RuntimeException("angry"));
		when(packageService.getPackageFile(packageId)).thenReturn(packagePath);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/v1/packages/1234/files");
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");

		try {
			controller.downloadPackage(packageId, request);
			fail("expected RuntimeException");
		} catch (RuntimeException expectedException) {
			assertEquals("java.lang.RuntimeException: angry", expectedException.getMessage());
			verify(logger).logErrorMessage(PackageController.class, "userID", packageId, "/v1/packages/1234/files",
					"Unable to get package zip with id: 1234");
		}
	}

}
