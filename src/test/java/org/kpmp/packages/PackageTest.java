package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

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
	public void testSetSubmitterFirstName() {
		testPackage.setSubmitterFirstName("submitter");
		assertEquals("submitter", testPackage.getSubmitterFirstName());
	}

	@Test
	public void testSetSubmitterLastName() {
		testPackage.setSubmitterLastName("submitter");
		assertEquals("submitter", testPackage.getSubmitterLastName());
	}

	@Test
	public void testSetInstitutionName() {
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
	public void testSetProtocol() throws Exception {
		testPackage.setProtocol("protocol");
		assertEquals("protocol", testPackage.getProtocol());
	}

	@Test
	public void testSetSubjectId() throws Exception {
		testPackage.setSubjectId("subjectId");
		assertEquals("subjectId", testPackage.getSubjectId());
	}

	@Test
	public void testSetExperimentDate() throws Exception {
		Date experimentDate = new Date();
		testPackage.setExperimentDate(experimentDate);
		assertEquals(experimentDate, testPackage.getExperimentDate());
	}

	@Test
	public void testSetDescription() throws Exception {
		testPackage.setDescription("description");
		assertEquals("description", testPackage.getDescription());
	}

	@Test
	public void testToString() throws Exception {
		Date createdAt = new Date();
		Package packageInfo = new Package();
		packageInfo.setAttachments(Arrays.asList(mock(Attachment.class)));
		packageInfo.setCreatedAt(createdAt);
		packageInfo.setDescription("description");
		packageInfo.setInstitution("institution");
		packageInfo.setPackageId("packageId");
		packageInfo.setPackageType("packageType");
		packageInfo.setProtocol("protocol");
		packageInfo.setSubjectId("subjectId");
		packageInfo.setSubmitterFirstName("submitterFirstName");
		packageInfo.setSubmitterLastName("submitterLastName");

		assertEquals("packageId: packageId, packageType: packageType, createdAt: " + createdAt + ", "
				+ "submitterFirstName: submitterFirstName, submitterLastName: submitterLastName, "
				+ "protocol: protocol, subjectId: subjectId, experimentDate: null, description: description, "
				+ "institution: institution, number of attachments: 1", packageInfo.toString());
	}

	@Test
	public void testGenerateJSON() throws Exception {
		Date createdAt = new Date();
		Package packageInfo = new Package();
		Attachment attachment = new Attachment();
		attachment.setFileName("filename");
		attachment.setId("fileId");
		attachment.setSize(433);
		packageInfo.setAttachments(Arrays.asList(attachment));
		packageInfo.setCreatedAt(createdAt);
		packageInfo.setDescription("description");
		packageInfo.setInstitution("institution");
		packageInfo.setPackageId("packageId");
		packageInfo.setPackageType("packageType");
		packageInfo.setProtocol("protocol");
		packageInfo.setSubjectId("subjectId");
		packageInfo.setSubmitterFirstName("submitterFirstName");
		packageInfo.setSubmitterLastName("submitterLastName");

		assertEquals("{\"packageId\":\"packageId\",\"packageType\":\"packageType\",\"createdAt\":" + createdAt.getTime()
				+ "," + "\"submitterFirstName\":\"submitterFirstName\",\"submitterLastName\":\"submitterLastName\","
				+ "\"institution\":\"institution\",\"protocol\":\"protocol\",\"subjectId\":\"subjectId\","
				+ "\"experimentDate\":null,\"description\":\"description\",\"attachments\":"
				+ "[{\"id\":\"fileId\",\"size\":433,\"fileName\":\"filename\"}]}", packageInfo.generateJSON());
	}
}
