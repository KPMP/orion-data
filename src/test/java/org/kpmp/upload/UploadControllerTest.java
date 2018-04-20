package org.kpmp.upload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UploadControllerTest {

	@Mock
	private UploadService uploadService;
	private UploadController controller;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new UploadController(uploadService);
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testUploadPackageInfo() {
		PackageInformation packageInformation = mock(PackageInformation.class);
		when(uploadService.saveUploadPackage(packageInformation)).thenReturn(5);
		when(uploadService.saveSubmitterInfo(packageInformation)).thenReturn(55);
		when(uploadService.findInstitutionId(packageInformation)).thenReturn(66);

		UploadPackageIds packageIds = controller.uploadPackageInfo(packageInformation);

		verify(uploadService).saveUploadPackage(packageInformation);
		assertEquals(5, packageIds.getPackageId());
		assertEquals(55, packageIds.getSubmitterId());
		assertEquals(66, packageIds.getInstitutionId());
	}

	@Test
	public void testHandleFileUpload() {
		fail("Not yet implemented");
	}

}
