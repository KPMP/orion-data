package org.kpmp.Notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public
class NotificationHandler {

	@Value("${notification.service.host}")
	private String notificationServiceHost;
	@Value("${notification.endpoint}")
	private String notificationEndpoint;
	private final RestTemplate restTemplate;

	public NotificationHandler(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void sendNotification(String userId, String origin) {

		restTemplate.postForObject(notificationServiceHost + notificationEndpoint,
				new NotificationEvent(userId, origin), Boolean.class);

	}

}

