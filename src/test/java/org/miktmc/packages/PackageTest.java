package org.miktmc.packages;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.miktmc.users.User;

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
	public void testSetSiteName() {
		testPackage.setSiteName("TIS");
		assertEquals("TIS", testPackage.getSiteName());
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
    public void testSetStudy() throws Exception {
        testPackage.setStudy("study");
        assertEquals("study", testPackage.getStudy());
    }

	@Test
	public void testToString() throws Exception {
		Date createdAt = new Date();
		Package packageInfo = new Package();
		packageInfo.setAttachments(Arrays.asList(mock(Attachment.class)));
		packageInfo.setCreatedAt(createdAt);
		packageInfo.setDescription("description");
		packageInfo.setSiteName("siteName");
		packageInfo.setPackageId("packageId");
		packageInfo.setPackageType("packageType");
		packageInfo.setProtocol("protocol");
		packageInfo.setSubjectId("subjectId");
        packageInfo.setStudy("study");
		User user = new User();
		user.setId("1234");
		packageInfo.setSubmitter(user);
		packageInfo.setModifications(Arrays.asList(new String[]{"DELETE 123 BY aDeleter"}));

		assertEquals(
				"packageId: packageId, packageType: packageType, createdAt: " + createdAt + ", " + "submitterId: 1234, "
						+ "protocol: protocol, subjectId: subjectId, experimentDate: null, description: description, "
						+ "siteName: siteName, study: study, number of attachments: 1, modifications: [DELETE 123 BY aDeleter]",
				packageInfo.toString());
	}

}
