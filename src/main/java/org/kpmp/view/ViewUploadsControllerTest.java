package org.kpmp.view;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ViewUploadsControllerTest {

	@Mock
	private FileSubmissionsRepository fileSubmissionsRepository;
	private ViewUploadsController controller;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new ViewUploadsController(fileSubmissionsRepository);
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testGetFileUploads() {
		List<FileSubmission> fileSubmissions = new ArrayList<>();
		FileSubmission fileSubmission = mock(FileSubmission.class);
		when(fileSubmission.getSubmitter()).thenReturn(mock(SubmitterDemographics.class));
		when(fileSubmission.getInstitution()).thenReturn(mock(InstitutionDemographics.class));
		UploadPackage uploadPackage = mock(UploadPackage.class);
		when(uploadPackage.getPackageType()).thenReturn(mock(PackageType.class));
		when(fileSubmission.getUploadPackage()).thenReturn(uploadPackage);
		fileSubmissions.add(fileSubmission);
		when(fileSubmissionsRepository.findAllByOrderByCreatedAtDesc()).thenReturn(fileSubmissions);

		List<FileUpload> fileUploads = controller.getFileUploads();

		assertEquals(1, fileUploads.size());
	}

	@Test
	public void testGetFileUploads_whenNoneReturned() {
		when(fileSubmissionsRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());

		List<FileUpload> fileUploads = controller.getFileUploads();

		assertEquals(0, fileUploads.size());
	}

}
