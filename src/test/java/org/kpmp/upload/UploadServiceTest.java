package org.kpmp.upload;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.dao.InstitutionDemographics;
import org.kpmp.dao.SubmitterDemographics;
import org.kpmp.dao.UploadPackage;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UploadServiceTest {

	@Mock
	private UploadPackageRepository uploadPackageRepository;
	@Mock
	private FileSubmissionsRepository fileSubmissionsRepository;
	@Mock
	private SubmitterRepository submitterRepository;
	@Mock
	private InstitutionRepository institutionRepository;
	private UploadService service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new UploadService(uploadPackageRepository, fileSubmissionsRepository, submitterRepository,
				institutionRepository);
	}

	@After
	public void tearDown() throws Exception {
		service = null;
	}

	@Test
	public void testSaveUploadPackage() {
		Date experimentDate = new Date();
		UploadPackage savedPackage = mock(UploadPackage.class);
		when(savedPackage.getId()).thenReturn(5);
		when(uploadPackageRepository.save(any(UploadPackage.class))).thenReturn(savedPackage);
		PackageInformation packageInformation = new PackageInformation();
		packageInformation.setExperimentDate(experimentDate);

		int packageId = service.saveUploadPackage(packageInformation);

		assertEquals(5, packageId);
		ArgumentCaptor<UploadPackage> packageCaptor = ArgumentCaptor.forClass(UploadPackage.class);
		verify(uploadPackageRepository).save(packageCaptor.capture());
		assertEquals(experimentDate, packageCaptor.getValue().getExperimentDate());
	}

	@Test
	public void testSaveSubmitterInfo() throws Exception {
		PackageInformation packageInfo = new PackageInformation();
		packageInfo.setFirstName("first name");
		SubmitterDemographics submitter = mock(SubmitterDemographics.class);
		when(submitter.getId()).thenReturn(55);
		when(submitterRepository.save(any(SubmitterDemographics.class))).thenReturn(submitter);

		int submitterId = service.saveSubmitterInfo(packageInfo);

		assertEquals(55, submitterId);
		ArgumentCaptor<SubmitterDemographics> submitterCaptor = ArgumentCaptor.forClass(SubmitterDemographics.class);
		verify(submitterRepository).save(submitterCaptor.capture());
		assertEquals("first name", submitterCaptor.getValue().getFirstName());
	}

	@Test
	public void testFindInstitutionId() throws Exception {
		PackageInformation packageInfo = new PackageInformation();
		packageInfo.setInstitutionName("institutionName");
		InstitutionDemographics institution = mock(InstitutionDemographics.class);
		when(institution.getId()).thenReturn(66);
		when(institutionRepository.findByInstitutionName("institutionName")).thenReturn(institution);

		int institutionId = service.findInstitutionId(packageInfo);

		assertEquals(66, institutionId);
		verify(institutionRepository).findByInstitutionName("institutionName");
	}

}
