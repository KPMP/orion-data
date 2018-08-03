package org.kpmp.view;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.dao.FileSubmission;
import org.kpmp.dao.InstitutionDemographics;
import org.kpmp.dao.PackageType;
import org.kpmp.dao.SubmitterDemographics;
import org.kpmp.dao.UploadPackage;

public class PackageViewTest {

	private PackageView packageView;

	@Before
	public void setUp() throws Exception {
		FileSubmission fileSubmission = mock(FileSubmission.class);
		when(fileSubmission.getSubmitter()).thenReturn(mock(SubmitterDemographics.class));
		when(fileSubmission.getInstitution()).thenReturn(mock(InstitutionDemographics.class));
		UploadPackage uploadPackage = mock(UploadPackage.class);
		when(uploadPackage.getPackageType()).thenReturn(mock(PackageType.class));
		when(fileSubmission.getUploadPackage()).thenReturn(uploadPackage);
		packageView = new PackageView(fileSubmission);
	}

	@After
	public void tearDown() throws Exception {
		packageView = null;
	}

	@Test
	public void testConstructor() throws Exception {
		FileSubmission fileSubmission = mock(FileSubmission.class);
		SubmitterDemographics submitter = mock(SubmitterDemographics.class);
		when(submitter.getFirstName()).thenReturn("first");
		when(submitter.getLastName()).thenReturn("last");
		when(fileSubmission.getSubmitter()).thenReturn(submitter);
		InstitutionDemographics institution = mock(InstitutionDemographics.class);
		when(fileSubmission.getInstitution()).thenReturn(institution);
		when(institution.getInstitutionName()).thenReturn("institution");
		UploadPackage uploadPackage = mock(UploadPackage.class);
		PackageType packageType = mock(PackageType.class);
		when(packageType.getPackageType()).thenReturn("package Type");
		when(uploadPackage.getPackageType()).thenReturn(packageType);
		when(uploadPackage.getSubjectId()).thenReturn("subject id");
		Date createdDate = new Date();
		when(uploadPackage.getCreatedAt()).thenReturn(createdDate);
		when(fileSubmission.getUploadPackage()).thenReturn(uploadPackage);

		packageView = new PackageView(fileSubmission);

		assertEquals("first last", packageView.getName());
		assertEquals("institution", packageView.getSite());
		assertEquals("need universal Id", packageView.getPackageId());
		assertEquals("package Type", packageView.getPackageType());
		assertEquals("subject id", packageView.getSubjectId());
		assertEquals(createdDate, packageView.getUploadDate());
	}

	@Test
	public void testSetName() {
		packageView.setName("name");
		assertEquals("name", packageView.getName());
	}

	@Test
	public void testSetSite() {
		packageView.setSite("site");
		assertEquals("site", packageView.getSite());
	}

	@Test
	public void testSetPackageType() {
		packageView.setPackageType("packageType");
		assertEquals("packageType", packageView.getPackageType());
	}

	@Test
	public void testSetSubjectId() {
		packageView.setSubjectId("subjectId");
		assertEquals("subjectId", packageView.getSubjectId());
	}

	@Test
	public void testSetUploadDate() {
		Date uploadDate = new Date();
		packageView.setUploadDate(uploadDate);
		assertEquals(uploadDate, packageView.getUploadDate());
	}

	@Test
	public void testSetPackageId() throws Exception {
		packageView.setPackageId("packageId");
		assertEquals("packageId", packageView.getPackageId());
	}

}
