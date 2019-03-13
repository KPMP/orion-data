package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.users.User;
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
	public void testGetAllPackages() {
		List<PackageView> expectedPackages = Arrays.asList(new PackageView(new Package()));
		when(packageService.findAllPackages()).thenReturn(expectedPackages);

		List<PackageView> packages = controller.getAllPackages();

		assertEquals(expectedPackages, packages);
		verify(packageService).findAllPackages();
	}

	@Test
	public void testPostPackageInfo() throws Exception {
		JSONObject packageInfo = mock(JSONObject.class);
		when(packageService.savePackageInformation(packageInfo)).thenReturn("universalId");

		String universalId = controller.postPackageInformation(packageInfo);

		assertEquals("universalId", universalId);
		verify(packageService).savePackageInformation(packageInfo);
	}

	@Test
	public void testPostFilesToPackage_whenNotInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 2);

		verify(packageService).saveFile(file, "packageId", "filename", true);
	}

	@Test
	public void testPostFilesToPackage_whenInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 0);

		verify(packageService).saveFile(file, "packageId", "filename", false);
	}

	@Test
	public void testFinishUpload() throws Exception {
		Package packageInfo = mock(Package.class);
		when(packageInfo.getSubmitter()).thenReturn(mock(User.class));
		when(packageService.findPackage("3545")).thenReturn(packageInfo);
		when(packageInfo.getCreatedAt()).thenReturn(new Date());
		when(packageService.checkFilesExist(packageInfo)).thenReturn(true);
		controller.finishUpload("3545");

		verify(packageService).createZipFile("3545");
	}

	@Test
	public void testFinishUploadMismatchedFiles() throws Exception {
		Package packageInfo = mock(Package.class);
		when(packageInfo.getSubmitter()).thenReturn(mock(User.class));
		when(packageService.findPackage("3545")).thenReturn(packageInfo);
		when(packageInfo.getCreatedAt()).thenReturn(new Date());
		when(packageService.checkFilesExist(packageInfo)).thenReturn(false);
		controller.finishUpload("3545");

		verify(packageService, times(0)).createZipFile("3545");
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
