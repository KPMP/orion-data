package org.kpmp.logging;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.kpmp.shibboleth.ShibbolethUserService;
import org.kpmp.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {

	private static final String LOG_MESSAGE_FORMAT = "USER: {} | PKGID: {} | URI: {} | MSG: {} ";
	private ShibbolethUserService shibUserService;

	@Autowired
	public LoggingService(ShibbolethUserService shibUserService) {
		this.shibUserService = shibUserService;
	}

	@SuppressWarnings("rawtypes")
	public void logInfoMessage(Class clazz, String packageId, String message, HttpServletRequest request) {
		Logger log = LoggerFactory.getLogger(clazz);
		try {
			User user = shibUserService.getUser(request);
			if (user == null) {
				log.info(LOG_MESSAGE_FORMAT, "Unknown user", packageId, request.getRequestURI(), message);
			} else {
				log.info(LOG_MESSAGE_FORMAT, user.toString(), packageId, request.getRequestURI(), message);
			}
		} catch (UnsupportedEncodingException e) {
			log.error(LOG_MESSAGE_FORMAT, "unknown", packageId, request.getRequestURI(),
					"ERROR: Unable to get user information from request: " + e.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	public void logInfoMessage(Class clazz, User user, String packageId, String uri, String message) {
		Logger log = LoggerFactory.getLogger(clazz);
		if (user == null) {
			log.info(LOG_MESSAGE_FORMAT, "Unknown user", packageId, uri, message);
		} else {
			log.info(LOG_MESSAGE_FORMAT, user.toString(), packageId, uri, message);
		}
	}

	@SuppressWarnings("rawtypes")
	public void logErrorMessage(Class clazz, String packageId, String message, HttpServletRequest request) {
		Logger log = LoggerFactory.getLogger(clazz);
		try {
			User user = shibUserService.getUser(request);
			if (user == null) {
				log.error(LOG_MESSAGE_FORMAT, "Unknown user", packageId, request.getRequestURI(), message);
			} else {
				log.error(LOG_MESSAGE_FORMAT, user.toString(), packageId, request.getRequestURI(), message);
			}
		} catch (UnsupportedEncodingException e) {
			log.error(LOG_MESSAGE_FORMAT, "unknown", packageId, request.getRequestURI(),
					"ERROR: Unable to get user information from request: " + e.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	public void logErrorMessage(Class clazz, User user, String packageId, String uri, String message) {
		Logger log = LoggerFactory.getLogger(clazz);
		if (user == null) {
			log.error(LOG_MESSAGE_FORMAT, "Unknown user", packageId, uri, message);
		} else {
			log.error(LOG_MESSAGE_FORMAT, user.toString(), packageId, uri, message);
		}
	}

	@SuppressWarnings("rawtypes")
	public void logWarnMessage(Class clazz, String packageId, String message, HttpServletRequest request) {
		Logger log = LoggerFactory.getLogger(clazz);
		try {
			User user = shibUserService.getUser(request);
			if (user == null) {
				log.warn(LOG_MESSAGE_FORMAT, "Unknown user", packageId, request.getRequestURI(), message);
			} else {
				log.warn(LOG_MESSAGE_FORMAT, user.toString(), packageId, request.getRequestURI(), message);
			}
		} catch (UnsupportedEncodingException e) {
			log.error(LOG_MESSAGE_FORMAT, "unknown", packageId, request.getRequestURI(),
					"ERROR: Unable to get user information from request: " + e.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	public void logWarnMessage(Class clazz, User user, String packageId, String uri, String message) {
		Logger log = LoggerFactory.getLogger(clazz);
		if (user == null) {
			log.warn(LOG_MESSAGE_FORMAT, "Unknown user", packageId, uri, message);
		} else {
			log.warn(LOG_MESSAGE_FORMAT, user.toString(), packageId, uri, message);
		}
	}

}
