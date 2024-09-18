package org.miktmc.filters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.Notification.NotificationHandler;
import org.miktmc.logging.LoggingService;
import org.miktmc.shibboleth.ShibbolethUserService;
import org.miktmc.users.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class AuthorizationFilterTest {

	private AuthorizationFilter filter;
	@Mock
	private LoggingService logger;
	@Mock
	private ShibbolethUserService shibUserService;
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private Environment env;
    @Mock
    private NotificationHandler handler;

	@BeforeEach
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		filter = new AuthorizationFilter(logger, shibUserService, restTemplate, env, handler);
		ReflectionTestUtils.setField(filter, "userAuthHost", "hostname");
		ReflectionTestUtils.setField(filter, "userAuthEndpoint", "endpoint");
		ReflectionTestUtils.setField(filter, "allowedGroups", Arrays.asList("group1", "group2"));
		ReflectionTestUtils.setField(filter, "allowedEndpoints", Arrays.asList("uri1"));
	}

	@AfterEach
	public void tearDown() throws Exception {
		filter = null;
	}

	@Test
	public void testInit() throws ServletException {
		FilterConfig filterConfig = mock(FilterConfig.class);

		filter.init(filterConfig);

		verify(logger, times(1)).logInfoMessage(AuthorizationFilter.class, null, null, "AuthorizationFilter.init",
				"Initializing filter: AuthorizationFilter");
	}

	@Test
	public void testDoFilter_skippableURI() throws Exception { // eslint-disable-line no-eval
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		when(incomingRequest.getRequestURI()).thenReturn("uri1");
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getShibId()).thenReturn("shibboleth id");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);
		HttpSession session = mock(HttpSession.class);
		when(incomingRequest.getSession(false)).thenReturn(session);

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(chain).doFilter(incomingRequest, incomingResponse);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_nonChunkedFileUpload() throws Exception {
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		when(incomingRequest.getRequestURI()).thenReturn("/v1/packages/123-3435-kljlkj/files");
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getShibId()).thenReturn("shibboleth id");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);
		HttpSession session = mock(HttpSession.class);
		when(incomingRequest.getSession(true)).thenReturn(session);
		ResponseEntity<String> response = mock(ResponseEntity.class);
		when(response.getBody()).thenReturn("{groups: [ 'group1', 'another group'], active: true}");
		when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(response);

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(incomingRequest, times(1)).getSession(true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_firstChunkFileUpload() throws Exception {
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		when(incomingRequest.getRequestURI()).thenReturn("/v1/packages/123-3435-kljlkj/files");
		when(incomingRequest.getParameter("qqpartindex")).thenReturn("0");
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getShibId()).thenReturn("shibboleth id");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);
		HttpSession session = mock(HttpSession.class);
		when(incomingRequest.getSession(true)).thenReturn(session);
		ResponseEntity<String> response = mock(ResponseEntity.class);
		when(response.getBody()).thenReturn("{groups: [ 'group1', 'another group'], active: true}");
		when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(response);

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(incomingRequest, times(1)).getSession(true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_notFirstChunkFileUpload() throws Exception {
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		when(incomingRequest.getRequestURI()).thenReturn("/v1/packages/123-3435-kljlkj/files");
		when(incomingRequest.getParameter("qqpartindex")).thenReturn("3");
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getShibId()).thenReturn("shibboleth id");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);
		HttpSession session = mock(HttpSession.class);
		when(incomingRequest.getSession(true)).thenReturn(session);
		ResponseEntity<String> response = mock(ResponseEntity.class);
		when(response.getBody()).thenReturn("{groups: [ 'group1', 'another group'], active: true}");
		when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(response);

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(incomingRequest, times(0)).getSession(true);
		verify(logger).logInfoMessage(AuthorizationFilter.class, null, null,
				"AuthorizationFilter.isFirstFilePartUpload", "file upload: not first part, skipping user auth check");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_whenNoSessionAndEmptyResponse() throws Exception { // eslint-disable-line no-eval
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		when(incomingRequest.getRequestURI()).thenReturn("anything");
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getShibId()).thenReturn("shibboleth id");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);
		when(incomingRequest.getSession(false)).thenReturn(null);
		ResponseEntity<String> response = mock(ResponseEntity.class);
		when(response.getBody()).thenReturn("{}");
		when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(response);

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(chain, times(0)).doFilter(incomingRequest, incomingResponse);
		verify(incomingResponse).setStatus(HttpStatus.FAILED_DEPENDENCY.value());
		verify(logger).logErrorMessage(AuthorizationFilter.class, null,
				"Unable to parse response from User Portal, denying user shibboleth id access.  Response: {}",
				incomingRequest);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_noSessionHasAllowedGroup() throws Exception { // eslint-disable-line no-eval
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		when(incomingRequest.getRequestURI()).thenReturn("anything");
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);
		when(incomingRequest.getSession(true)).thenReturn(session);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getShibId()).thenReturn("shibboleth id");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);
		when(incomingRequest.getSession(false)).thenReturn(null);
		ResponseEntity<String> response = mock(ResponseEntity.class);
		when(response.getBody()).thenReturn("{groups: [ 'group1', 'another group'], active: true}");
		when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(response);

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(chain).doFilter(incomingRequest, incomingResponse);
		verify(session).setMaxInactiveInterval(8 * 60 * 60);
	}

    @SuppressWarnings("unchecked")
    @Test
    public void testDoFilter_noSessionIncorrectGroups() throws Exception {
        HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		when(incomingRequest.getRequestURI()).thenReturn("anything");
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);
		when(incomingRequest.getSession(true)).thenReturn(session);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getShibId()).thenReturn("shibboleth id");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);
		when(incomingRequest.getSession(false)).thenReturn(null);
		ResponseEntity<String> response = mock(ResponseEntity.class);
		when(response.getBody()).thenReturn("{groups: [ 'another group'], active: true}");
		when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(response);

        filter.doFilter(incomingRequest, incomingResponse, chain);

        verify(chain, times(0)).doFilter(incomingRequest, incomingResponse);
		verify(incomingResponse).setStatus(HttpStatus.NOT_FOUND.value());
		verify(logger).logErrorMessage(AuthorizationFilter.class, null,
				"User does not have access to DLU: [\"another group\"]",
				incomingRequest);
        verify(handler).sendNotification("shibboleth id", "hostname");
    }

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_noSessionBlankShibId() throws Exception { // eslint-disable-line
		// no-eval
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		when(incomingRequest.getRequestURI()).thenReturn("anything");
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);
		when(incomingRequest.getSession(true)).thenReturn(session);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getShibId()).thenReturn("");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);
		when(incomingRequest.getSession(false)).thenReturn(null);
		ResponseEntity<String> response = mock(ResponseEntity.class);
		when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(response);

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(chain, times(0)).doFilter(incomingRequest, incomingResponse);
		verify(incomingResponse).setStatus(HttpStatus.FORBIDDEN.value());
		verify(logger).logWarnMessage(AuthorizationFilter.class, null, "request with no shib id", incomingRequest);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_userAuthReturnedAnotherErrorCode() throws Exception { // eslint-disable-line no-eval
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		when(incomingRequest.getRequestURI()).thenReturn("anything");
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);
		when(incomingRequest.getSession(true)).thenReturn(session);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getShibId()).thenReturn("shibboleth id");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);
		when(incomingRequest.getSession(false)).thenReturn(null);
		when(restTemplate.getForEntity(any(String.class), any(Class.class)))
				.thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(chain, times(0)).doFilter(incomingRequest, incomingResponse);
		verify(incomingResponse).setStatus(HttpStatus.FAILED_DEPENDENCY.value());
		verify(logger).logErrorMessage(AuthorizationFilter.class, null,
				"Unable to get user information. User auth returned status code: 500", incomingRequest);
	}

	@Test
	public void testDestroy() {

		filter.destroy();

		verify(logger).logInfoMessage(AuthorizationFilter.class, null, null, "AuthorizationFilter.destroy",
				"Destroying filter: AuthorizationFilter");
	}

}
