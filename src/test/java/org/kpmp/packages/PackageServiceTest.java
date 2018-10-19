package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.UniversalIdGenerator;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

public class PackageServiceTest {

	@Mock
	private PackageRepository packageRepository;
	@Mock
	private UniversalIdGenerator universalIdGenerator;
	@Mock
	private PackageFileHandler packageFileHandler;
	@Mock
	private PackageZipService packageZipService;
	@Mock
	private FilePathHelper filePathHelper;
	private PackageService service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new PackageService(packageRepository, universalIdGenerator, packageFileHandler, packageZipService,
				filePathHelper);
	}

	@After
	public void tearDown() throws Exception {
		service = null;
	}

	@Test
	public void testFindAllPackages() {
		Package uploadedPackage = mock(Package.class);
		when(uploadedPackage.getPackageId()).thenReturn("packageId");
		List<Package> expectedResults = Arrays.asList(uploadedPackage);
		when(packageRepository.findAll(new Sort(Sort.Direction.DESC, "createdAt"))).thenReturn(expectedResults);
		when(filePathHelper.getZipFileName("packageId")).thenReturn("/data/packageId/packageId.zip");

		List<PackageView> packages = service.findAllPackages();

		assertEquals(false, packages.get(0).isDownloadable());
		verify(packageRepository).findAll(new Sort(Sort.Direction.DESC, "createdAt"));
	}

	@Test
	public void testSavePackageInformation() throws Exception {
		Package packageInfo = new Package();
		when(universalIdGenerator.generateUniversalId()).thenReturn("new universal id");
		Package expectedPackage = mock(Package.class);
		when(packageRepository.save(packageInfo)).thenReturn(expectedPackage);

		Package savedPackage = service.savePackageInformation(packageInfo);

		assertEquals("new universal id", packageInfo.getPackageId());
		assertNotNull(packageInfo.getCreatedAt());
		verify(packageRepository).save(packageInfo);
		assertEquals(expectedPackage, savedPackage);
	}

	@Test
	public void testFindPackage() throws Exception {
		Package expectedPackage = mock(Package.class);
		when(packageRepository.findByPackageId("packageId")).thenReturn(expectedPackage);

		Package actualPackage = service.findPackage("packageId");

		assertEquals(expectedPackage, actualPackage);
		verify(packageRepository).findByPackageId("packageId");
	}

	@Test
	public void testSaveFile_whenFilenameMetadataJson() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		boolean isInitialChunk = true;

		service.saveFile(file, "packageId", "metadata.json", 322, isInitialChunk);

		verify(packageFileHandler).saveMultipartFile(file, "packageId", "metadata_user.json", !isInitialChunk);
	}

	@Test
	public void testSaveFile_whenInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		Package packageToUpdate = mock(Package.class);
		when(packageRepository.findByPackageId("packageId")).thenReturn(packageToUpdate);
		boolean isInitialChunk = true;

		service.saveFile(file, "packageId", "filename", 322, isInitialChunk);

		verify(packageFileHandler).saveMultipartFile(file, "packageId", "filename", !isInitialChunk);
	}

	@SuppressWarnings({ "unchecked" })
	@Test
	public void testSaveFile_whenNotInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		Package packageToUpdate = mock(Package.class);
		when(packageRepository.findByPackageId("packageId")).thenReturn(packageToUpdate);
		ArrayList<Attachment> files = mock(ArrayList.class);
		when(packageToUpdate.getAttachments()).thenReturn(files);
		boolean isInitialChunk = false;

		service.saveFile(file, "packageId", "filename", 322, isInitialChunk);

		verify(packageFileHandler).saveMultipartFile(file, "packageId", "filename", !isInitialChunk);
		verify(packageRepository, times(0)).findByPackageId("packageId");
		verify(packageRepository, times(0)).save(packageToUpdate);
	}

	@Test
	public void testSaveFile_whenException() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		boolean isInitialChunk = false;
		Exception expectedException = new IOException();
		doThrow(expectedException).when(packageFileHandler).saveMultipartFile(file, "packageId", "filename",
				!isInitialChunk);

		try {
			service.saveFile(file, "packageId", "filename", 322, isInitialChunk);
			fail("Should have thrown exception");
		} catch (Exception actual) {
			assertEquals(expectedException, actual);
		}
	}

	@Test
	public void testGetPackageFile_doesntExist() throws Exception {
		String packageId = "abc-345";
		Path packagePath = Files.createTempDirectory("data");
		packagePath.toFile().deleteOnExit();
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
		when(filePathHelper.getPackagePath(packageId)).thenReturn(packagePath.toString());

		Path actualFilePath = service.getPackageFile(packageId);

		assertEquals(expectedFilePathString, actualFilePath.toString());
		assertTrue(actualFilePath.toFile().exists());
	}
}
