package org.kpmp.packages;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.packages.PackageNotificationInfo;

public class PackageNotificationInfoTest {

	private PackageNotificationInfo info;

	@Before
	public void setUp() throws Exception {
		info = new PackageNotificationInfo();
	}

	@After
	public void tearDown() throws Exception {
		info = null;
	}

	@Test
	public void testConstructor() {
		Date datePackageSubmitted = new Date();
		PackageNotificationInfo packageInfo = new PackageNotificationInfo("packageId", "packageType",
				datePackageSubmitted, "submitterName", "specimenId", "origin");

		assertEquals("packageId", packageInfo.getPackageId());
		assertEquals("packageType", packageInfo.getPackageType());
		assertEquals(datePackageSubmitted, packageInfo.getDatePackageSubmitted());
		assertEquals("submitterName", packageInfo.getSubmitter());
		assertEquals("specimenId", packageInfo.getSpecimenId());
		assertEquals("origin", packageInfo.getOrigin());
	}

	@Test
	public void testSetPackageId() {
		info.setPackageId("packageId");
		assertEquals("packageId", info.getPackageId());
	}

	@Test
	public void testSetPackageType() {
		info.setPackageType("packageType");
		assertEquals("packageType", info.getPackageType());
	}

	@Test
	public void testSetDatePackageSubmitted() {
		Date dateSubmitted = new Date();
		info.setDatePackageSubmitted(dateSubmitted);
		assertEquals(dateSubmitted, info.getDatePackageSubmitted());
	}

	@Test
	public void testSetSubmitter() {
		info.setSubmitter("submitter");
		assertEquals("submitter", info.getSubmitter());
	}

	@Test
	public void testSetSpecimenId() {
		info.setSpecimenId("specimenId");
		assertEquals("specimenId", info.getSpecimenId());
	}

	@Test
	public void testOrigin() throws Exception {
		info.setOrigin("origin");
		assertEquals("origin", info.getOrigin());
	}

}
