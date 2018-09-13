package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
	public void testGetAllPackagess() {
		List<Package> expectedPackages = Arrays.asList(new Package());
		when(packageService.findAllPackages()).thenReturn(expectedPackages);

		List<Package> packages = controller.getAllPackages();

		assertEquals(expectedPackages, packages);
		verify(packageService).findAllPackages();
	}

	@Test
	public void testPostPackageInfo() throws Exception {
		Package packageInfo = new Package();
		Package savedPackage = mock(Package.class);
		when(savedPackage.getPackageId()).thenReturn("universalId");
		when(packageService.savePackageInformation(packageInfo)).thenReturn(savedPackage);

		String universalId = controller.postPackageInfo(packageInfo);

		assertEquals("universalId", universalId);
		verify(packageService).savePackageInformation(packageInfo);
	}

}
