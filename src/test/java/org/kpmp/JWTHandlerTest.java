package org.kpmp;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

public class JWTHandlerTest extends JWTHandler {

	private JWTHandler jwtHandler;
	@SuppressWarnings("rawtypes")
	@Mock
	private Appender appender;
	@Captor
	private ArgumentCaptor<LoggingEvent> captureLoggingEvent;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Logger root = (Logger) LoggerFactory.getLogger(JWTHandler.class);
		root.addAppender(appender);
		root.setLevel(Level.INFO);
		jwtHandler = new JWTHandler();
	}

	@After
	public void tearDown() throws Exception {
		appender = null;
		jwtHandler = null;
		captureLoggingEvent = null;
	}

	@Test
	public void testGetUserIdFromToken() throws Exception {
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJyb3NlbWlAdW1pY2guZWR1IiwiZXhwIjoxNTU5OTQ2MTE5LCJ1c2VyIjoie1wiZmlyc3ROYW1lXCI6XCJNaWNoYWVsXCIsXCJsYXN0TmFtZVwiOlwiUm9zZVwiLFwiZGlzcGxheU5hbWVcIjpcIk1pY2hhZWwgUm9zZVwiLFwiZW1haWxcIjpcInJvc2VtaUB1bWljaC5lZHVcIn0ifQ.PVhgjr36JX9jk7NocOhpchVkr3iImzfhJRsOIhJmO0OoLTTJMPHNI6mWKCUWP9ecDMHboc-U00BVt6YlG26-3Q";

		String user = jwtHandler.getUserIdFromToken(token);

		assertEquals("rosemi@umich.edu", user);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetUserIdFromToken_whenTokenMalformed() throws Exception {
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9eyJzdWIiOiJyb3NlbWlAdW1pY2guZWR1IiwiZXhwIjoxNTU5OTQ2MTE5LCJ1c2VyIjoie1wiZmlyc3ROYW1lXCI6XCJNaWNoYWVsXCIsXCJsYXN0TmFtZVwiOlwiUm9zZVwiLFwiZGlzcGxheU5hbWVcIjpcIk1pY2hhZWwgUm9zZVwiLFwiZW1haWxcIjpcInJvc2VtaUB1bWljaC5lZHVcIn0ifQPVhgjr36JX9jk7NocOhpchVkr3iImzfhJRsOIhJmO0OoLTTJMPHNI6mWKCUWP9ecDMHboc-U00BVt6YlG26-3Q";

		String user = jwtHandler.getUserIdFromToken(token);

		assertEquals("", user);
		verify(appender, times(1)).doAppend(captureLoggingEvent.capture());
		LoggingEvent event = captureLoggingEvent.getAllValues().get(0);
		assertEquals("USERID: {} | PKGID: {} | URI: {} | MSG: {} ", event.getMessage());
		assertEquals(
				"USERID: null | PKGID: null | URI: JWTHandler.getUserIdFromToken | MSG: Unable to get UserID from JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9eyJzdWIiOiJyb3NlbWlAdW1pY2guZWR1IiwiZXhwIjoxNTU5OTQ2MTE5LCJ1c2VyIjoie1wiZmlyc3ROYW1lXCI6XCJNaWNoYWVsXCIsXCJsYXN0TmFtZVwiOlwiUm9zZVwiLFwiZGlzcGxheU5hbWVcIjpcIk1pY2hhZWwgUm9zZVwiLFwiZW1haWxcIjpcInJvc2VtaUB1bWljaC5lZHVcIn0ifQPVhgjr36JX9jk7NocOhpchVkr3iImzfhJRsOIhJmO0OoLTTJMPHNI6mWKCUWP9ecDMHboc-U00BVt6YlG26-3Q ",
				event.getFormattedMessage());
		assertEquals(Level.WARN, event.getLevel());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetUserIdFromToken_whenTokenNotBase64() throws Exception {
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzUxMiJ9.eyJzdWIiOiJyb3NlbWlAdW1pY2guFFFR1IiwiZXhwIjoxNTU5OTQ2MTE5LCJ1c2VyIjoie1wiZmlyc3ROYW1lXCI6XCJNaWNoYWVsXCIsXCJsYXN0TmFtZVwiOlwiUm9zZVwiLFwiZGlzcGxheU5hbWVcIjpcIk1pY2hhZWwgUm9zZVwiLFwiZW1haWxcIjpcInJvc2VtaUB1bWljaC5lZHVcIn0ifQ.PVhgjr36JX9jk7NocOhpchVkr3iImzfhJRsOIhJmO0OoLTTJMPHNI6mWKCUWP9ecDMHboc-U00BVt6YlG26-3Q";

		String user = jwtHandler.getUserIdFromToken(token);

		assertEquals("", user);
		verify(appender, times(1)).doAppend(captureLoggingEvent.capture());
		LoggingEvent event = captureLoggingEvent.getAllValues().get(0);
		assertEquals("USERID: {} | PKGID: {} | URI: {} | MSG: {} ", event.getMessage());
		assertEquals(
				"USERID: null | PKGID: null | URI: JWTHandler.getUserIdFromToken | MSG: Unable to get UserID from token: Illegal unquoted character ((CTRL-CHAR, code 20)): has to be escaped using backslash to be included in string value\n"
						+ " at [Source: (String)\"{\"sub\":\"rosemi@umich.QQԈ����������������䰉�͕Ȉ��p������9���p��p�5������p��p�����9���p��p�I�͕p��p��������9���p��p�5�������I�͕p��p������p��p�ɽ͕��յ�������p���\"; line: 1, column: 23] ",
				event.getFormattedMessage());
		assertEquals(Level.ERROR, event.getLevel());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetUserIdFromToken_whenTokenIsNull() throws Exception {

		String user = jwtHandler.getUserIdFromToken(null);

		assertEquals("", user);
		verify(appender, times(1)).doAppend(captureLoggingEvent.capture());
		LoggingEvent event = captureLoggingEvent.getAllValues().get(0);
		assertEquals("USERID: {} | PKGID: {} | URI: {} | MSG: {} ", event.getMessage());
		assertEquals(
				"USERID: null | PKGID: null | URI: JWTHandler.getUserIdFromToken | MSG: Unable to get UserID from JWT null ",
				event.getFormattedMessage());
		assertEquals(Level.WARN, event.getLevel());
	}

	@Test
	public void testGetJWTFromHeader() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token");

		String token = jwtHandler.getJWTFromHeader(request);

		assertEquals("token", token);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetJWTFromHeader_whenAuthHeaderMalformed() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("BEARS token");

		String token = jwtHandler.getJWTFromHeader(request);

		assertEquals(null, token);
		verify(appender, times(1)).doAppend(captureLoggingEvent.capture());
		LoggingEvent event = captureLoggingEvent.getAllValues().get(0);
		assertEquals("USERID: {} | PKGID: {} | URI: {} | MSG: {} ", event.getMessage());
		assertEquals(
				"USERID: null | PKGID: null | URI: JWTHandler.getJWTFromHeader | MSG: Authorization Header either missing or malformed ",
				event.getFormattedMessage());
		assertEquals(Level.WARN, event.getLevel());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetJWTFromHeader_whenNoAuthHeader() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);

		String token = jwtHandler.getJWTFromHeader(request);

		assertEquals(null, token);
		verify(appender, times(1)).doAppend(captureLoggingEvent.capture());
		LoggingEvent event = captureLoggingEvent.getAllValues().get(0);
		assertEquals("USERID: {} | PKGID: {} | URI: {} | MSG: {} ", event.getMessage());
		assertEquals(
				"USERID: null | PKGID: null | URI: JWTHandler.getJWTFromHeader | MSG: Authorization Header either missing or malformed ",
				event.getFormattedMessage());
		assertEquals(Level.WARN, event.getLevel());
	}

}
