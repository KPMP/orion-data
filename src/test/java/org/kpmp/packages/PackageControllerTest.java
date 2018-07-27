package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
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
	}

}
