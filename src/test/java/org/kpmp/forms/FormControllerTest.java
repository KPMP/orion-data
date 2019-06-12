package org.kpmp.forms;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

public class FormControllerTest {

	@Mock
	private FormRepository repository;
	@Mock
	private JWTHandler jwtHandler;
	@SuppressWarnings("rawtypes")
	@Mock
	private Appender appender;
	@Captor
	private ArgumentCaptor<LoggingEvent> captureLoggingEvent;
	private FormController controller;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Logger root = (Logger) LoggerFactory.getLogger(FormController.class);
		root.addAppender(appender);
		root.setLevel(Level.INFO);
		controller = new FormController(repository, jwtHandler);
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
		appender = null;
		captureLoggingEvent = null;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetFormDTDNoVersion() {
		Form expectedForm = mock(Form.class);
		when(repository.findTopByOrderByVersionDesc()).thenReturn(expectedForm);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");
		when(request.getRequestURI()).thenReturn("/v1/form");

		Form formDTD = controller.getFormDTD(request);

		assertEquals(expectedForm, formDTD);
		verify(appender, times(1)).doAppend(captureLoggingEvent.capture());
		LoggingEvent event = captureLoggingEvent.getAllValues().get(0);
		assertEquals("USERID: {} | PKGID: {} | URI: {} | MSG: {} ", event.getMessage());
		assertEquals("USERID: userID | PKGID: null | URI: /v1/form | MSG: Request for all forms ",
				event.getFormattedMessage());
		assertEquals(Level.INFO, event.getLevel());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetFormDTDWithVersion() {
		Form expectedForm = mock(Form.class);
		when(repository.findByVersion(1.0)).thenReturn(Arrays.asList(expectedForm));
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");
		when(request.getRequestURI()).thenReturn("/v1/form/version/1.0");

		Form formDTD = controller.getFormDTD(1.0, request);

		assertEquals(expectedForm, formDTD);
		verify(appender, times(1)).doAppend(captureLoggingEvent.capture());
		LoggingEvent event = captureLoggingEvent.getAllValues().get(0);
		assertEquals("USERID: {} | PKGID: {} | URI: {} | MSG: {} ", event.getMessage());
		assertEquals(
				"USERID: userID | PKGID: null | URI: /v1/form/version/1.0 | MSG: Request for form with version: 1.0 ",
				event.getFormattedMessage());
		assertEquals(Level.INFO, event.getLevel());
	}

}
