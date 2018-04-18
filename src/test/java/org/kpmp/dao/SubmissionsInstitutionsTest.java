package org.kpmp.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SubmissionsInstitutionsTest {

	private SubmissionsInstitutions institutionSubmission;

	@Before
	public void setUp() throws Exception {
		institutionSubmission = new SubmissionsInstitutions();
	}

	@After
	public void tearDown() throws Exception {
		institutionSubmission = null;
	}

	@Test
	public void testSetId() {
		institutionSubmission.setId(4);
		assertEquals(4, institutionSubmission.getId());
	}

	@Test
	public void testSetFileSubmissionId() {
		institutionSubmission.setFileSubmissionId(55);
		assertEquals(55, institutionSubmission.getFileSubmissionId());
	}

	@Test
	public void testSetCreatedAt() {
		Date createdAt = new Date();
		institutionSubmission.setCreatedAt(createdAt);
		assertEquals(createdAt, institutionSubmission.getCreatedAt());
	}

	@Test
	public void testSetInstitutionDemographics() throws Exception {
		InstitutionDemographics institutionDemographics = new InstitutionDemographics();
		institutionSubmission.setInstitution(institutionDemographics);
		assertEquals(institutionDemographics, institutionSubmission.getInstitution());
	}

}
