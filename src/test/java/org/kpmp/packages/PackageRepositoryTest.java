package org.kpmp.packages;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * Just a quick note...this test is really an integration test, and should live
 * with the other integration tests. However, we are currently skipping running
 * those tests on Travis since they need the mysql database, and we don't have
 * one to connect to.
 * 
 * Since we are switching to use mongo in the background and moving away from
 * mysql, we will be able to configure those tests to use an embedded mongo as
 * we have here. At that point, we will move this guy to live with his friends.
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { TestMongoConfig.class })
public class PackageRepositoryTest {

	@Autowired
	private PackageRepository packageRepo;

	@Test
	public void testFindAll() {
		packageRepo.save(new Package());
		List<Package> packages = packageRepo.findAll();
		assertEquals(1, packages.size());
	}

	@Test
	public void testFindById() {
		Package uploadPackage = new Package();
		uploadPackage.setPackageId("1234");
		packageRepo.save(uploadPackage);
		Optional<Package> foundPackage = packageRepo.findById("1234");
		assertEquals(uploadPackage, foundPackage.get());
	}

}
