package org.kpmp.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.kpmp.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationFilter implements Filter {

	private LoggingService logger;

	@Autowired
	public AuthorizationFilter(LoggingService logger) {
		this.logger = logger;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.logInfoMessage(this.getClass(), null, null, this.getClass().getSimpleName() + ".init",
				"Initializing filter: " + this.getClass().getSimpleName());
	}

	@Override
	public void doFilter(ServletRequest incomingRequest, ServletResponse incomingResponse, FilterChain chain)
			throws IOException, ServletException {

		// This is where we will implement the logic to talk to the user portal and do
		// authorization.

		logger.logInfoMessage(this.getClass(), null, null, this.getClass().getSimpleName() + ".doFilter",
				"Passing through authentication filter");
		chain.doFilter(incomingRequest, incomingResponse);
	}

	@Override
	public void destroy() {
		logger.logInfoMessage(this.getClass(), null, null, this.getClass().getSimpleName() + ".destroy",
				"Destroying filter: AuthorizationFilter");
	}

}
