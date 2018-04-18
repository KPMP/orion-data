package org.kpmp.dao;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CaseDemographicsTest {

	private CaseDemographics caseDemographics;

	@Before
	public void setUp() throws Exception {
		caseDemographics = new CaseDemographics();
	}

	@After
	public void tearDown() throws Exception {
		caseDemographics = null;
	}

	@Test
	public void testSetId() {
		caseDemographics.setId(5);
		assertEquals(5, caseDemographics.getId());
	}

	@Test
	public void testSetSubjectId() {
		caseDemographics.setSubjectId("subjectId");
		assertEquals("subjectId", caseDemographics.getSubjectId());
	}

	@Test
	public void testSetExperimentId() {
		caseDemographics.setExperimentId("experimentId");
		assertEquals("experimentId", caseDemographics.getExperimentId());
	}

	@Test
	public void testSetExperimentDate() {
		Date experimentDate = new Date();
		caseDemographics.setExperimentDate(experimentDate);
		assertEquals(experimentDate, caseDemographics.getExperimentDate());
	}

	@Test
	public void testSetCreatedAt() {
		Date createdAt = new Date();
		caseDemographics.setCreatedAt(createdAt);
		assertEquals(createdAt, caseDemographics.getCreatedAt());
	}

	@Test
	public void testSetFileSubmissions() throws Exception {
		List<FileSubmissions> fileSubmissions = Arrays.asList(new FileSubmissions());
		caseDemographics.setFileSubmissions(fileSubmissions);
		assertEquals(fileSubmissions, caseDemographics.getFileSubmissions());
	}

}
