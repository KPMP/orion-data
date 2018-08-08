package org.kpmp.view;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.dao.FileSubmission;
import org.kpmp.dao.FileSubmissionsRepository;
import org.kpmp.dao.InstitutionDemographics;
import org.kpmp.dao.PackageType;
import org.kpmp.dao.SubmitterDemographics;
import org.kpmp.dao.UploadPackage;
import org.kpmp.upload.FilePathHelper;
import org.kpmp.upload.UploadPackageRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ViewUploadsControllerTest {

	@Mock
	private FileSubmissionsRepository fileSubmissionsRepository;
	private ViewUploadsController controller;
	@Mock
	private UploadPackageRepository uploadPackageRepository;
	@Mock
	private FilePathHelper filePathHelper;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new ViewUploadsController(fileSubmissionsRepository, uploadPackageRepository, filePathHelper);
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testGetPackages_oneFileOnePackage() {
		UploadPackage uploadPackage = mock(UploadPackage.class);
		when(uploadPackage.getUniversalId()).thenReturn("uuid");
		when(uploadPackageRepository.findById(1)).thenReturn(uploadPackage);
		FileSubmission fileSubmission = generateBlankFileSubmission();
		when(fileSubmission.getUploadPackage().getId()).thenReturn(1);
		List<FileSubmission> files = Arrays.asList(fileSubmission);
		when(fileSubmissionsRepository.findAllByOrderByCreatedAtDesc()).thenReturn(files);

		List<PackageView> packages = controller.getPackages();

		assertEquals(1, packages.size());
	}

	@Test
	public void testGetPackages_twoFilesOnePackage() {
		UploadPackage uploadPackage = mock(UploadPackage.class);
		when(uploadPackage.getUniversalId()).thenReturn("uuid", "uuid");
		when(uploadPackageRepository.findById(1)).thenReturn(uploadPackage, uploadPackage);
		FileSubmission fileSubmission1 = generateBlankFileSubmission();
		when(fileSubmission1.getUploadPackage().getId()).thenReturn(1);
		FileSubmission fileSubmission2 = generateBlankFileSubmission();
		when(fileSubmission2.getUploadPackage().getId()).thenReturn(1);
		List<FileSubmission> files = Arrays.asList(fileSubmission1, fileSubmission2);
		when(fileSubmissionsRepository.findAllByOrderByCreatedAtDesc()).thenReturn(files);

		List<PackageView> packages = controller.getPackages();

		assertEquals(1, packages.size());
	}

	@Test
	public void testGetPackages_threeFilesTwoPackages() {
		UploadPackage uploadPackage = mock(UploadPackage.class);
		when(uploadPackage.getUniversalId()).thenReturn("uuid", "uuid");
		when(uploadPackageRepository.findById(1)).thenReturn(uploadPackage, uploadPackage);
		when(uploadPackageRepository.findById(2)).thenReturn(uploadPackage);
		FileSubmission fileSubmission1 = generateBlankFileSubmission();
		when(fileSubmission1.getUploadPackage().getId()).thenReturn(1);
		FileSubmission fileSubmission2 = generateBlankFileSubmission();
		when(fileSubmission2.getUploadPackage().getId()).thenReturn(1);
		FileSubmission fileSubmission3 = generateBlankFileSubmission();
		when(fileSubmission3.getUploadPackage().getId()).thenReturn(2);
		List<FileSubmission> files = Arrays.asList(fileSubmission1, fileSubmission2, fileSubmission3);
		when(fileSubmissionsRepository.findAllByOrderByCreatedAtDesc()).thenReturn(files);

		List<PackageView> packages = controller.getPackages();

		assertEquals(2, packages.size());
	}

	private FileSubmission generateBlankFileSubmission() {
		FileSubmission fileSubmission = mock(FileSubmission.class);
		when(fileSubmission.getSubmitter()).thenReturn(mock(SubmitterDemographics.class));
		when(fileSubmission.getInstitution()).thenReturn(mock(InstitutionDemographics.class));
		UploadPackage uploadPackage = mock(UploadPackage.class);
		when(uploadPackage.getPackageType()).thenReturn(mock(PackageType.class));
		when(fileSubmission.getUploadPackage()).thenReturn(uploadPackage);
		return fileSubmission;
	}

}
