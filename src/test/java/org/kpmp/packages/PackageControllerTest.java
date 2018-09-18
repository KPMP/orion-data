package org.kpmp.packages;

import static org.assertj.core.api.Fail.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PackageControllerTest {

	@Mock
	private PackageService packageService;
	private PackageController controller;
	private FilePathHelper filePathHelper;
	private PackageRepository packageRepository;

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
	public void testGetAllPackages() {
		List<Package> expectedPackages = Arrays.asList(new Package());
		when(packageService.findAllPackages()).thenReturn(expectedPackages);

		List<Package> packages = controller.getAllPackages();

		assertEquals(expectedPackages, packages);
		verify(packageService).findAllPackages();
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
