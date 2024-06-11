package org.kpmp.packages.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kpmp.logging.LoggingService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PackageFilesValidationControllerTest {

	@Mock
	private PackageFilesValidationService service;
	private PackageFilesValidationController controller;
	@Mock
	private LoggingService logger;
	private AutoCloseable mocks;

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		controller = new PackageFilesValidationController(service, logger);
	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
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
