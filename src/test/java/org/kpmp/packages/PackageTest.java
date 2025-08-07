package org.kpmp.packages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kpmp.users.User;

public class PackageTest {

	private Package testPackage;

	@BeforeEach
	public void setUp() throws Exception {
		testPackage = new Package();
	}

	@AfterEach
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
        packageInfo.setUploadType("uploadType");
		User user = new User();
		user.setId("1234");
		packageInfo.setSubmitter(user);

		assertEquals(
				"packageId: packageId, packageType: packageType, createdAt: " + createdAt + ", " + "submitterId: 1234, "
						+ "protocol: protocol, subjectId: subjectId, uploadType: uploadType, experimentDate: null, description: description, "
						+ "tisName: TIS, number of attachments: 1",
				packageInfo.toString());
	}

}
