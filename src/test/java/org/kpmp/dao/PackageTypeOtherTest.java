package org.kpmp.dao;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.dao.deprecated.PackageTypeOther;

public class PackageTypeOtherTest {

	private PackageTypeOther packageTypeOther;

	@Before
	public void setUp() throws Exception {
		packageTypeOther = new PackageTypeOther();
	}

	@After
	public void tearDown() throws Exception {
		packageTypeOther = null;
	}

	@Test
	public void testSetId() {
		packageTypeOther.setId(44);
		assertEquals(44, packageTypeOther.getId());
	}

	@Test
	public void testSetPackageType() {
		packageTypeOther.setPackageType("package type");
		assertEquals("package type", packageTypeOther.getPackageType());
	}

}
