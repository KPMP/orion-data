package org.kpmp.filters;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.logging.LoggingService;
import org.kpmp.shibboleth.ShibbolethUserService;
import org.kpmp.users.User;
import org.mockito.ArgumentCaptor;
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

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		filter = new AuthorizationFilter(logger, shibUserService, restTemplate, env);
		ReflectionTestUtils.setField(filter, "userAuthHost", "hostname");
		ReflectionTestUtils.setField(filter, "userAuthEndpoint", "endpoint");
		ReflectionTestUtils.setField(filter, "allowedGroups", Arrays.asList("group1", "group2"));
		ReflectionTestUtils.setField(filter, "kpmpGroup", "imaKpmpUser");
	}

	@After
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
	public void testDoFilter_userHasValidCookie() throws Exception {
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getShibId()).thenReturn("shibboleth id");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);
		HttpSession session = mock(HttpSession.class);
		when(incomingRequest.getSession(false)).thenReturn(session);
		Cookie goodCookie = mock(Cookie.class);
		when(goodCookie.getName()).thenReturn("shibId");
		when(goodCookie.getValue()).thenReturn("shibboleth id");
		Cookie badCookie = mock(Cookie.class);
		when(badCookie.getName()).thenReturn("Darth Vader");
		when(incomingRequest.getCookies()).thenReturn(new Cookie[] { badCookie, goodCookie });

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(chain).doFilter(incomingRequest, incomingResponse);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_userHasSomeoneElsesCookieAndGotEmptyResponse() throws Exception {
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getShibId()).thenReturn("shibboleth id");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);
		HttpSession session = mock(HttpSession.class);
		when(incomingRequest.getSession(false)).thenReturn(session);
		Cookie shibCookie = mock(Cookie.class);
		when(shibCookie.getName()).thenReturn("shibId");
		when(shibCookie.getValue()).thenReturn("not your cookie");
		when(incomingRequest.getCookies()).thenReturn(new Cookie[] { shibCookie });
		ResponseEntity<String> response = mock(ResponseEntity.class);
		when(response.getBody()).thenReturn("{}");
		when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(response);

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(chain, times(0)).doFilter(incomingRequest, incomingResponse);
		verify(logger).logInfoMessage(AuthorizationFilter.class, null,
				"MSG: Invalidating session. Cookie does not match shibId for user", incomingRequest);
		verify(session).invalidate();
		verify(incomingResponse).setStatus(HttpStatus.FAILED_DEPENDENCY.value());
		verify(logger).logErrorMessage(AuthorizationFilter.class, null,
				"Unable to parse response from User Portal, denying user shibboleth id access.  Response: {}",
				incomingRequest);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_whenNoSessionAndEmptyResponse() throws Exception {
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
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
	public void testDoFilter_noSessionHasAllowedGroup() throws Exception {
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);
		when(incomingRequest.getSession(true)).thenReturn(session);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getShibId()).thenReturn("shibboleth id");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);
		when(incomingRequest.getSession(false)).thenReturn(null);
		ResponseEntity<String> response = mock(ResponseEntity.class);
		when(response.getBody()).thenReturn("{groups: [ 'group1', 'another group']}");
		when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(response);

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(chain).doFilter(incomingRequest, incomingResponse);
		verify(session).setMaxInactiveInterval(8 * 60 * 60);
		ArgumentCaptor<Cookie> cookieJar = ArgumentCaptor.forClass(Cookie.class);
		verify(incomingResponse).addCookie(cookieJar.capture());
		assertEquals(cookieJar.getValue().getName(), "shibid");
		assertEquals(cookieJar.getValue().getValue(), "shibboleth id");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_noSessionDoesNotHaveAllowedGroupHasKpmpGroup() throws Exception {
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);
		when(incomingRequest.getSession(true)).thenReturn(session);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getShibId()).thenReturn("shibboleth id");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);
		when(incomingRequest.getSession(false)).thenReturn(null);
		ResponseEntity<String> response = mock(ResponseEntity.class);
		when(response.getBody()).thenReturn("{groups: [ 'imaKpmpUser', 'another group']}");
		when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(response);

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(chain, times(0)).doFilter(incomingRequest, incomingResponse);
		verify(incomingResponse).setStatus(HttpStatus.FORBIDDEN.value());
		verify(logger).logErrorMessage(AuthorizationFilter.class, null,
				"User does not have access to DLU: [\"imaKpmpUser\",\"another group\"]", incomingRequest);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_noSessionDoesNotHaveAllowedGroupNoKpmpGroup() throws Exception {
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);
		when(incomingRequest.getSession(true)).thenReturn(session);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getShibId()).thenReturn("shibboleth id");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);
		when(incomingRequest.getSession(false)).thenReturn(null);
		ResponseEntity<String> response = mock(ResponseEntity.class);
		when(response.getBody()).thenReturn("{groups: [ 'unrelated group', 'another group']}");
		when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(response);

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(chain, times(0)).doFilter(incomingRequest, incomingResponse);
		verify(incomingResponse).setStatus(HttpStatus.NOT_FOUND.value());
		verify(logger).logErrorMessage(AuthorizationFilter.class, null,
				"User is not part of KPMP: [\"unrelated group\",\"another group\"]", incomingRequest);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_userAuthReturned404() throws Exception {
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);
		when(incomingRequest.getSession(true)).thenReturn(session);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getShibId()).thenReturn("shibboleth id");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);
		when(incomingRequest.getSession(false)).thenReturn(null);
		when(restTemplate.getForEntity(any(String.class), any(Class.class)))
				.thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(chain, times(0)).doFilter(incomingRequest, incomingResponse);
		verify(incomingResponse).setStatus(HttpStatus.NOT_FOUND.value());
		verify(logger).logErrorMessage(AuthorizationFilter.class, null,
				"User does not exist in User Portal: shibboleth id", incomingRequest);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoFilter_userAuthReturnedAnotherErrorCode() throws Exception {
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
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
