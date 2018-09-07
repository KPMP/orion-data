package org.kpmp.packages;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PackageTest {

	private Package testPackage;

	@Before
	public void setUp() throws Exception {
		testPackage = new Package();
	}

	@After
	public void tearDown() throws Exception {
		testPackage = null;
	}

	@Test
	public void testSetPackageId() {
		testPackage.setPackageId("packageId");
		assertEquals("packageId", testPackage.getPackageId());
	}

	@Test
	public void testSetPackageType() {
		testPackage.setPackageType("packageType");
		assertEquals("packageType", testPackage.getPackageType());
	}

	@Test
	public void testSetCreatedAt() {
		Date createdAt = new Date();
		testPackage.setCreatedAt(createdAt);
		assertEquals(createdAt, testPackage.getCreatedAt());
	}

	@Test
	public void testSetSubmitter() {
		testPackage.setSubmitter("submitter");
		assertEquals("submitter", testPackage.getSubmitter());
	}

	@Test
	public void testSetInstitution() {
		testPackage.setInstitution("institution");
		assertEquals("institution", testPackage.getInstitution());
	}

	@Test
	public void testSetAttachments() {
		List<Attachment> attachments = Arrays.asList(new Attachment());
		testPackage.setAttachments(attachments);
		assertEquals(attachments, testPackage.getAttachments());
	}

	@Test
	public void testSetSubjectId() {
		testPackage.setSubjectId("Subject 23");
		assertEquals("Subject 23", testPackage.getSubjectId());
	}

}
