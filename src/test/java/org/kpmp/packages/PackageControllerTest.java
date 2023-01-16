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
import org.kpmp.dmd.DmdService;
import org.kpmp.globus.GlobusService;
import org.kpmp.logging.LoggingService;
import org.kpmp.shibboleth.ShibbolethUserService;
import org.kpmp.users.User;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

public class PackageControllerTest {

	private PackageController controller;
	@Mock
	private PackageService packageService;
	@Mock
	private LoggingService logger;
	@Mock
	private ShibbolethUserService shibUserService;
	@Mock
	private UniversalIdGenerator universalIdGenerator;
	@Mock
	private GlobusService globusService;

	@Mock
	private DmdService dmdService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new PackageController(packageService, logger, shibUserService, universalIdGenerator,
				globusService, dmdService);
		ReflectionTestUtils.setField(controller, "filesReceivedState", "FILES_RECEIVED");
		ReflectionTestUtils.setField(controller, "uploadStartedState", "UPLOAD_STARTED");
		ReflectionTestUtils.setField(controller, "metadataReceivedState", "METADATA_RECEIVED");
		ReflectionTestUtils.setField(controller, "uploadFailedState", "UPLOAD_FAILED");

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
	public void testPostPackageInformation_whenSaveThrowsException() throws Exception {
		String packageInfoString = "{\"packageType\":\"blah\"}";
		when(universalIdGenerator.generateUniversalId()).thenReturn("universalId");
		when(packageService.savePackageInformation(any(JSONObject.class), any(User.class), any(String.class)))
				.thenThrow(new JSONException("FAIL"));
		HttpServletRequest request = mock(HttpServletRequest.class);
		User user = mock(User.class);
		when(shibUserService.getUser(request)).thenReturn(user);

		PackageResponse response = controller.postPackageInformation(packageInfoString, "hostname", request);

		assertEquals(null, response.getGlobusURL());
		verify(logger).logErrorMessage(PackageController.class, "universalId", "FAIL", request);
		verify(packageService).sendStateChangeEvent("universalId", "UPLOAD_FAILED", null, "FAIL", "hostname");
	}

	@Test
	public void testPostPackageInformation_whenGoogleDriveServiceThrowsException() throws Exception {
		String packageInfoString = "{\"packageType\":\"blah\",\"largeFilesChecked\":true}";
		when(universalIdGenerator.generateUniversalId()).thenReturn("universalId");
		HttpServletRequest request = mock(HttpServletRequest.class);
		User user = mock(User.class);
		when(shibUserService.getUser(request)).thenReturn(user);
		when(globusService.createDirectory("universalId")).thenThrow(new IOException("NO DICE"));

		PackageResponse response = controller.postPackageInformation(packageInfoString, "hostname", request);

		assertEquals(null, response.getGlobusURL());
		verify(logger).logErrorMessage(PackageController.class, "universalId", "NO DICE", request);
		verify(packageService).sendStateChangeEvent("universalId", "UPLOAD_FAILED", null, "NO DICE", "hostname");
	}

	@Test
	public void testPostPackageInformation() throws Exception {
		String packageInfoString = "{\"packageType\":\"blah\"}";
		when(universalIdGenerator.generateUniversalId()).thenReturn("universalId");
		HttpServletRequest request = mock(HttpServletRequest.class);
		User user = mock(User.class);
		when(shibUserService.getUser(request)).thenReturn(user);

		PackageResponse response = controller.postPackageInformation(packageInfoString, "hostname", request);

		assertEquals("universalId", response.getPackageId());
		assertEquals(null, response.getGlobusURL());
		ArgumentCaptor<JSONObject> jsonCaptor = ArgumentCaptor.forClass(JSONObject.class);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<String> packageIdCaptor = ArgumentCaptor.forClass(String.class);
		verify(packageService).savePackageInformation(jsonCaptor.capture(), userCaptor.capture(),
				packageIdCaptor.capture());
		assertEquals(user, userCaptor.getValue());
		assertEquals("blah", jsonCaptor.getValue().get("packageType"));
		assertEquals("universalId", packageIdCaptor.getValue());
		verify(logger).logInfoMessage(PackageController.class, "universalId",
				"Posting package info: {\"packageType\":\"blah\"}", request);
		verify(packageService).sendStateChangeEvent("universalId", "UPLOAD_STARTED", null, "hostname");
		verify(packageService).sendStateChangeEvent("universalId", "METADATA_RECEIVED", "false", null, "hostname");
	}

	@Test
	public void testPostPackageInformationLargeFile() throws Exception {
		String packageInfoString = "{\"packageType\":\"blah\",\"largeFilesChecked\":true}";
		when(universalIdGenerator.generateUniversalId()).thenReturn("universalId");

		HttpServletRequest request = mock(HttpServletRequest.class);
		User user = mock(User.class);
		when(shibUserService.getUser(request)).thenReturn(user);
		when(globusService.createDirectory("universalId")).thenReturn("theWholeURL");

		PackageResponse response = controller.postPackageInformation(packageInfoString, "hostname", request);

		assertEquals("universalId", response.getPackageId());
		assertEquals("theWholeURL", response.getGlobusURL());
		ArgumentCaptor<JSONObject> jsonCaptor = ArgumentCaptor.forClass(JSONObject.class);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<String> packageIdCaptor = ArgumentCaptor.forClass(String.class);
		verify(packageService).savePackageInformation(jsonCaptor.capture(), userCaptor.capture(),
				packageIdCaptor.capture());
		verify(packageService).savePackageInformation(jsonCaptor.capture(), userCaptor.capture(),
				packageIdCaptor.capture());
		assertEquals(user, userCaptor.getValue());
		assertEquals("blah", jsonCaptor.getValue().get("packageType"));
		assertEquals("universalId", packageIdCaptor.getValue());
		verify(logger).logInfoMessage(PackageController.class, "universalId",
				"Posting package info: {\"largeFilesChecked\":true,\"packageType\":\"blah\"}", request);
		verify(packageService).sendStateChangeEvent("universalId", "UPLOAD_STARTED", null, "hostname");
		verify(packageService).sendStateChangeEvent("universalId", "METADATA_RECEIVED", "true", "theWholeURL",
				"hostname");
	}

	@Test
	public void testPostFilesToPackage_whenNotInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("Host")).thenReturn("hostname");

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
		when(request.getHeader("Host")).thenReturn("hostname");

		FileUploadResponse response = controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 0, request);

		assertEquals(true, response.isSuccess());
		verify(packageService).saveFile(file, "packageId", "filename", false);
		verify(logger).logInfoMessage(PackageController.class, "packageId",
				"Posting file: filename to package with id: packageId, filesize: 1,234, chunk: 0 out of 3 chunks",
				request);
	}

	@Test
	public void testPostFilesToPackage_whenSaveThrowsException() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		doThrow(new Exception("NOPE")).when(packageService).saveFile(file, "packageId", "filename", false);
		when(request.getHeader("Host")).thenReturn("hostname");

		FileUploadResponse response = controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 0, request);

		assertEquals(false, response.isSuccess());
		verify(logger).logErrorMessage(PackageController.class, "packageId", "NOPE", request);
		verify(packageService).sendStateChangeEvent("packageId", "UPLOAD_FAILED", null, "NOPE", "hostname");
	}

	@Test
	public void testFinishUpload() throws Exception {
		User user = mock(User.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(shibUserService.getUser(request)).thenReturn(user);
		when(packageService.validatePackage("3545", user)).thenReturn(true);

		FileUploadResponse result = controller.finishUpload("3545", "origin", request);

		verify(packageService).createZipFile("3545", "origin", user);
		verify(packageService).validatePackage("3545", user);
		assertEquals(true, result.isSuccess());
		verify(logger).logInfoMessage(PackageController.class, "3545", "Finishing file upload with packageId:  3545",
				request);
		verify(packageService).sendStateChangeEvent("3545", "FILES_RECEIVED", null, "origin");
	}

	@Test
	public void testFinishUpload_whenCreateZipThrows() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		User user = mock(User.class);
		when(shibUserService.getUser(request)).thenReturn(user);
		when(packageService.validatePackage("3545", user)).thenReturn(true);
		doThrow(new JSONException("OOF")).when(packageService).createZipFile("3545", "origin", user);

		FileUploadResponse result = controller.finishUpload("3545", "origin", request);

		verify(packageService).createZipFile("3545", "origin", user);
		verify(packageService).validatePackage("3545", user);
		assertEquals(false, result.isSuccess());
		verify(logger).logErrorMessage(PackageController.class, "3545", "error getting metadata for package id:  3545",
				request);
		verify(packageService).sendStateChangeEvent("3545", "FILES_RECEIVED", null, "origin");
		verify(packageService).sendStateChangeEvent("3545", "UPLOAD_FAILED", null,
				"error getting metadata for package id:  3545", "origin");
	}

	@Test
	public void testFinishUpload_whenMismatchedFiles() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		User user = mock(User.class);
		when(shibUserService.getUser(request)).thenReturn(user);
		when(packageService.validatePackage("3545", user)).thenReturn(false);

		FileUploadResponse result = controller.finishUpload("3545", "origin", request);

		verify(packageService, times(0)).createZipFile("3545", "origin", user);
		verify(packageService).validatePackage("3545", user);
		assertEquals(false, result.isSuccess());
		verify(logger).logErrorMessage(PackageController.class, "3545", "Unable to zip package with package id:  3545",
				request);
		verify(packageService).sendStateChangeEvent("3545", "FILES_RECEIVED", null, "origin");
		verify(packageService).sendStateChangeEvent("3545", "UPLOAD_FAILED", null,
				"Unable to zip package with package id:  3545", "origin");
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

	@SuppressWarnings("rawtypes")
	@Test
	public void testMovePackageFiles() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		ResponseEntity responseEntity = controller.movePackageFiles("3545", request);
		verify(packageService).movePackageFiles("3545");
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("Moving files for package 3545", responseEntity.getBody());
	}

}
