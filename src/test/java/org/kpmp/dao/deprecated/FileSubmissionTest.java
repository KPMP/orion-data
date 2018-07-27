package org.kpmp.dao.deprecated;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.dao.deprecated.FileFormats;
import org.kpmp.dao.deprecated.FileMetadataEntries;
import org.kpmp.dao.deprecated.FileSubmission;
import org.kpmp.dao.deprecated.InstitutionDemographics;
import org.kpmp.dao.deprecated.SubmitterDemographics;
import org.kpmp.dao.deprecated.UploadPackage;

public class FileSubmissionTest {

	private FileSubmission fileSubmission;

	@Before
	public void setUp() throws Exception {
		fileSubmission = new FileSubmission();
	}

	@After
	public void tearDown() throws Exception {
		fileSubmission = null;
	}

	@Test
	public void testSetId() {
		fileSubmission.setId(45);
		assertEquals(45, fileSubmission.getId());
	}

	@Test
	public void testSetFilename() {
		fileSubmission.setFilename("filename");
		assertEquals("filename", fileSubmission.getFilename());
	}

	@Test
	public void testSetCreatedAt() {
		Date createdAt = new Date();
		fileSubmission.setCreatedAt(createdAt);
		assertEquals(createdAt, fileSubmission.getCreatedAt());
	}

	@Test
	public void testSetUpdatedAt() {
		Date updatedAt = new Date();
		fileSubmission.setUpdatedAt(updatedAt);
		assertEquals(updatedAt, fileSubmission.getUpdatedAt());
	}

	@Test
	public void testSetDeletedAt() {
		Date deletedAt = new Date();
		fileSubmission.setDeletedAt(deletedAt);
		assertEquals(deletedAt, fileSubmission.getDeletedAt());
	}

	@Test
	public void testSetSubmitter() throws Exception {
		SubmitterDemographics submitter = new SubmitterDemographics();
		fileSubmission.setSubmitter(submitter);
		assertEquals(submitter, fileSubmission.getSubmitter());
	}

	@Test
	public void testSetFileSize() throws Exception {
		fileSubmission.setFileSize(455l);
		assertEquals(new Long(455), fileSubmission.getFileSize());
	}

	@Test
	public void testSetFileFormat() throws Exception {
		FileFormats fileFormat = new FileFormats();
		fileSubmission.setFileFormat(fileFormat);
		assertEquals(fileFormat, fileSubmission.getFileFormat());
	}

	@Test
	public void testSetUploadPackage() throws Exception {
		UploadPackage uploadPackage = new UploadPackage();
		fileSubmission.setUploadPackage(uploadPackage);
		assertEquals(uploadPackage, fileSubmission.getUploadPackage());
	}

	@Test
	public void testSetFileMetadata() throws Exception {
		FileMetadataEntries fileMetadata = new FileMetadataEntries();
		fileSubmission.setFileMetadata(fileMetadata);
		assertEquals(fileMetadata, fileSubmission.getFileMetadata());
	}

	@Test
	public void testSetInstitution() throws Exception {
		InstitutionDemographics institution = new InstitutionDemographics();
		fileSubmission.setInstitution(institution);
		assertEquals(institution, fileSubmission.getInstitution());
	}

	@Test
	public void testSetFilePath() throws Exception {
		fileSubmission.setFilePath("filePath");
		assertEquals("filePath", fileSubmission.getFilePath());
	}

	@Test
	public void testSetUniversalId() throws Exception {
		fileSubmission.setUniversalId("universalId");
		assertEquals("universalId", fileSubmission.getUniversalId());
	}
}
