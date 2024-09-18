package org.miktmc.packages;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.logging.LoggingService;
import org.miktmc.shibboleth.ShibbolethUserService;
import org.miktmc.users.User;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

	private AutoCloseable mocks;

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);

		controller = new PackageController(packageService, logger, shibUserService, universalIdGenerator);
		ReflectionTestUtils.setField(controller, "uploadStartedState", "UPLOAD_STARTED");
		ReflectionTestUtils.setField(controller, "metadataReceivedState", "METADATA_RECEIVED");
		ReflectionTestUtils.setField(controller, "uploadFailedState", "UPLOAD_FAILED");
		ReflectionTestUtils.setField(controller, "filesReceivedState", "FILES_RECEIVED");
		ReflectionTestUtils.setField(controller, "uploadLockedState", "UPLOAD_LOCKED");
	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
		controller = null;
	}

	@Test
	public void testGetAllPackages() throws JSONException, IOException {
		List<PackageView> expectedPackages = Arrays.asList(new PackageView(new JSONObject()));
		when(packageService.findAllPackages()).thenReturn(expectedPackages);
		HttpServletRequest request = mock(HttpServletRequest.class);

		List<PackageView> packages = controller.getAllPackages(false, request);

		assertEquals(expectedPackages, packages);
		verify(packageService).findAllPackages();
		verify(packageService, times(0)).findMostPackages();
		verify(logger).logInfoMessage(PackageController.class, null, "Request for all packages", request);
	}

	@Test
	public void testGetAllPackages_whenShouldExclude() throws JSONException, IOException {
		List<PackageView> expectedPackages = Arrays.asList(new PackageView(new JSONObject()));
		when(packageService.findMostPackages()).thenReturn(expectedPackages);
		HttpServletRequest request = mock(HttpServletRequest.class);

		List<PackageView> packages = controller.getAllPackages(true, request);

		assertEquals(expectedPackages, packages);
		verify(packageService).findMostPackages();
		verify(packageService, times(0)).findAllPackages();
		verify(logger).logInfoMessage(PackageController.class, null, "Request for filtered packages", request);
	}

	@Test
	public void testPostPackageInformation_whenSaveThrowsException() throws Exception {
		String packageInfoString = "{\"packageType\":\"blah\"}";
		when(universalIdGenerator.generateUniversalId()).thenReturn("universalId");
		when(packageService.savePackageInformation(any(JSONObject.class), any(User.class), any(String.class)))
				.thenThrow(new JSONException("FAIL"));
		HttpServletRequest request = mock(HttpServletRequest.class);
		User user = mock(User.class);
		when(shibUserService.getUserNoHeaders(any(HttpServletRequest.class), any(JSONObject.class))).thenReturn(user);

		PackageResponse response = controller.postPackageInformation(packageInfoString, "hostname", request);

		assertEquals(null, response.getGlobusURL());
		verify(logger).logErrorMessage(PackageController.class, "universalId", "FAIL", request);
		verify(packageService).sendStateChangeEvent("universalId", "UPLOAD_FAILED", null, "FAIL", "hostname");
	}

	@Test
	public void testPostPackageInformation() throws Exception {
		String packageInfoString = "{\"packageType\":\"blah\"}";
		when(universalIdGenerator.generateUniversalId()).thenReturn("universalId");
		HttpServletRequest request = mock(HttpServletRequest.class);
		User user = mock(User.class);
		when(shibUserService.getUserNoHeaders(any(HttpServletRequest.class), any(JSONObject.class))).thenReturn(user);

		PackageResponse response = controller.postPackageInformation(packageInfoString, "hostname", request);

		assertEquals("universalId", response.getPackageId());
		assertEquals(null, response.getGlobusURL());
		ArgumentCaptor<JSONObject> jsonCaptor = ArgumentCaptor.forClass(JSONObject.class);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<String> packageIdCaptor = ArgumentCaptor.forClass(String.class);
		verify(packageService).savePackageInformation(jsonCaptor.capture(), userCaptor.capture(),
				packageIdCaptor.capture());
		assertEquals("blah", jsonCaptor.getValue().get("packageType"));
		assertEquals("universalId", packageIdCaptor.getValue());
		verify(logger).logInfoMessage(PackageController.class, "universalId",
				"Posting package info: {\"packageType\":\"blah\"}", request);
		verify(packageService).sendStateChangeEvent("universalId", "UPLOAD_STARTED", null, "hostname");
		verify(packageService).sendStateChangeEvent("universalId", "METADATA_RECEIVED", "false", null, "hostname");
	}

	@Test
	public void testPostFilesToPackage_whenNotInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("Host")).thenReturn("hostname");
        Package myPackage = new Package();
        myPackage.setStudy("study");
		Attachment attachment = new Attachment();
		attachment.setOriginalFileName("filename");
		attachment.setFileName("renamedFile");
		myPackage.setAttachments(Arrays.asList(attachment));
        when(packageService.findPackage("packageId")).thenReturn(myPackage);

		controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 2, request);

		verify(packageService).saveFile(file, "packageId","renamedFile", "study", true);
		verify(logger).logInfoMessage(PackageController.class, "packageId",
				"Posting file: renamedFile to package with id: packageId, filesize: 1,234, chunk: 2 out of 3 chunks",
				request);
	}

	@Test
	public void testPostFilesToPackage_whenInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("Host")).thenReturn("hostname");
        Package myPackage = new Package();
		Attachment attachment = new Attachment();
		attachment.setOriginalFileName("filename");
		attachment.setFileName("renamedFile");
		myPackage.setAttachments(Arrays.asList(attachment));
        myPackage.setStudy("study");
        when(packageService.findPackage("packageId")).thenReturn(myPackage);

		FileUploadResponse response = controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 0, request);

		assertEquals(true, response.isSuccess());
		verify(packageService).saveFile(file, "packageId", "renamedFile", "study", false);
		verify(logger).logInfoMessage(PackageController.class, "packageId",
				"Posting file: renamedFile to package with id: packageId, filesize: 1,234, chunk: 0 out of 3 chunks",
				request);
	}

	@Test
	public void testPostFilesToPackage_whenSaveThrowsException() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		doThrow(new Exception("NOPE")).when(packageService).saveFile(file, "packageId", "renamedFile", "study", false);
		when(request.getHeader("Host")).thenReturn("hostname");
        Package myPackage = new Package();
        myPackage.setStudy("study");
		Attachment attachment = new Attachment();
		attachment.setOriginalFileName("filename");
		attachment.setFileName("renamedFile");
		myPackage.setAttachments(Arrays.asList(attachment));
        when(packageService.findPackage("packageId")).thenReturn(myPackage);

		FileUploadResponse response = controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 0, request);

		assertEquals(false, response.isSuccess());
		verify(logger).logErrorMessage(PackageController.class, "packageId", "Unable to save file. NOPE", request);
		verify(packageService).sendStateChangeEvent("packageId", "UPLOAD_FAILED", null, "NOPE", "hostname");
	}

	@Test
	public void testPostFilesToPackage_whenCannotFindFileRename() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("Host")).thenReturn("hostname");
        Package myPackage = new Package();
		Attachment attachment = new Attachment();
		attachment.setOriginalFileName("someOtherFilename");
		attachment.setFileName("renamedFile");
		myPackage.setAttachments(Arrays.asList(attachment));
        myPackage.setStudy("study");
        when(packageService.findPackage("packageId")).thenReturn(myPackage);

		FileUploadResponse response = controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 0, request);

		assertEquals(false, response.isSuccess());
		verify(logger).logErrorMessage(PackageController.class, "packageId", "Unable to find file rename for file: filename", request);
		verify(packageService).sendStateChangeEvent("packageId", "UPLOAD_FAILED", null, "Unable to find file rename", "hostname");
	}

	@Test
	public void testFinishUpload() throws Exception {
		User user = mock(User.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
        Package myPackage = new Package();
        when(packageService.findPackage("3545")).thenReturn(myPackage);
		when(shibUserService.getUser(request)).thenReturn(user);
		when(packageService.validatePackage("3545", user)).thenReturn(true);

		FileUploadResponse result = controller.finishUpload("3545", "origin", request);

		verify(packageService).validatePackage("3545", user);
        verify(packageService).stripMetadata(myPackage);
		assertEquals(true, result.isSuccess());
		verify(packageService).sendStateChangeEvent("3545", "FILES_RECEIVED", null, "origin");
	}

	@Test
	public void testFinishUpload_whenMismatchedFiles() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		User user = mock(User.class);
		when(shibUserService.getUser(request)).thenReturn(user);
		when(packageService.validatePackage("3545", user)).thenReturn(false);

		FileUploadResponse result = controller.finishUpload("3545", "origin", request);

		verify(packageService).validatePackage("3545", user);
		assertEquals(false, result.isSuccess());
		verify(logger).logErrorMessage(PackageController.class, "3545", "The files on disk did not match the database:  3545",
				request);
		verify(packageService).sendStateChangeEvent("3545", "FILES_RECEIVED", null, "origin");
		verify(packageService).sendStateChangeEvent("3545", "UPLOAD_FAILED", null,
				"The files on disk did not match the database:  3545", "origin");
	}

	@Test
	public void testLockPackage() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(session.getAttribute("shibid")).thenReturn("shibId111");
		when(request.getSession(false)).thenReturn(session);
		
		controller.lockPackage("12345", "myHost", request);

		verify(packageService).sendStateChangeEvent("12345", "UPLOAD_LOCKED", null, "Locked by [shibId111]", "myHost");
	}

	@Test
	public void testAddFiles() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(session.getAttribute("shibid")).thenReturn("shibid");
		when(request.getSession(false)).thenReturn(session);
		JSONArray array = new JSONArray("[{\"fileName\":\"filename\"},{\"fileName2\":\"filename2\"}]");
		controller.postNewFiles("packageId", "{\"files\":" + array + "}", "host", request);
		ArgumentCaptor<JSONArray> jsonCaptor = ArgumentCaptor.forClass(JSONArray.class);
		ArgumentCaptor<String> packageIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> shibCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> replaceCaptor = ArgumentCaptor.forClass(Boolean.class);
		verify(packageService).addFiles(packageIdCaptor.capture(), jsonCaptor.capture(),shibCaptor.capture(), replaceCaptor.capture());
		assertEquals(jsonCaptor.getValue().toString(), array.toString());
		assertEquals("packageId", packageIdCaptor.getValue());
		assertEquals("shibid", shibCaptor.getValue());
		assertFalse(replaceCaptor.getValue());
	}

	@Test
	public void testReplaceFiles() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(session.getAttribute("shibid")).thenReturn("shibid");
		when(request.getSession(false)).thenReturn(session);
		when(packageService.canReplaceFile("packageId", "fileId", "filename")).thenReturn(true);
		when(packageService.deleteFile("packageId", "fileId", "shibid")).thenReturn(true);
		JSONArray array = new JSONArray("[{\"fileName\":\"filename\"}]");
		controller.replaceFile("packageId", "fileId", "{\"files\":" + array + "}", "host", request);
		ArgumentCaptor<JSONArray> jsonCaptor = ArgumentCaptor.forClass(JSONArray.class);
		ArgumentCaptor<String> packageIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> fileIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> shibCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> packageIdCaptor2 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> fileIdCaptor2 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> replaceCaptor = ArgumentCaptor.forClass(Boolean.class);
		verify(packageService).canReplaceFile(packageIdCaptor2.capture(), fileIdCaptor2.capture(), fileNameCaptor.capture());
		verify(packageService).deleteFile(packageIdCaptor.capture(), fileIdCaptor.capture(), shibCaptor.capture());
		verify(packageService).addFiles(packageIdCaptor.capture(), jsonCaptor.capture(),shibCaptor.capture(), replaceCaptor.capture());
		assertEquals(jsonCaptor.getValue().toString(), array.toString());
		assertEquals("packageId", packageIdCaptor.getValue());
		assertEquals("shibid", shibCaptor.getValue());
		assertEquals("fileId", fileIdCaptor.getValue());
		assertEquals("packageId", packageIdCaptor2.getValue());
		assertEquals("filename", fileNameCaptor.getValue());
		assertEquals("fileId", fileIdCaptor2.getValue());
		assertTrue(replaceCaptor.getValue());
	}

}
