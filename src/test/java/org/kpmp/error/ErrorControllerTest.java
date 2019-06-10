package org.kpmp.error;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.JWTHandler;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class ErrorControllerTest {

	private ErrorController controller;

	@Before
	public void setUp() throws Exception {
		controller = new ErrorController(new JWTHandler());
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testLogError() throws IOException {
		Logger testLogger = (Logger) LoggerFactory.getLogger(ErrorController.class);
		ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
		listAppender.start();
		testLogger.addAppender(listAppender);
		FrontEndError errorMessage = mock(FrontEndError.class);
		when(errorMessage.getError()).thenReturn("error");
		when(errorMessage.getStackTrace()).thenReturn("stacktrace");

		ResponseEntity<Boolean> result = controller.logError(errorMessage, mock(HttpServletRequest.class));

		List<ILoggingEvent> logsList = listAppender.list;
		String message = logsList.get(0).getMessage();
		assertEquals("error with stacktrace: stacktrace", message);
		assertEquals(new ResponseEntity<>(true, HttpStatus.OK), result);
	}

}
