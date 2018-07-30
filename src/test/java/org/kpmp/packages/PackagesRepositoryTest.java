package org.kpmp.packages;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { TestMongoConfig.class })
public class PackagesRepositoryTest {

	@Autowired
	private PackageRepository packageRepo;

	@Test
	public void testFindAll() {
		packageRepo.save(new Package());
		List<Package> packages = packageRepo.findAll();
		assertEquals(1, packages.size());
	}

}
