package org.kpmp.packages.state;

import java.util.Date;

import org.kpmp.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StateHandlerService {

	@Value("${notification.service.host}")
	private String notificationServiceHost;
	@Value("${notification.endpoint}")
	private String notificationEndpoint;
	@Value("${state.service.host}")
	private String stateServiceHost;
	@Value("${state.service.endpoint}")
	private String stateServiceEndpoint;
	private RestTemplate restTemplate;
	private LoggingService logger;

	@Autowired
	public StateHandlerService(RestTemplate restTemplate, LoggingService logger) {
		this.restTemplate = restTemplate;
		this.logger = logger;
	}

	public void sendStateChange(String packageId, String state, String codicil) {

	}

	public void sendNotification(String packageId, String packageType, Date datePackageSubmitted,
			String submitterFirstName, String submitterLastName, String specimenId, String origin) {

		String submitterName = submitterFirstName + " " + submitterLastName;
		PackageNotificationInfo packageNotification = new PackageNotificationInfo(packageId, packageType,
				datePackageSubmitted, submitterName, specimenId, origin);

		Boolean response = restTemplate.postForObject(notificationServiceHost + notificationEndpoint,
				packageNotification, Boolean.class);

		if (!response) {
			logger.logErrorMessage(this.getClass(), null, packageId,
					this.getClass().getSimpleName() + ".sendNotification", "Notification message failed to send.");
		}
	}

}
