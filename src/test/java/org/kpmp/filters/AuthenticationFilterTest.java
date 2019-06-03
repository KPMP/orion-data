package org.kpmp.filters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

public class AuthenticationFilterTest extends AuthenticationFilter {

	private AuthenticationFilter filter;
	@SuppressWarnings("rawtypes")
	@Mock
	private Appender appender;
	@Captor
	private ArgumentCaptor<LoggingEvent> captureLoggingEvent;
	private static MockHttpUrlStreamHandler urlStreamHandler;

	@BeforeClass
	public static void setURLStreamHandlerFactory() {
		URLStreamHandlerFactory urlStreamHandlerFactory = mock(URLStreamHandlerFactory.class);
		URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);

		urlStreamHandler = new MockHttpUrlStreamHandler();
		when(urlStreamHandlerFactory.createURLStreamHandler("http")).thenReturn(urlStreamHandler);
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		urlStreamHandler.resetConnections();
		Logger root = (Logger) LoggerFactory.getLogger(AuthenticationFilter.class);
		root.addAppender(appender);
		root.setLevel(Level.INFO);
		filter = new AuthenticationFilter();
	}

	@After
	public void tearDown() throws Exception {
		filter = null;
		appender = null;
		captureLoggingEvent = null;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInit() throws ServletException {
		FilterConfig filterConfig = mock(FilterConfig.class);

		filter.init(filterConfig);

		verify(appender, times(1)).doAppend(captureLoggingEvent.capture());
		LoggingEvent event = captureLoggingEvent.getAllValues().get(0);
		assertEquals("Initializing filter: {}", event.getMessage());
		assertEquals("Initializing filter: AuthenticationFilter", event.getFormattedMessage());
		assertEquals(Level.INFO, event.getLevel());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_whenMissingJWT() throws Exception {
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		String href = "http://auth.kpmp.org/api/auth";
		HttpURLConnection urlConnection = mock(HttpURLConnection.class);
		urlStreamHandler.addConnection(new URL(href), urlConnection);
		when(incomingRequest.getMethod()).thenReturn("myMethod");
		when(incomingRequest.getRequestURI()).thenReturn("/request/uri");

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(incomingResponse).sendError(401, "Unauthorized");
		verify(appender, times(3)).doAppend(captureLoggingEvent.capture());
		List<LoggingEvent> loggingValues = captureLoggingEvent.getAllValues();
		LoggingEvent event = loggingValues.get(2);
		assertEquals("Request {} unauthorized.  No JWT present", event.getMessage());
		assertEquals("Request /request/uri unauthorized.  No JWT present", event.getFormattedMessage());
		assertEquals(Level.ERROR, event.getLevel());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_whenReturnsResponseCodeOver299() throws Exception {
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		String href = "http://auth.kpmp.org/api/auth";
		HttpURLConnection urlConnection = mock(HttpURLConnection.class);
		urlStreamHandler.addConnection(new URL(href), urlConnection);
		when(urlConnection.getResponseCode()).thenReturn(500);
		when(urlConnection.getResponseMessage()).thenReturn("No good");
		when(incomingRequest.getHeader("Authorization")).thenReturn("Bearer stuff");
		when(incomingRequest.getMethod()).thenReturn("myMethod");
		when(incomingRequest.getRequestURI()).thenReturn("/request/uri");

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(incomingResponse).sendError(500, "No good");
		verify(appender, times(4)).doAppend(captureLoggingEvent.capture());
		List<LoggingEvent> loggingValues = captureLoggingEvent.getAllValues();
		LoggingEvent event = loggingValues.get(2);
		assertEquals("Request {} unauthorized with response code {}", event.getMessage());
		assertEquals("Request /request/uri unauthorized with response code 500", event.getFormattedMessage());
		assertEquals(Level.ERROR, event.getLevel());
		verify(urlConnection).disconnect();
	}

	@Test
	public void testDoFilter() throws Exception {
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		String href = "http://auth.kpmp.org/api/auth";
		HttpURLConnection urlConnection = mock(HttpURLConnection.class);
		urlStreamHandler.addConnection(new URL(href), urlConnection);
		when(urlConnection.getResponseCode()).thenReturn(200);
		when(urlConnection.getResponseMessage()).thenReturn("All good");
		when(incomingRequest.getHeader("Authorization")).thenReturn("Bearer stuff");
		when(incomingRequest.getMethod()).thenReturn("myMethod");
		when(incomingRequest.getRequestURI()).thenReturn("/request/uri");
		when(urlConnection.getInputStream()).thenReturn(IOUtils.toInputStream("some dummy data", "UTF-8"));

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(chain).doFilter(incomingRequest, incomingResponse);
		verify(urlConnection).disconnect();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_checkLogging() throws IOException, ServletException {

		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		when(incomingRequest.getMethod()).thenReturn("myMethod");
		when(incomingRequest.getRequestURI()).thenReturn("/request/uri");
		when(incomingRequest.getHeader("Authorization")).thenReturn("Bearer stuff");
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		when(incomingResponse.getContentType()).thenReturn("awesome content");
		FilterChain chain = mock(FilterChain.class);
		String href = "http://auth.kpmp.org/api/auth";
		HttpURLConnection urlConnection = mock(HttpURLConnection.class);
		urlStreamHandler.addConnection(new URL(href), urlConnection);
		when(urlConnection.getInputStream()).thenReturn(IOUtils.toInputStream("some dummy data", "UTF-8"));

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(appender, times(3)).doAppend(captureLoggingEvent.capture());
		List<LoggingEvent> loggingValues = captureLoggingEvent.getAllValues();
		LoggingEvent event = loggingValues.get(0);
		assertEquals("Request {} : {}", event.getMessage());
		assertEquals("Request myMethod : /request/uri", event.getFormattedMessage());
		assertEquals(Level.INFO, event.getLevel());
		event = loggingValues.get(1);
		assertEquals("Checking authentication for request: {}", event.getMessage());
		assertEquals("Checking authentication for request: /request/uri", event.getFormattedMessage());
		assertEquals(Level.INFO, event.getLevel());
		event = loggingValues.get(2);
		assertEquals("Response: {}", event.getMessage());
		assertEquals("Response: awesome content", event.getFormattedMessage());
		assertEquals(Level.INFO, event.getLevel());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDestroy() {

		filter.destroy();

		verify(appender, times(1)).doAppend(captureLoggingEvent.capture());
		LoggingEvent event = captureLoggingEvent.getAllValues().get(0);
		assertEquals("Destroying filter: {}", event.getMessage());
		assertEquals("Destroying filter: AuthenticationFilter", event.getFormattedMessage());
		assertEquals(Level.INFO, event.getLevel());
	}

}
