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
import java.util.Arrays;
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
import org.kpmp.JWTHandler;
import org.kpmp.logging.LoggingService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

public class AuthenticationFilterTest {

	private AuthenticationFilter filter;
	@Mock
	private JWTHandler jwtHandler;
	@Mock
	private LoggingService logger;
	private static MockHttpUrlStreamHandler urlStreamHandler;

	@BeforeClass
	public static void setURLStreamHandlerFactory() {
		URLStreamHandlerFactory urlStreamHandlerFactory = mock(URLStreamHandlerFactory.class);
		URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);

		urlStreamHandler = new MockHttpUrlStreamHandler();
		when(urlStreamHandlerFactory.createURLStreamHandler("http")).thenReturn(urlStreamHandler);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		urlStreamHandler.resetConnections();
		filter = new AuthenticationFilter(jwtHandler, logger);
		ReflectionTestUtils.setField(filter, "excludedUrls", Arrays.asList("/this/api", "/that/api"));
	}

	@After
	public void tearDown() throws Exception {
		filter = null;
	}

	@Test
	public void testInit() throws ServletException {
		FilterConfig filterConfig = mock(FilterConfig.class);

		filter.init(filterConfig);

		verify(logger, times(1)).logInfoMessage(AuthenticationFilter.class, null, null, "AuthenticationFilter.init",
				"Initializing filter: AuthenticationFilter");
	}

	@Test
	public void testDoFilter_whenExcludePath() throws Exception {
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
		when(incomingRequest.getRequestURI()).thenReturn("/this/api");
		when(urlConnection.getInputStream()).thenReturn(IOUtils.toInputStream("some dummy data", "UTF-8"));

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(chain).doFilter(incomingRequest, incomingResponse);
		verify(logger, times(1)).logInfoMessage(AuthenticationFilter.class, null, null, "AuthenticationFilter.doFilter",
				"No authentication required for request: /this/api");

	}

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
		verify(logger).logErrorMessage(AuthenticationFilter.class, null, null, "AuthenticationFilter.doFilter",
				"Request /request/uri unauthorized.  No JWT present");
	}

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
		when(jwtHandler.getJWTFromHeader(incomingRequest)).thenReturn("stuff");
		when(incomingRequest.getHeader("Authorization")).thenReturn("Bearer stuff");
		when(incomingRequest.getMethod()).thenReturn("myMethod");
		when(incomingRequest.getRequestURI()).thenReturn("/request/uri");

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(incomingResponse).sendError(500, "No good");
		verify(logger).logErrorMessage(AuthenticationFilter.class, null, null, "AuthenticationFilter.authenticate",
				"Request /request/uri unauthorized with response code 500");
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
		when(jwtHandler.getJWTFromHeader(incomingRequest)).thenReturn("stuff");
		when(incomingRequest.getMethod()).thenReturn("myMethod");
		when(incomingRequest.getRequestURI()).thenReturn("/request/uri");
		when(urlConnection.getInputStream()).thenReturn(IOUtils.toInputStream("some dummy data", "UTF-8"));

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(chain).doFilter(incomingRequest, incomingResponse);
		verify(urlConnection).disconnect();
	}

	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testDoFilter_checkLogging() throws IOException, ServletException {

		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		when(incomingRequest.getMethod()).thenReturn("myMethod");
		when(incomingRequest.getRequestURI()).thenReturn("/request/uri");
		when(incomingRequest.getHeader("Authorization")).thenReturn("Bearer stuff");
		when(jwtHandler.getJWTFromHeader(incomingRequest)).thenReturn("stuff");
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		when(incomingResponse.getContentType()).thenReturn("awesome content");
		FilterChain chain = mock(FilterChain.class);
		String href = "http://auth.kpmp.org/api/auth";
		HttpURLConnection urlConnection = mock(HttpURLConnection.class);
		urlStreamHandler.addConnection(new URL(href), urlConnection);
		when(urlConnection.getInputStream()).thenReturn(IOUtils.toInputStream("some dummy data", "UTF-8"));
		when(jwtHandler.getUserIdFromHeader(incomingRequest)).thenReturn("user123");

		filter.doFilter(incomingRequest, incomingResponse, chain);

		ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
		ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> packageIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
		verify(logger, times(3)).logInfoMessage(classCaptor.capture(), userIdCaptor.capture(),
				packageIdCaptor.capture(), uriCaptor.capture(), messageCaptor.capture());
		List<Class> allClasses = classCaptor.getAllValues();
		List<String> allUserIds = userIdCaptor.getAllValues();
		List<String> allPackageIds = packageIdCaptor.getAllValues();
		List<String> allUris = uriCaptor.getAllValues();
		List<String> allMessages = messageCaptor.getAllValues();
		assertEquals(AuthenticationFilter.class, allClasses.get(0));
		assertEquals(null, allUserIds.get(0));
		assertEquals(null, allPackageIds.get(0));
		assertEquals("AuthenticationFilter.doFilter", allUris.get(0));
		assertEquals("Request myMethod : /request/uri", allMessages.get(0));
		assertEquals(AuthenticationFilter.class, allClasses.get(1));
		assertEquals(null, allUserIds.get(1));
		assertEquals(null, allPackageIds.get(1));
		assertEquals("AuthenticationFilter.doFilter", allUris.get(1));
		assertEquals("Checking authentication for request: /request/uri", allMessages.get(1));
		assertEquals(AuthenticationFilter.class, allClasses.get(2));
		assertEquals(null, allUserIds.get(2));
		assertEquals(null, allPackageIds.get(2));
		assertEquals("AuthenticationFilter.doFilter", allUris.get(2));
		assertEquals("Response: awesome content", allMessages.get(2));
	}

	@Test
	public void testDestroy() {

		filter.destroy();

		verify(logger).logInfoMessage(AuthenticationFilter.class, null, null, "AuthenticationFilter.destroy",
				"Destroying filter: AuthenticationFilter");
	}

}
