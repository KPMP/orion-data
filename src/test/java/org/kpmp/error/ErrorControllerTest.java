package org.kpmp.error;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.logging.LoggingService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorControllerTest {

	private ErrorController controller;
	@Mock
	private LoggingService logger;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new ErrorController(logger);
	}

	@After
	public void tearDown() throws Exception {
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
