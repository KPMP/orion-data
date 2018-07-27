package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PackageServiceTest {

	@Mock
	private PackageRepository packageRepository;
	private PackageService service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new PackageService(packageRepository);
	}

	@After
	public void tearDown() throws Exception {
		service = null;
	}

	@Test
	public void testFindAllPackages() {
		List<Package> expectedResults = Arrays.asList(new Package());
		when(packageRepository.findAll()).thenReturn(expectedResults);

		List<Package> packages = service.findAllPackages();

		assertEquals(expectedResults, packages);
	}

}
