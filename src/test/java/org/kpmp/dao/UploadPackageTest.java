package org.kpmp.dao;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.upload.PackageInformation;

public class UploadPackageTest {

	private UploadPackage uploadPackage;

	@Before
	public void setUp() throws Exception {
		uploadPackage = new UploadPackage();
	}

	@After
	public void tearDown() throws Exception {
		uploadPackage = null;
	}

	@Test
	public void testConstructor_packageInformation() throws Exception {
		Date createdDate = new Date();
		Date experimentDate = new Date();
		PackageInformation packageInformation = new PackageInformation();
		packageInformation.setExperimentDate(experimentDate);
		packageInformation.setSubjectId("subjectId");
		UploadPackage uploadPackage = new UploadPackage(packageInformation, createdDate);

		assertEquals(experimentDate, uploadPackage.getExperimentDate());
		assertEquals("subjectId", uploadPackage.getSubjectId());
		assertEquals(createdDate, uploadPackage.getCreatedAt());
	}

	@Test
	public void testSetId() {
		uploadPackage.setId(5);
		assertEquals(5, uploadPackage.getId());
	}

	@Test
	public void testSetSubjectId() {
		uploadPackage.setSubjectId("subjectId");
		assertEquals("subjectId", uploadPackage.getSubjectId());
	}

	@Test
	public void testSetExperimentDate() {
		Date experimentDate = new Date();
		uploadPackage.setExperimentDate(experimentDate);
		assertEquals(experimentDate, uploadPackage.getExperimentDate());
	}

	@Test
	public void testSetCreatedAt() {
		Date createdAt = new Date();
		uploadPackage.setCreatedAt(createdAt);
		assertEquals(createdAt, uploadPackage.getCreatedAt());
	}

	@Test
	public void testSetFileSubmissions() throws Exception {
		List<FileSubmission> fileSubmissions = Arrays.asList(new FileSubmission());
		uploadPackage.setFileSubmissions(fileSubmissions);
		assertEquals(fileSubmissions, uploadPackage.getFileSubmissions());
	}

	@Test
	public void testSetPackageType() throws Exception {
		PackageType packageType = new PackageType();
		uploadPackage.setPackageType(packageType);
		assertEquals(packageType, uploadPackage.getPackageType());
	}

	@Test
	public void testSetPackageTypeOther() {
		PackageTypeOther packageTypeOther = new PackageTypeOther();
		uploadPackage.setPackageTypeOther(packageTypeOther);
		assertEquals(packageTypeOther, uploadPackage.getPackageTypeOther());
	}

	@Test
	public void testSetUniversalId() throws Exception {
		uploadPackage.setUniversalId("universalId");
		assertEquals("universalId", uploadPackage.getUniversalId());
	}

	@Test
	public void testSetProtocol() throws Exception {
		Protocol protocol = new Protocol();
		uploadPackage.setProtocol(protocol);
		assertEquals(protocol, uploadPackage.getProtocol());
	}
}
