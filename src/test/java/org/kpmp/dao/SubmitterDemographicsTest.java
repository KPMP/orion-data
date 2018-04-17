package org.kpmp.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

}
