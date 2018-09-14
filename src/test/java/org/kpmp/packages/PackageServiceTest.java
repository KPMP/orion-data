package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PackageServiceTest {

	@Mock
	private PackageRepository packageRepository;
	@Mock
	private FilePathHelper filePathHelper;
	private PackageService service;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new PackageService(packageRepository, filePathHelper);
	}

	@After
	public void tearDown() throws Exception {
		service = null;
	}

	@Test
	public void testFindAllPackages() {
		List<Package> expectedResults = Arrays.asList(new Package());
		when(packageRepository.findAll()).thenReturn(expectedResults);

		List<Package> packages = service.findAllPackages();

		assertEquals(expectedResults, packages);
		verify(packageRepository).findAll();
	}

	@Test
	public void testGetPackageFile_doesntExist() throws Exception {
		String packageId = "abc-345";
		Path packagePath = Files.createTempDirectory("data");
		packagePath.toFile().deleteOnExit();
		String expectedFilePathString = Paths.get(packagePath.toString(), packageId + ".zip").toString();
		when(filePathHelper.getPackagePath("", packageId)).thenReturn(packagePath.toString());

		try {
			service.getPackageFile(packageId);
			fail("expected a RuntimeException");
		} catch (RuntimeException expectedException) {
			assertEquals("The file was not found: " + packageId + ".zip", expectedException.getMessage());
		}
	}

	@Test
	public void testGetPackageFile_exists() throws Exception {
		String packageId = "abc-345";
		Path packagePath = Files.createTempDirectory("data");
		packagePath.toFile().deleteOnExit();
		String expectedFilePathString = Paths.get(packagePath.toString(), packageId + ".zip").toString();
		File file = new File(expectedFilePathString);
		file.createNewFile();
		file.deleteOnExit();
		when(filePathHelper.getPackagePath("", packageId)).thenReturn(packagePath.toString());

		Path actualFilePath = service.getPackageFile(packageId);
		assertEquals(expectedFilePathString, actualFilePath.toString());
		assertTrue(actualFilePath.toFile().exists());
	}

}
