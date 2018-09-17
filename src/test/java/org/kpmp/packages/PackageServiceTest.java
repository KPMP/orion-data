package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.UniversalIdGenerator;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
	private PackageService service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new PackageService(packageRepository, universalIdGenerator, packageFileHandler, packageZipService);
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

	@SuppressWarnings({ "unchecked" })
	@Test
	public void testSaveFile_whenInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		Package packageToUpdate = mock(Package.class);
		when(packageRepository.findByPackageId("packageId")).thenReturn(packageToUpdate);
		ArrayList<Attachment> files = mock(ArrayList.class);
		when(packageToUpdate.getAttachments()).thenReturn(files);
		boolean isInitialChunk = true;

		service.saveFile(file, "packageId", "filename", 322, isInitialChunk);

		verify(packageFileHandler).saveMultipartFile(file, "packageId", "filename", !isInitialChunk);
		verify(packageRepository).findByPackageId("packageId");
		verify(packageRepository).save(packageToUpdate);
		ArgumentCaptor<Attachment> attachmentCaptor = ArgumentCaptor.forClass(Attachment.class);
		verify(files).add(attachmentCaptor.capture());
		Attachment attachment = attachmentCaptor.getValue();
		assertEquals("filename", attachment.getFileName());
		assertEquals(322, attachment.getSize());
		verify(packageToUpdate).setAttachments(files);
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
}
