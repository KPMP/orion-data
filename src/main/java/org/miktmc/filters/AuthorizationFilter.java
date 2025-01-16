package org.miktmc.filters;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.miktmc.logging.LoggingService;
import org.miktmc.shibboleth.ShibbolethUserService;
import org.miktmc.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.miktmc.Notification.NotificationHandler;

@Component
public class AuthorizationFilter implements Filter {

	private static final String FILE_PART_INDEX = "qqpartindex";
	private static final String USER_NO_DLU_ACCESS = "User does not have access to DLU: ";
	private static final String GROUPS_KEY = "groups";
    private static final String FIRST_NAME_KEY = "first_name";
    private static final String LAST_NAME_KEY = "last_name";
	private static final String USER_DOES_NOT_EXIST = "User does not exist in User Portal: ";
	private static final String CLIENT_ID_PROPERTY = "CLIENT_ID";
	private static final int SECONDS_IN_MINUTE = 60;
	private static final int MINUTES_IN_HOUR = 60;
	private static final int SESSION_TIMEOUT_HOURS = 8;
	private static final int SESSION_TIMEOUT_SECONDS = SECONDS_IN_MINUTE * MINUTES_IN_HOUR * SESSION_TIMEOUT_HOURS;
	private static final String FILE_PART_UPLOAD_URI_MATCHER = "/v1/packages/(.*)/files";

	private LoggingService logger;
	private ShibbolethUserService shibUserService;
	private RestTemplate restTemplate;
    private NotificationHandler notificationHandler;

	@Value("${user.auth.host}")
	private String userAuthHost;
	@Value("${user.auth.endpoint}")
	private String userAuthEndpoint;
	@Value("#{'${user.auth.allowed.groups}'.split(',')}")
	private List<String> allowedGroups;
	@Value("#{'${user.auth.allow.endpoints}'.split(',')}")
	private List<String> allowedEndpoints;
	private Environment env;

	@Autowired
	public AuthorizationFilter(LoggingService logger, ShibbolethUserService shibUserService, RestTemplate restTemplate,
			Environment env, NotificationHandler notificationHandler) {
		this.logger = logger;
		this.shibUserService = shibUserService;
		this.restTemplate = restTemplate;
		this.env = env;
        this.notificationHandler = notificationHandler;
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

		Cookie[] cookies = request.getCookies();
		User user = shibUserService.getUser(request);
		String shibId = user.getShibId();
		if (hasExistingSession(user, shibId, cookies, request) || allowedEndpoints.contains(request.getRequestURI())
				|| !isFirstFilePartUpload(request)) {

			chain.doFilter(request, response);
		} else if (shibId != null && !shibId.isEmpty()) {
			String clientId = env.getProperty(CLIENT_ID_PROPERTY);
			String uri = userAuthHost + userAuthEndpoint + "/" + clientId + "/" + shibId;
			try {
				ResponseEntity<String> userInfoResponse = restTemplate.getForEntity(uri, String.class);
				String userInfo = userInfoResponse.getBody();
				try {
					JSONObject userJson = new JSONObject(userInfo);
					JSONArray userGroups = userJson.getJSONArray(GROUPS_KEY);
                    String firstName = userJson.getString(FIRST_NAME_KEY);
                    String lastName = userJson.getString(LAST_NAME_KEY);

					if (isAllowed(userGroups) && userJson.getBoolean("active")) {
						HttpSession session = request.getSession(true);
						session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
						session.setAttribute("roles", userGroups);
						session.setAttribute("shibid", shibId);
                        session.setAttribute("lastName", lastName);
                        session.setAttribute("firstName", firstName);
						chain.doFilter(request, response);
					} else {
						handleError(USER_NO_DLU_ACCESS + userGroups, HttpStatus.NOT_FOUND, request, response);
                        notificationHandler.sendNotification(shibId, userAuthHost);
					}


				} catch (JSONException e) {
					handleError("Unable to parse response from User Portal, denying user " + shibId
							+ " access.  Response: " + userInfo, HttpStatus.FAILED_DEPENDENCY, request, response);

				}

			} catch (HttpClientErrorException e) {
				int statusCode = e.getRawStatusCode();
				if (statusCode == HttpStatus.NOT_FOUND.value()) {
					handleError(USER_DOES_NOT_EXIST + shibId, HttpStatus.NOT_FOUND, request, response);
                    notificationHandler.sendNotification(shibId, userAuthHost);
				} else if (statusCode != HttpStatus.OK.value()) {
					handleError("Unable to get user information. User auth returned status code: " + statusCode,
							HttpStatus.FAILED_DEPENDENCY, request, response);
				}
			}
		} else {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			logger.logWarnMessage(this.getClass(), null, "request with no shib id", request);
		}

	}

	private boolean isFirstFilePartUpload(HttpServletRequest request) {
		String filePartIndex = request.getParameter(FILE_PART_INDEX);
		if (filePartIndex != null && request.getRequestURI().matches(FILE_PART_UPLOAD_URI_MATCHER)
				&& Integer.parseInt(filePartIndex) > 0) {
			logger.logInfoMessage(this.getClass(), null, null,
					this.getClass().getSimpleName() + ".isFirstFilePartUpload",
					"file upload: not first part, skipping user auth check");
			return false;
		}
		return true;
	}

	private boolean isAllowed(JSONArray userGroups) throws JSONException {
		for (int i = 0; i < userGroups.length(); i++) {
			String group = userGroups.getString(i);
			if (allowedGroups.contains(group)) {
				return true;
			}
		}
		return false;
	}

	private void handleError(String errorMessage, HttpStatus status, HttpServletRequest request,
			HttpServletResponse response) {
		logger.logErrorMessage(this.getClass(), null, errorMessage, request);
		response.setStatus(status.value());

	}

	private boolean hasExistingSession(User user, String shibId, Cookie[] cookies, HttpServletRequest request) {
		HttpSession existingSession = request.getSession(false);
		if (existingSession != null) {
			logger.logInfoMessage(this.getClass(), user, null, request.getRequestURI(),
					"checking for existing session");
			if (existingSession.getAttribute("shibid") != null
					&& existingSession.getAttribute("shibid").equals(user.getShibId())) {
				logger.logWarnMessage(this.getClass(), user, null, request.getRequestURI(),
						"skipping filter, active session");
				return true;
			} else {
				return false;
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
