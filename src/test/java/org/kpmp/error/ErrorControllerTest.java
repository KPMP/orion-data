package org.kpmp.error;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.JWTHandler;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

public class ErrorControllerTest {

	private ErrorController controller;
	@SuppressWarnings("rawtypes")
	@Mock
	private Appender appender;
	@Captor
	private ArgumentCaptor<LoggingEvent> captureLoggingEvent;
	@Mock
	private JWTHandler jwtHandler;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Logger root = (Logger) LoggerFactory.getLogger(ErrorController.class);
		root.addAppender(appender);
		root.setLevel(Level.INFO);
		controller = new ErrorController(jwtHandler);
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
		appender = null;
		jwtHandler = null;
		captureLoggingEvent = null;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLogError() throws IOException {
		FrontEndError errorMessage = mock(FrontEndError.class);
		when(errorMessage.getError()).thenReturn("error");
		when(errorMessage.getStackTrace()).thenReturn("oh noes...something terrible happened");
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/v1/error");
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");

		ResponseEntity<Boolean> result = controller.logError(errorMessage, request);

		verify(appender, times(1)).doAppend(captureLoggingEvent.capture());
		LoggingEvent event = captureLoggingEvent.getAllValues().get(0);
		assertEquals("USERID: {} | PKGID: {} | URI: {} | MSG: {} ", event.getMessage());
		assertEquals(
				"USERID: userID | PKGID: null | URI: /v1/error | MSG: error with stacktrace: oh noes...something terrible happened ",
				event.getFormattedMessage());
		assertEquals(Level.ERROR, event.getLevel());
		assertEquals(new ResponseEntity<>(true, HttpStatus.OK), result);
	}

}
