package org.kpmp.filters;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.logging.LoggingService;
import org.kpmp.shibboleth.ShibbolethUserService;
import org.kpmp.users.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AuthorizationFilterTest {

	private AuthorizationFilter filter;
	@Mock
	private LoggingService logger;
	@Mock
	private ShibbolethUserService shibUserService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		filter = new AuthorizationFilter(logger, shibUserService);
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
	public void testDoFilter() throws Exception {
		HttpServletRequest incomingRequest = mock(HttpServletRequest.class);
		HttpServletResponse incomingResponse = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		User user = mock(User.class);
		when(user.getEmail()).thenReturn("jimmy@buffet.org");
		when(shibUserService.getUser(incomingRequest)).thenReturn(user);

		filter.doFilter(incomingRequest, incomingResponse, chain);

		verify(chain).doFilter(incomingRequest, incomingResponse);
		verify(logger, times(1)).logInfoMessage(AuthorizationFilter.class, user, null, "AuthorizationFilter.doFilter",
				"Passing through authentication filter");
	}

	@Test
	public void testDestroy() {

		filter.destroy();

		verify(logger).logInfoMessage(AuthorizationFilter.class, null, null, "AuthorizationFilter.destroy",
				"Destroying filter: AuthorizationFilter");
	}

}
