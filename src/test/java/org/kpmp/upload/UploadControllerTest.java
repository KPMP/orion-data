package org.kpmp.upload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.dao.deprecated.FileMetadataEntries;
import org.kpmp.dao.deprecated.FileSubmission;
import org.kpmp.dao.deprecated.InstitutionDemographics;
import org.kpmp.dao.deprecated.PackageType;
import org.kpmp.dao.deprecated.PackageTypeOther;
import org.kpmp.dao.deprecated.SubmitterDemographics;
import org.kpmp.dao.deprecated.UploadPackage;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

public class UploadControllerTest {

	private UploadController controller;

	@Mock
	private UploadService uploadService;
	@Mock
	private FileHandler fileHandler;
	@Mock
	private FilePathHelper filePathHelper;
	@Mock
	private MetadataHandler metadataHandler;
	private HttpSession session;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new UploadController(uploadService, fileHandler, filePathHelper, metadataHandler);
		session = mock(HttpSession.class);
		ReflectionTestUtils.setField(filePathHelper, "metadataFileName", "metadata.json");
		ReflectionTestUtils.setField(filePathHelper, "basePath", File.separator + "data");
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testUploadPackageInfo_whenPackageTypeIsNotOther() {
		PackageInformation packageInformation = mock(PackageInformation.class);
		when(packageInformation.getPackageType()).thenReturn("something");
		when(uploadService.saveUploadPackage(packageInformation, null)).thenReturn(5);
		when(uploadService.saveSubmitterInfo(packageInformation)).thenReturn(55);
		when(uploadService.findInstitutionId(packageInformation)).thenReturn(66);
		when(uploadService.createUploadPackage(packageInformation, null)).thenReturn(new UploadPackage());

		UploadPackageIds packageIds = controller.uploadPackageInfo(packageInformation, session);

		verify(uploadService).createUploadPackage(packageInformation, null);
		verify(uploadService).saveUploadPackage(packageInformation, null);
		verify(uploadService).saveSubmitterInfo(packageInformation);
		verify(uploadService).findInstitutionId(packageInformation);
		verify(uploadService, times(0)).savePackageTypeOther(any(String.class));
		assertEquals(5, packageIds.getPackageId());
		assertEquals(55, packageIds.getSubmitterId());
		assertEquals(66, packageIds.getInstitutionId());
	}

	@Test
	public void testUploadPackageInfo_whenPackageTypeIsOtherAndPackageTypeOtherIsBlank() throws Exception {
		PackageInformation packageInformation = mock(PackageInformation.class);
		when(packageInformation.getPackageType()).thenReturn("Other");
		when(packageInformation.getPackageTypeOther()).thenReturn("");
		when(uploadService.createUploadPackage(packageInformation, null)).thenReturn(new UploadPackage());

		try {
			controller.uploadPackageInfo(packageInformation, session);
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Package type 'Other' selected, but not defined further.", e.getMessage());
			verify(uploadService, times(0)).createUploadPackage(packageInformation, null);
			verify(uploadService, times(0)).saveUploadPackage(packageInformation, null);
			verify(uploadService, times(0)).saveSubmitterInfo(packageInformation);
			verify(uploadService, times(0)).findInstitutionId(packageInformation);
			verify(uploadService, times(0)).savePackageTypeOther(any(String.class));
		}

	}

	@Test
	public void testUploadPackageInfo_whenPackageTypeIsOtherAndPackageTypeOtherIsNull() throws Exception {
		PackageInformation packageInformation = mock(PackageInformation.class);
		when(packageInformation.getPackageType()).thenReturn("Other");
		when(packageInformation.getPackageTypeOther()).thenReturn(null);

		try {
			controller.uploadPackageInfo(packageInformation, session);
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Package type 'Other' selected, but not defined further.", e.getMessage());
			verify(uploadService, times(0)).createUploadPackage(packageInformation, null);
			verify(uploadService, times(0)).saveUploadPackage(packageInformation, null);
			verify(uploadService, times(0)).saveSubmitterInfo(packageInformation);
			verify(uploadService, times(0)).findInstitutionId(packageInformation);
			verify(uploadService, times(0)).savePackageTypeOther(any(String.class));
		}

	}

	@Test
	public void testUploadPackageInfo_whenPackageTypeIsOtherAndPackageTypeOtherHasAValue() throws Exception {
		PackageInformation packageInformation = mock(PackageInformation.class);
		when(packageInformation.getPackageType()).thenReturn("Other");
		when(packageInformation.getPackageTypeOther()).thenReturn("my special package");
		PackageTypeOther packageTypeOther = new PackageTypeOther();
		when(uploadService.savePackageTypeOther("my special package")).thenReturn(packageTypeOther);
		when(uploadService.saveUploadPackage(packageInformation, packageTypeOther)).thenReturn(5);
		when(uploadService.saveSubmitterInfo(packageInformation)).thenReturn(55);
		when(uploadService.findInstitutionId(packageInformation)).thenReturn(66);
		when(uploadService.createUploadPackage(packageInformation, packageTypeOther)).thenReturn(new UploadPackage());

		UploadPackageIds packageIds = controller.uploadPackageInfo(packageInformation, session);

		verify(uploadService).createUploadPackage(packageInformation, packageTypeOther);
		verify(uploadService).saveUploadPackage(packageInformation, packageTypeOther);
		verify(uploadService).saveSubmitterInfo(packageInformation);
		verify(uploadService).findInstitutionId(packageInformation);
		verify(uploadService).savePackageTypeOther("my special package");
		assertEquals(5, packageIds.getPackageId());
		assertEquals(55, packageIds.getSubmitterId());
		assertEquals(66, packageIds.getInstitutionId());
	}

	@Test
	public void testHandleFileUpload_oneChunk() throws IllegalStateException, IOException {
		MultipartFile file = mock(MultipartFile.class);
		File savedFile = mock(File.class);
		when(fileHandler.saveMultipartFile(file, "123_UUID", "filename", false)).thenReturn(savedFile);

		UploadPackage uploadPackage = mock(UploadPackage.class);
		when(uploadPackage.getCreatedAt()).thenReturn(new Date());
		when(uploadPackage.getPackageType()).thenReturn(new PackageType());
		when(uploadPackage.getUniversalId()).thenReturn("123_UUID");
		InstitutionDemographics institutionDemographics = new InstitutionDemographics();
		SubmitterDemographics submitterDemographics = new SubmitterDemographics();
		when(session.getAttribute("institution")).thenReturn(institutionDemographics);
		when(session.getAttribute("submitter")).thenReturn(submitterDemographics);
		when(session.getAttribute("uploadPackage")).thenReturn(uploadPackage);

		controller.setSession(session);

		controller.handleFileUpload(file, "fileMetadata", 1, 2, 3, 0, 1, "filename", 1, 0);

		ArgumentCaptor<UploadPackageIds> idCaptor = ArgumentCaptor.forClass(UploadPackageIds.class);
		ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
		ArgumentCaptor<String> metadataCaptor = ArgumentCaptor.forClass(String.class);
		verify(uploadService).addFileToPackage(fileCaptor.capture(), metadataCaptor.capture(), idCaptor.capture());
		UploadPackageIds ids = idCaptor.getValue();
		assertEquals(1, ids.getPackageId());
		assertEquals(2, ids.getSubmitterId());
		assertEquals(3, ids.getInstitutionId());
		assertEquals(savedFile, fileCaptor.getValue());
		assertEquals("fileMetadata", metadataCaptor.getValue());
	}

	@Test
	public void testHandleFileUpload_secondChunk() throws IllegalStateException, IOException {
		MultipartFile file = mock(MultipartFile.class);
		File savedFile = mock(File.class);

		FileMetadataEntries fileMetadataEntries = new FileMetadataEntries();
		UploadPackage uploadPackage1 = mock(UploadPackage.class);
		UploadPackage uploadPackage2 = mock(UploadPackage.class);
		FileSubmission fileSubmission = mock(FileSubmission.class);
		when(uploadPackage1.getCreatedAt()).thenReturn(new Date());
		when(uploadPackage1.getPackageType()).thenReturn(new PackageType());
		when(uploadPackage1.getUniversalId()).thenReturn("123_UUID");
		when(uploadPackage2.getCreatedAt()).thenReturn(new Date());
		when(uploadPackage2.getPackageType()).thenReturn(new PackageType());
		when(uploadPackage2.getUniversalId()).thenReturn("123_UUID");
		when(fileSubmission.getFileMetadata()).thenReturn(fileMetadataEntries);
		when(fileSubmission.getFilePath()).thenReturn("/");
		when(fileSubmission.getFileSize()).thenReturn(123L);
		when(uploadPackage2.getFileSubmissions()).thenReturn(Arrays.asList(fileSubmission));
		InstitutionDemographics institutionDemographics = new InstitutionDemographics();
		SubmitterDemographics submitterDemographics = new SubmitterDemographics();
		when(session.getAttribute("institution")).thenReturn(institutionDemographics);
		when(session.getAttribute("submitter")).thenReturn(submitterDemographics);
		when(session.getAttribute("uploadPackage")).thenReturn(uploadPackage1);
		when(uploadService.createFileSubmission(savedFile, fileMetadataEntries, institutionDemographics, submitterDemographics, uploadPackage2)).thenReturn(fileSubmission);

		controller.setSession(session);

		when(fileHandler.saveMultipartFile(file, "123_UUID", "filename", true)).thenReturn(savedFile);

		controller.handleFileUpload(file, "fileMetadata", 1, 2, 3, 0, 1, "filename", 2, 1);

		ArgumentCaptor<UploadPackageIds> idCaptor = ArgumentCaptor.forClass(UploadPackageIds.class);
		ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
		ArgumentCaptor<String> metadataCaptor = ArgumentCaptor.forClass(String.class);
		verify(uploadService).addFileToPackage(fileCaptor.capture(), metadataCaptor.capture(), idCaptor.capture());
		UploadPackageIds ids = idCaptor.getValue();
		assertEquals(1, ids.getPackageId());
		assertEquals(2, ids.getSubmitterId());
		assertEquals(3, ids.getInstitutionId());
		assertEquals(savedFile, fileCaptor.getValue());
		assertEquals("fileMetadata", metadataCaptor.getValue());
	}

}
