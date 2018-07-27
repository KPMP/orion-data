package org.kpmp.upload.deprecated;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.upload.deprecated.UploadPackageIds;

public class UploadPackageIdsTest {

	private UploadPackageIds ids;

	@Before
	public void setUp() throws Exception {
		ids = new UploadPackageIds();
	}

	@After
	public void tearDown() throws Exception {
		ids = null;
	}

	@Test
	public void testConstructor() throws Exception {
		UploadPackageIds packageIds = new UploadPackageIds(1, 2, 3);
		assertEquals(1, packageIds.getPackageId());
		assertEquals(2, packageIds.getSubmitterId());
		assertEquals(3, packageIds.getInstitutionId());
	}

	@Test
	public void testSetPackageId() {
		ids.setPackageId(5);
		assertEquals(5, ids.getPackageId());
	}

	@Test
	public void testSetSubmitterId() {
		ids.setSubmitterId(55);
		assertEquals(55, ids.getSubmitterId());
	}

	@Test
	public void testSetInstitutionId() {
		ids.setInstitutionId(66);
		assertEquals(66, ids.getInstitutionId());
	}

}
