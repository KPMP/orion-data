package org.kpmp.upload;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.dao.UploadPackage;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UploadServiceTest {

	@Mock
	private UploadPackageRepository uploadPackageRepository;
	@Mock
	private FileSubmissionsRepository fileSubmissionsRepository;
	private UploadService service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new UploadService(uploadPackageRepository, fileSubmissionsRepository);
	}

	@After
	public void tearDown() throws Exception {
		service = null;
	}

	@Test
	public void testSaveUploadPackage() {
		UploadPackage uploadPackage = uploadPackageRepository.save(mock(UploadPackage.class));
		when(uploadPackage).thenReturn(mock(UploadPackage.class));
	}

}
