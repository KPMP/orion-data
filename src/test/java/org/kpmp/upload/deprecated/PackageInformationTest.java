package org.kpmp.upload.deprecated;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.upload.deprecated.PackageInformation;

public class PackageInformationTest {

	private PackageInformation packageInfo;

	@Before
	public void setUp() throws Exception {
		packageInfo = new PackageInformation();
	}

	@After
	public void tearDown() throws Exception {
		packageInfo = null;
	}

	@Test
	public void testSetFirstName() {
		packageInfo.setFirstName("firstName");
		assertEquals("firstName", packageInfo.getFirstName());
	}

	@Test
	public void testSetLastName() {
		packageInfo.setLastName("lastName");
		assertEquals("lastName", packageInfo.getLastName());
	}

	@Test
	public void testSetPackageType() {
		packageInfo.setPackageType("packageType");
		assertEquals("packageType", packageInfo.getPackageType());
	}

	@Test
	public void testSetSubjectId() {
		packageInfo.setSubjectId("subject id");
		assertEquals("subject id", packageInfo.getSubjectId());
	}

	@Test
	public void testSetExperimentId() {
		packageInfo.setExperimentId("experimentId");
		assertEquals("experimentId", packageInfo.getExperimentId());
	}

	@Test
	public void testSetExperimentDate() {
		Date experimentDate = new Date();
		packageInfo.setExperimentDate(experimentDate);
		assertEquals(experimentDate, packageInfo.getExperimentDate());
	}

	@Test
	public void testSetInstitutionName() throws Exception {
		packageInfo.setInstitutionName("name");
		assertEquals("name", packageInfo.getInstitutionName());
	}

	@Test
	public void testSetPackageTypeOther() throws Exception {
		packageInfo.setPackageTypeOther("packageTypeOther");
		assertEquals("packageTypeOther", packageInfo.getPackageTypeOther());
	}

	@Test
	public void testSetProtocol() throws Exception {
		packageInfo.setProtocol("protocol name");
		assertEquals("protocol name", packageInfo.getProtocol());
	}
}
