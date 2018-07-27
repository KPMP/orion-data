package org.kpmp.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.dao.deprecated.PackageType;
import org.kpmp.dao.deprecated.UploadPackage;

public class PackageTypeTest {

	private PackageType packageType;

	@Before
	public void setUp() throws Exception {
		packageType = new PackageType();
	}

	@After
	public void tearDown() throws Exception {
		packageType = null;
	}

	@Test
	public void testSetId() {
		packageType.setId(3);
		assertEquals(3, packageType.getId());
	}

	@Test
	public void testSetPackageType() {
		packageType.setPackageType("packageType");
		assertEquals("packageType", packageType.getPackageType());
	}

	@Test
	public void testSetUploadPackages() throws Exception {
		List<UploadPackage> packages = new ArrayList<>();
		packages.add(new UploadPackage());
		packageType.setUploadPackages(packages);
		assertEquals(packages, packageType.getUploadPackages());
	}
}
