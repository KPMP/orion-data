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

import org.kpmp.JWTHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
public class AuthenticationFilter implements Filter {

	private static final String PROD_AUTH = "http://auth.kpmp.org/api/auth";
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Value("#{'${exclude.from.auth}'.split(',')}")
	private List<String> excludedUrls;
	private JWTHandler jwtHandler;

	@Autowired
	public AuthenticationFilter(JWTHandler jwtHandler) {
		this.jwtHandler = jwtHandler;
	}

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

			String header = jwtHandler.getJWTFromHeader(request);
			if (header == null) {
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
		URL url = new URL(PROD_AUTH);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION));
		connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		connection.setRequestMethod(RequestMethod.GET.name());
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
