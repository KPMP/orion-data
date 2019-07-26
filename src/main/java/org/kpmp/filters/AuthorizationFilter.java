package org.kpmp.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.kpmp.logging.LoggingService;
import org.kpmp.shibboleth.ShibbolethUserService;
import org.kpmp.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationFilter implements Filter {

	private LoggingService logger;
	private ShibbolethUserService shibUserService;

	@Autowired
	public AuthorizationFilter(LoggingService logger, ShibbolethUserService shibUserService) {
		this.logger = logger;
		this.shibUserService = shibUserService;
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
		User user = shibUserService.getUser(request);
		// This is where we will implement the logic to talk to the user portal and do
		// authorization.

		logger.logInfoMessage(this.getClass(), user, null, this.getClass().getSimpleName() + ".doFilter",
				"Passing through authentication filter");
		chain.doFilter(incomingRequest, incomingResponse);
	}

	@Override
	public void destroy() {
		logger.logInfoMessage(this.getClass(), null, null, this.getClass().getSimpleName() + ".destroy",
				"Destroying filter: AuthorizationFilter");
	}

}
