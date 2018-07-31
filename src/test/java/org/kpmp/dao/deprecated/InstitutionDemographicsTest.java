package org.kpmp.dao.deprecated;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.dao.deprecated.FileSubmission;
import org.kpmp.dao.deprecated.InstitutionDemographics;

public class InstitutionDemographicsTest {

	private InstitutionDemographics institution;

	@Before
	public void setUp() throws Exception {
		institution = new InstitutionDemographics();
	}

	@After
	public void tearDown() throws Exception {
		institution = null;
	}

	@Test
	public void testSetInstitutionShortName() {
		institution.setInstitutionShortName("institutionShortName");
		assertEquals("institutionShortName", institution.getInstitutionShortName());
	}

	@Test
	public void testSetInstitutionName() {
		institution.setInstitutionName("institutionName");
		assertEquals("institutionName", institution.getInstitutionName());
	}

	@Test
	public void testSetId() {
		institution.setId(565);
		assertEquals(565, institution.getId());
	}

	@Test
	public void testSetFileSubmissions() throws Exception {
		List<FileSubmission> fileSubmissions = Arrays.asList(new FileSubmission());
		institution.setFileSubmissions(fileSubmissions);
		assertEquals(fileSubmissions, institution.getFileSubmissions());
	}

}
