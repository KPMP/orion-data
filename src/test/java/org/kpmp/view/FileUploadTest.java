package org.kpmp.view;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.dao.FileSubmission;
import org.kpmp.dao.InstitutionDemographics;
import org.kpmp.dao.PackageType;
import org.kpmp.dao.SubmitterDemographics;
import org.kpmp.dao.UploadPackage;

public class FileUploadTest {

	private FileUpload upload;

	@Before
	public void setUp() throws Exception {
		upload = new FileUpload();
	}

	@After
	public void tearDown() throws Exception {
		upload = null;
	}

	@Test
	public void testConstructor_fileSubmission() throws Exception {

		FileSubmission fileSubmission = mock(FileSubmission.class);
		when(fileSubmission.getFilename()).thenReturn("filename");
		SubmitterDemographics submitter = mock(SubmitterDemographics.class);
		when(submitter.getFirstName()).thenReturn("John");
		when(submitter.getLastName()).thenReturn("Doe");
		when(fileSubmission.getSubmitter()).thenReturn(submitter);
		InstitutionDemographics institution = mock(InstitutionDemographics.class);
		when(institution.getInstitutionName()).thenReturn("University of Michigan");
		when(fileSubmission.getInstitution()).thenReturn(institution);
		UploadPackage uploadPackage = mock(UploadPackage.class);
		Date experimentDate = new Date();
		when(uploadPackage.getExperimentDate()).thenReturn(experimentDate);
		Date createdAt = new Date();
		when(uploadPackage.getCreatedAt()).thenReturn(createdAt);
		PackageType packageType = mock(PackageType.class);
		when(packageType.getPackageType()).thenReturn("envelope");
		when(uploadPackage.getPackageType()).thenReturn(packageType);
		when(uploadPackage.getSubjectId()).thenReturn("subjectId");
		when(fileSubmission.getUploadPackage()).thenReturn(uploadPackage);

		FileUpload fileUpload = new FileUpload(fileSubmission);

		assertEquals("John Doe", fileUpload.getResearcher());
		assertEquals("University of Michigan", fileUpload.getInstitution());
		assertEquals(experimentDate, fileUpload.getExperimentDate());
		assertEquals(createdAt, fileUpload.getCreatedAt());
		assertEquals("envelope", fileUpload.getPackageType());
		assertEquals("subjectId", fileUpload.getSubjectId());
		assertEquals("filename", fileUpload.getFilename());
	}

	@Test
	public void testSetFilename() throws Exception {
		upload.setFilename("filename");
		assertEquals("filename", upload.getFilename());
	}

	@Test
	public void testSetResearcher() {
		upload.setResearcher("first last");
		assertEquals("first last", upload.getResearcher());
	}

	@Test
	public void testSetInstitution() {
		upload.setInstitution("institution");
		assertEquals("institution", upload.getInstitution());
	}

	@Test
	public void testSetPackageType() {
		upload.setPackageType("packageType");
		assertEquals("packageType", upload.getPackageType());
	}

	@Test
	public void testSetExperimentDate() {
		Date experimentDate = new Date();
		upload.setExperimentDate(experimentDate);
		assertEquals(experimentDate, upload.getExperimentDate());
	}

	@Test
	public void testSetCreatedAt() {
		Date createdAt = new Date();
		upload.setCreatedAt(createdAt);
		assertEquals(createdAt, upload.getCreatedAt());
	}

	@Test
	public void testSetSubjectId() throws Exception {
		upload.setSubjectId("subjectId");
		assertEquals("subjectId", upload.getSubjectId());
	}
}
