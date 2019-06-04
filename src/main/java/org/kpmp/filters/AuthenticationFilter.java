package org.kpmp.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter implements Filter {

	private static final String HTTP_AUTH_KPMP_ORG_API_AUTH = "http://auth.kpmp.org/api/auth";
	private static final String GET = "GET";
	private static final String BEARER = "Bearer ";
	private static final String AUTHORIZATION = "Authorization";
	private static final String CONTENT_TYPE = "Content-Type";
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Value("#{'${exclude.from.auth}'.split(',')}")
	private List<String> excludedUrls;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("Initializing filter: {}", this.getClass().getSimpleName());

	}

	@Override
	public void doFilter(ServletRequest incomingRequest, ServletResponse incomingResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) incomingRequest;
		HttpServletResponse response = (HttpServletResponse) incomingResponse;
		log.info("Request {} : {}", request.getMethod(), request.getRequestURI());

		String uri = request.getRequestURI();
		if (!excludedUrls.contains(uri)) {

			log.info("Checking authentication for request: {}", uri);

			String header = request.getHeader(AUTHORIZATION);
			if (header == null || !header.startsWith(BEARER)) {
				log.error("Request {} unauthorized.  No JWT present", uri);
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
			} else {
				authenticate(incomingRequest, incomingResponse, chain, request, response, uri);
			}
		} else {
			log.info("No authentication required for request: {}", uri);
			chain.doFilter(incomingRequest, incomingResponse);
		}
		log.info("Response: {}", response.getContentType());
	}

	private void authenticate(ServletRequest incomingRequest, ServletResponse incomingResponse, FilterChain chain,
			HttpServletRequest request, HttpServletResponse response, String uri)
			throws MalformedURLException, IOException, ProtocolException, ServletException {
		URL url = new URL(HTTP_AUTH_KPMP_ORG_API_AUTH);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty(AUTHORIZATION, request.getHeader(AUTHORIZATION));
		connection.setRequestProperty(CONTENT_TYPE, "application/json");
		connection.setRequestMethod(GET);
		int status = connection.getResponseCode();

		if (status > 299 && status != 302) {
			log.error("Request {} unauthorized with response code {}", uri, status);
			response.sendError(status, connection.getResponseMessage());
		} else {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			if (in.readLine() != null) {
				chain.doFilter(incomingRequest, incomingResponse);
			}
		}

		connection.disconnect();
	}

	@Override
	public void destroy() {
		log.info("Destroying filter: {}", this.getClass().getSimpleName());
	}

}
