package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.users.User;

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
	public void testSetTisName() {
		testPackage.setTisName("TIS");
		assertEquals("TIS", testPackage.getTisName());
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
	public void testSetSubmitter() throws Exception {
		User testUser = new User();
		testPackage.setSubmitter(testUser);
		assertEquals(testUser, testPackage.getSubmitter());
	}

	@Test
	public void testSetRegenerateZip() throws Exception {
		testPackage.setRegenerateZip(true);
		assertEquals(true, testPackage.getRegenerateZip());
	}

	@Test
	public void testToString() throws Exception {
		Date createdAt = new Date();
		Package packageInfo = new Package();
		packageInfo.setAttachments(Arrays.asList(mock(Attachment.class)));
		packageInfo.setCreatedAt(createdAt);
		packageInfo.setDescription("description");
		packageInfo.setTisName("TIS");
		packageInfo.setPackageId("packageId");
		packageInfo.setPackageType("packageType");
		packageInfo.setProtocol("protocol");
		packageInfo.setSubjectId("subjectId");
		packageInfo.setRegenerateZip(true);
		User user = new User();
		user.setId("1234");
		packageInfo.setSubmitter(user);

		assertEquals("packageId: packageId, packageType: packageType, createdAt: " + createdAt + ", "
				+ "submitterId: 1234, "
				+ "protocol: protocol, subjectId: subjectId, experimentDate: null, description: description, "
				+ "tisName: TIS, number of attachments: 1, regenerateZip: true", packageInfo.toString());
	}

	@Test
	public void testGenerateJSON() throws Exception {
		Date createdAt = new Date();
		Date experimentDate = new Date();
		Package packageInfo = new Package();
		Attachment attachment = new Attachment();
		attachment.setFileName("filename");
		attachment.setId("fileId");
		attachment.setSize(433);
		packageInfo.setAttachments(Arrays.asList(attachment));
		packageInfo.setCreatedAt(createdAt);
		packageInfo.setDescription("description");
		packageInfo.setTisName("TIS");
		packageInfo.setPackageId("packageId");
		packageInfo.setPackageType("packageType");
		packageInfo.setProtocol("protocol");
		packageInfo.setSubjectId("subjectId");
		User testUser = new User();
		testUser.setId("1234");
		testUser.setFirstName("Arnold");
		testUser.setLastName("Schwarzenegger");
		testUser.setDisplayName("Conan");
		testUser.setEmail("arnie@illbeback.com");
		packageInfo.setSubmitter(testUser);
		packageInfo.setExperimentDate(experimentDate);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		String createdAtString = df.format(createdAt);
		DateFormat experimentDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String experimentDateString = experimentDateFormat.format(experimentDate);

		assertEquals(
				"{\"packageId\":\"packageId\",\"createdAt\":\"" + createdAtString + "\","
						+ "\"packageType\":\"packageType\"," +
						"\"submitter\":{\"firstName\":\"Arnold\",\"lastName\":\"Schwarzenegger\","
						+ "\"displayName\":\"Conan\",\"email\":\"arnie@illbeback.com\"},"
						+ "\"tisName\":\"TIS\"," + "\"protocol\":\"protocol\",\"subjectId\":\"subjectId\","
						+ "\"experimentDate\":\"" + experimentDateString + "\",\"description\":\"description\","
						+ "\"attachments\":[{\"fileName\":\"filename\",\"size\":433,\"id\":\"fileId\"}]}",
				packageInfo.generateJSON());
	}
}
