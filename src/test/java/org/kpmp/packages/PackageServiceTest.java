package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.UniversalIdGenerator;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PackageServiceTest {

	@Mock
	private PackageRepository packageRepository;
	@Mock
	private UniversalIdGenerator universalIdGenerator;
	private PackageService service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new PackageService(packageRepository, universalIdGenerator);
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
		verify(packageRepository).findAll();
	}

	@Test
	public void testSavePackageInformation() throws Exception {
		Package packageInfo = new Package();
		when(universalIdGenerator.generateUniversalId()).thenReturn("new universal id");
		Package expectedPackage = mock(Package.class);
		when(packageRepository.save(packageInfo)).thenReturn(expectedPackage);

		Package savedPackage = service.savePackageInformation(packageInfo);

		assertEquals("new universal id", packageInfo.getPackageId());
		assertNotNull(packageInfo.getCreatedAt());
		verify(packageRepository).save(packageInfo);
		assertEquals(expectedPackage, savedPackage);
	}

}
