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
import org.kpmp.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
public class AuthenticationFilter implements Filter {

	private static final String PROD_AUTH = "http://auth.kpmp.org/api/auth";
	@Value("#{'${exclude.from.auth}'.split(',')}")
	private List<String> excludedUrls;
	private JWTHandler jwtHandler;
	private LoggingService logger;

	@Autowired
	public AuthenticationFilter(JWTHandler jwtHandler, LoggingService logger) {
		this.jwtHandler = jwtHandler;
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
		HttpServletRequest request = (HttpServletRequest) incomingRequest;
		HttpServletResponse response = (HttpServletResponse) incomingResponse;
		logger.logInfoMessage(this.getClass(), null, null, this.getClass().getSimpleName() + ".doFilter",
				"Request " + request.getMethod() + " : " + request.getRequestURI());
		String uri = request.getRequestURI();
		if (!excludedUrls.contains(uri)) {

			logger.logInfoMessage(this.getClass(), null, null, this.getClass().getSimpleName() + ".doFilter",
					"Checking authentication for request: " + uri);

			String header = jwtHandler.getJWTFromHeader(request);
			if (header == null) {
				logger.logErrorMessage(this.getClass(), null, null, this.getClass().getSimpleName() + ".doFilter",
						"Request " + uri + " unauthorized.  No JWT present");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
			} else {
				authenticate(incomingRequest, incomingResponse, chain, request, response, uri);
			}
		} else {
			logger.logInfoMessage(this.getClass(), null, null, this.getClass().getSimpleName() + ".doFilter",
					"No authentication required for request: " + uri);
			chain.doFilter(incomingRequest, incomingResponse);
		}
		logger.logInfoMessage(this.getClass(), jwtHandler.getUserIdFromHeader(request), null,
				this.getClass().getSimpleName() + ".doFilter", "Response: " + response.getContentType());
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
			logger.logErrorMessage(this.getClass(), null, null, this.getClass().getSimpleName() + ".authenticate",
					"Request " + uri + " unauthorized with response code " + status);
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
		logger.logInfoMessage(this.getClass(), null, null, this.getClass().getSimpleName() + ".destroy",
				"Destroying filter: " + this.getClass().getSimpleName());
	}

}
