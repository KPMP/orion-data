package org.kpmp.dao;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.dao.deprecated.FileSubmission;
import org.kpmp.dao.deprecated.SubmitterDemographics;
import org.kpmp.upload.PackageInformation;

public class SubmitterDemographicsTest {

	private SubmitterDemographics submitter;

	@Before
	public void setUp() throws Exception {
		submitter = new SubmitterDemographics();
	}

	@After
	public void tearDown() throws Exception {
		submitter = null;
	}

	@Test
	public void testConstructor_packageInfo() throws Exception {
		PackageInformation packageInfo = new PackageInformation();
		packageInfo.setFirstName("firstName");
		packageInfo.setLastName("lastName");
		Date createdDate = new Date();

		SubmitterDemographics submitterDemographics = new SubmitterDemographics(packageInfo, createdDate);

		assertEquals("firstName", submitterDemographics.getFirstName());
		assertEquals("lastName", submitterDemographics.getLastName());
		assertEquals(createdDate, submitterDemographics.getCreatedAt());
	}

	@Test
	public void testSetId() {
		submitter.setId(3);
		assertEquals(3, submitter.getId());
	}

	@Test
	public void testSetFirstName() {
		submitter.setFirstName("firstName");
		assertEquals("firstName", submitter.getFirstName());
	}

	@Test
	public void testSetLastName() {
		submitter.setLastName("lastName");
		assertEquals("lastName", submitter.getLastName());
	}

	@Test
	public void testSetCreatedAt() {
		Date createdAt = new Date();
		submitter.setCreatedAt(createdAt);
		assertEquals(createdAt, submitter.getCreatedAt());
	}

	@Test
	public void testSetDeletedAt() {
		Date deletedAt = new Date();
		submitter.setDeletedAt(deletedAt);
		assertEquals(deletedAt, submitter.getDeletedAt());
	}

	@Test
	public void testSetUpdatedAt() {
		Date updatedAt = new Date();
		submitter.setUpdatedAt(updatedAt);
		assertEquals(updatedAt, submitter.getUpdatedAt());
	}

	@Test
	public void testSetFileSumbissions() throws Exception {
		List<FileSubmission> submissions = Arrays.asList(new FileSubmission());
		submitter.setFileSubmissions(submissions);
		assertEquals(submissions, submitter.getFileSubmissions());
	}

}
