package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import org.springframework.test.util.ReflectionTestUtils;

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
	private AutoCloseable mocks;

	@Before
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);

		controller = new PackageController(packageService, logger, shibUserService, universalIdGenerator,
				globusService, dmdService);
		ReflectionTestUtils.setField(controller, "uploadStartedState", "UPLOAD_STARTED");
		ReflectionTestUtils.setField(controller, "metadataReceivedState", "METADATA_RECEIVED");
		ReflectionTestUtils.setField(controller, "uploadFailedState", "UPLOAD_FAILED");

	}

	@After
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
		verify(packageService).sendStateChangeEvent("universalId", "METADATA_RECEIVED", "true", null, "hostname");
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

}
