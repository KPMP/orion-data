package org.kpmp.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter implements Filter {

	private static final String GET = "GET";
	private static final String BEARER = "Bearer ";
	private static final String AUTHORIZATION = "Authorization";
	private static final String CONTENT_TYPE = "Content-Type";
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("Initializing filter: {}", this);

	}

	@Override
	public void doFilter(ServletRequest incomingRequest, ServletResponse incomingResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) incomingRequest;
		HttpServletResponse response = (HttpServletResponse) incomingResponse;
		log.info("Request {} : {}", request.getMethod(), request.getRequestURI());
		log.info("Checking authentication for request: {}", request.getRequestURI());

		String header = request.getHeader(AUTHORIZATION);
		if (header == null || !header.startsWith(BEARER)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		}
		URL url = new URL("http://auth.kpmp.org/api/auth");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty(AUTHORIZATION, request.getHeader(AUTHORIZATION));
		connection.setRequestProperty(CONTENT_TYPE, "application/json");
		connection.setRequestMethod(GET);
		int status = connection.getResponseCode();

		if (status > 299) {
			response.sendError(status, connection.getResponseMessage());
		} else {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			if (in.readLine() != null) {
				chain.doFilter(incomingRequest, incomingResponse);
			}
		}

		connection.disconnect();

		log.info("Response: {}", response.getContentType());
	}

	@Override
	public void destroy() {
		log.info("Destroying filter: {}", this);
	}

}
