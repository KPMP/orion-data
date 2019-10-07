package org.kpmp.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.kpmp.logging.LoggingService;
import org.kpmp.shibboleth.ShibbolethUserService;
import org.kpmp.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthorizationFilter implements Filter {

	private LoggingService logger;
	private ShibbolethUserService shibUserService;
	private RestTemplate restTemplate;

	@Value("${user.auth.host}")
	private String userAuthHost;
	@Value("${user.auth.endpoint}")
	private String userAuthEndpoint;

	@Autowired
	public AuthorizationFilter(LoggingService logger, ShibbolethUserService shibUserService, RestTemplate restTemplate,
			Environment env) {
		this.logger = logger;
		this.shibUserService = shibUserService;
		this.restTemplate = restTemplate;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.logInfoMessage(this.getClass(), null, null, this.getClass().getSimpleName() + ".init",
				"Initializing filter: " + this.getClass().getSimpleName());
	}

	@Override
	public void doFilter(ServletRequest incomingRequest, ServletResponse incomingResponse, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) incomingRequest;
		HttpServletResponse response = (HttpServletResponse) incomingResponse;

		HttpSession existingSession = request.getSession(false);
		Cookie[] cookies = request.getCookies();
		User user = shibUserService.getUser(request);
		String shibId = user.getShibId();

		if (hasExistingSession(existingSession, shibId, cookies, request)) {
			chain.doFilter(incomingRequest, incomingResponse);
		} else {
			// get user information
			String uri = userAuthHost + userAuthEndpoint + "/";
		}

	}

	private boolean hasExistingSession(HttpSession existingSession, String shibId, Cookie[] cookies,
			HttpServletRequest request) {
		if (existingSession != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("shibId")) {
					if (cookie.getValue().equals(shibId)) {
						return true;
					} else {
						logger.logInfoMessage(this.getClass(), null,
								"MSG: Invalidating session. Cookie does not match shibId for user", request);
						existingSession.invalidate();
						return false;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void destroy() {
		logger.logInfoMessage(this.getClass(), null, null, this.getClass().getSimpleName() + ".destroy",
				"Destroying filter: AuthorizationFilter");
	}

}
