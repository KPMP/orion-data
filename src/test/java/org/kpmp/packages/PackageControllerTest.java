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
import org.kpmp.logging.LoggingService;
import org.kpmp.shibboleth.ShibbolethUserService;
import org.kpmp.users.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public class PackageControllerTest {

	private PackageController controller;
	@Mock
	private PackageService packageService;
	@Mock
	private LoggingService logger;
	@Mock
	private ShibbolethUserService shibUserService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new PackageController(packageService, logger, shibUserService);
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

		List<PackageView> packages = controller.getAllPackages(request);

		assertEquals(expectedPackages, packages);
		verify(packageService).findAllPackages();
		verify(logger).logInfoMessage(PackageController.class, null, "Request for all packages", request);
	}

	@Test
	public void testPostPackageInfo() throws Exception {
		String packageInfoString = "{}";
		when(packageService.savePackageInformation(any(JSONObject.class))).thenReturn("universalId");
		HttpServletRequest request = mock(HttpServletRequest.class);

		String universalId = controller.postPackageInformation(packageInfoString, request);

		assertEquals("universalId", universalId);
		verify(packageService).savePackageInformation(any(JSONObject.class));
		verify(logger).logInfoMessage(PackageController.class, null, "Posting package info: {}", request);
	}

	@Test
	public void testPostFilesToPackage_whenNotInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		HttpServletRequest request = mock(HttpServletRequest.class);

		controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 2, request);

		verify(packageService).saveFile(file, "packageId", "filename", true);
		verify(logger).logInfoMessage(PackageController.class, "packageId",
				"Posting file: filename to package with id: packageId, filesize: 1,234, chunk: 2 out of 3 chunks",
				request);
	}

	@Test
	public void testPostFilesToPackage_whenInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		HttpServletRequest request = mock(HttpServletRequest.class);

		controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 0, request);

		verify(packageService).saveFile(file, "packageId", "filename", false);
		verify(logger).logInfoMessage(PackageController.class, "packageId",
				"Posting file: filename to package with id: packageId, filesize: 1,234, chunk: 0 out of 3 chunks",
				request);
	}

	@Test
	public void testFinishUpload() throws Exception {
		User user = mock(User.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(shibUserService.getUser(request)).thenReturn(user);
		when(packageService.validatePackageForZipping("3545", user)).thenReturn(true);

		FileUploadResponse result = controller.finishUpload("3545", request);

		verify(packageService).createZipFile("3545", "origin");
		verify(packageService).validatePackageForZipping("3545", user);
		assertEquals(true, result.isSuccess());
		verify(logger).logInfoMessage(PackageController.class, "3545", "Finishing file upload with packageId:  3545",
				request);
	}

	@Test
	public void testFinishUpload_whenCreateZipThrows() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		User user = mock(User.class);
		when(shibUserService.getUser(request)).thenReturn(user);
		when(packageService.validatePackageForZipping("3545", user)).thenReturn(true);
		doThrow(new JSONException("OOF")).when(packageService).createZipFile("3545", "origin");

		FileUploadResponse result = controller.finishUpload("3545", request);

		verify(packageService).createZipFile("3545", "origin");
		verify(packageService).validatePackageForZipping("3545", user);
		assertEquals(false, result.isSuccess());
		verify(logger).logErrorMessage(PackageController.class, "3545", "error getting metadata for package id:  3545",
				request);
	}

	@Test
	public void testFinishUpload_whenMismatchedFiles() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		User user = mock(User.class);
		when(shibUserService.getUser(request)).thenReturn(user);
		when(packageService.validatePackageForZipping("3545", user)).thenReturn(false);

		FileUploadResponse result = controller.finishUpload("3545", request);

		verify(packageService, times(0)).createZipFile("3545", "origin");
		verify(packageService).validatePackageForZipping("3545", user);
		assertEquals(false, result.isSuccess());
		verify(logger).logErrorMessage(PackageController.class, "3545", "Unable to zip package with package id:  3545",
				request);
	}

	@Test
	public void testDownloadPackage() throws Exception {
		String packageId = "1234";
		Path filePath = Paths.get("foo", "1234.zip");
		when(packageService.getPackageFile(packageId)).thenReturn(filePath);
		HttpServletRequest request = mock(HttpServletRequest.class);

		ResponseEntity<Resource> response = controller.downloadPackage(packageId, request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).iterator().next().contains("1234.zip"));
		verify(logger).logInfoMessage(PackageController.class, packageId,
				"Requesting package download with id 1234, filename URL [file:" + filePath.toAbsolutePath() + "]",
				request);
	}

	@Test
	public void testDownloadPackage_serviceException() throws Exception {
		String packageId = "1234";
		Path packagePath = mock(Path.class);
		when(packagePath.toUri()).thenThrow(new RuntimeException("angry"));
		when(packageService.getPackageFile(packageId)).thenReturn(packagePath);
		HttpServletRequest request = mock(HttpServletRequest.class);

		try {
			controller.downloadPackage(packageId, request);
			fail("expected RuntimeException");
		} catch (RuntimeException expectedException) {
			assertEquals("java.lang.RuntimeException: angry", expectedException.getMessage());
			verify(logger).logErrorMessage(PackageController.class, packageId,
					"Unable to get package zip with id: 1234", request);
		}
	}

}
