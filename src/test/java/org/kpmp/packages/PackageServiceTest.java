package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.users.User;
import org.kpmp.dmd.DmdService;
import org.kpmp.logging.LoggingService;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

public class PackageServiceTest {

	@Mock
	private CustomPackageRepository packageRepository;
	private PackageService service;
	@Mock
	private DmdService dmdService;
	@Mock
	private LoggingService logger;
	@Mock
	private StateHandlerService stateHandlerService;
	private AutoCloseable mocks;

	@Before
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		service = new PackageService(packageRepository, stateHandlerService, dmdService, logger);
		ReflectionTestUtils.setField(service, "packageTypesToExclude", Arrays.asList("Electron Microscopy Images"));
	}

	@After
	public void tearDown() throws Exception {
		mocks.close();
		service = null;
	}

	@Test
	public void testSendStateChangeEvent() throws Exception {
		service.sendStateChangeEvent("packageId1", "stateString", null, "codicil", "hostname");

		verify(stateHandlerService).sendStateChange("packageId1", "stateString", null, "codicil", "hostname");
	}

	@Test
	public void testFindAllPackages() throws JSONException, IOException {
		State newState = mock(State.class);
		HashMap<String, State> stateMap = new HashMap<String, State>();
		stateMap.put("packageId", newState);
		stateMap.put("anotherId", mock(State.class));
		when(stateHandlerService.getState()).thenReturn(stateMap);
		JSONObject uploadedPackage = mock(JSONObject.class);
		when(uploadedPackage.toString()).thenReturn("");
		when(uploadedPackage.getString("_id")).thenReturn("packageId");
		List<JSONObject> expectedResults = Arrays.asList(uploadedPackage);
		when(packageRepository.findAll()).thenReturn(expectedResults);

		List<PackageView> packages = service.findAllPackages();

		assertEquals(newState, packages.get(0).getState());
		verify(packageRepository).findAll();
		verify(stateHandlerService).getState();
	}

	@Test
	public void testfindMostPackages() throws JSONException, IOException {
		State newState = mock(State.class);
		HashMap<String, State> stateMap = new HashMap<String, State>();
		stateMap.put("packageId", newState);
		stateMap.put("anotherId", mock(State.class));
		when(stateHandlerService.getState()).thenReturn(stateMap);
		JSONObject excludedPackage = mock(JSONObject.class);
		when(excludedPackage.toString()).thenReturn("");
		when(excludedPackage.getString("_id")).thenReturn("packageId");
		when(excludedPackage.getString("packageType")).thenReturn("Electron Microscopy Images");
		JSONObject uploadedPackage = mock(JSONObject.class);
		when(uploadedPackage.toString()).thenReturn("");
		when(uploadedPackage.getString("_id")).thenReturn("packageId");
		when(uploadedPackage.getString("packageType")).thenReturn("Anything Else");
		when(packageRepository.findAll()).thenReturn(Arrays.asList(excludedPackage, uploadedPackage));

		List<PackageView> packages = service.findMostPackages();

		assertEquals(1, packages.size());
		assertEquals(newState, packages.get(0).getState());
		verify(packageRepository).findAll();
		verify(stateHandlerService).getState();
	}


	@Test
	public void testSavePackageInformation() throws Exception {
		JSONObject packageMetadata = mock(JSONObject.class);
		User user = mock(User.class);
		Package myPackage = new Package();
		myPackage.setPackageId("awesomeNewId");
		when(packageRepository.findByPackageId("awesomeNewId")).thenReturn(myPackage);
		String packageId = service.savePackageInformation(packageMetadata, user, "awesomeNewId");
		assertEquals("awesomeNewId", packageId);
		verify(dmdService).convertAndSendNewPackage(myPackage);
		verify(packageRepository).saveDynamicForm(packageMetadata, user, "awesomeNewId");
	}

	@Test
	public void testFindPackage() throws Exception {
		Package expectedPackage = mock(Package.class);
		when(packageRepository.findByPackageId("packageId")).thenReturn(expectedPackage);

		Package actualPackage = service.findPackage("packageId");

		assertEquals(expectedPackage, actualPackage);
		verify(packageRepository).findByPackageId("packageId");
	}

}
