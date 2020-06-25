package org.kpmp.packages.validation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.logging.LoggingService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PackageFilesValidationControllerTest {

	@Mock
	private PackageFilesValidationService service;
	private PackageFilesValidationController controller;
	@Mock
	private LoggingService logger;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new PackageFilesValidationController(service, logger);
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testValidateFilesInPackage() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		PackageFilesRequest packageFiles = new PackageFilesRequest();
		PackageValidationResponse expectedResult = new PackageValidationResponse();
		when(service.matchFiles(packageFiles)).thenReturn(expectedResult);

		PackageValidationResponse result = controller.validateFilesInPackage(packageFiles, request);

		assertEquals(expectedResult, result);
	}

	@Test
	public void testValidateFilesInPackageWhenException() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		PackageFilesRequest packageFiles = new PackageFilesRequest();
		Exception expectedException = new IOException("oopsie");
		when(service.matchFiles(packageFiles)).thenThrow(expectedException);

		try {
			controller.validateFilesInPackage(packageFiles, request);
		} catch (Exception expected) {
			assertEquals(expectedException, expected);
		}

	}

}
