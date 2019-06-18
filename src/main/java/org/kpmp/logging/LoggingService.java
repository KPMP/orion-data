package org.kpmp.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {

	private static final String LOG_MESSAGE_FORMAT = "USERID: {} | PKGID: {} | URI: {} | MSG: {} ";

	@SuppressWarnings("rawtypes")
	public void logInfoMessage(Class clazz, String userId, String packageId, String uri, String message) {
		Logger log = LoggerFactory.getLogger(clazz);
		log.info(LOG_MESSAGE_FORMAT, userId, packageId, uri, message);
	}

	@SuppressWarnings("rawtypes")
	public void logErrorMessage(Class clazz, String userId, String packageId, String uri, String message) {
		Logger log = LoggerFactory.getLogger(clazz);
		log.error(LOG_MESSAGE_FORMAT, userId, packageId, uri, message);
	}

	@SuppressWarnings("rawtypes")
	public void logWarnMessage(Class clazz, String userId, String packageId, String uri, String message) {
		Logger log = LoggerFactory.getLogger(clazz);
		log.warn(LOG_MESSAGE_FORMAT, userId, packageId, uri, message);
	}

}
