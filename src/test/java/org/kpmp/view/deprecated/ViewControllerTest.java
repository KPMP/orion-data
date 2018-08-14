package org.kpmp.view.deprecated;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.dao.deprecated.FileSubmission;
import org.kpmp.dao.deprecated.FileSubmissionsRepository;
import org.kpmp.dao.deprecated.InstitutionDemographics;
import org.kpmp.dao.deprecated.PackageType;
import org.kpmp.dao.deprecated.SubmitterDemographics;
import org.kpmp.dao.deprecated.UploadPackage;
import org.kpmp.view.deprecated.FileUpload;
import org.kpmp.view.deprecated.ViewController;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ViewControllerTest {

	@Mock
	private FileSubmissionsRepository fileSubmissionsRepository;
	private ViewController controller;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new ViewController(fileSubmissionsRepository);
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