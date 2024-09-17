package org.miktmc.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.logging.LoggingService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorControllerTest {

	private ErrorController controller;
	@Mock
	private LoggingService logger;
	private AutoCloseable mocks;

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		controller = new ErrorController(logger);
	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
		controller = null;
	}

	@Test
	public void testLogError() throws IOException {
		FrontEndError errorMessage = mock(FrontEndError.class);
		when(errorMessage.getError()).thenReturn("error");
		when(errorMessage.getStackTrace()).thenReturn("oh noes...something terrible happened");
		HttpServletRequest request = mock(HttpServletRequest.class);

		ResponseEntity<Boolean> result = controller.logError(errorMessage, request);

		verify(logger).logErrorMessage(ErrorController.class, null,
				"error with stacktrace: oh noes...something terrible happened", request);
		assertEquals(new ResponseEntity<>(true, HttpStatus.OK), result);
	}

}
