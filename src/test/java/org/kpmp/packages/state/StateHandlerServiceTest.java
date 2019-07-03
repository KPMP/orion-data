package org.kpmp.packages.state;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.logging.LoggingService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

public class StateHandlerServiceTest {

	private StateHandlerService service;
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private LoggingService logger;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new StateHandlerService(restTemplate, logger);
		ReflectionTestUtils.setField(service, "notificationServiceHost", "hostname");
		ReflectionTestUtils.setField(service, "notificationEndpoint", "/uri/to/endpoint");
	}

	@After
	public void tearDown() throws Exception {
		service = null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSendNotification() {
		Date datePackageSubmitted = new Date();
		when(restTemplate.postForObject(any(String.class), any(PackageNotificationInfo.class), any(Class.class)))
				.thenReturn(true);

		service.sendNotification("packageId", "packageType", datePackageSubmitted, "submitterFirstName",
				"submitterLastName", "specimenId");

		ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<PackageNotificationInfo> packageInfoCaptor = ArgumentCaptor
				.forClass(PackageNotificationInfo.class);
		ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
		verify(restTemplate).postForObject(uriCaptor.capture(), packageInfoCaptor.capture(), classCaptor.capture());
		assertEquals("hostname/uri/to/endpoint", uriCaptor.getValue());
		PackageNotificationInfo packageInfo = packageInfoCaptor.getValue();
		assertEquals("packageId", packageInfo.getPackageId());
		assertEquals("packageType", packageInfo.getPackageType());
		assertEquals(datePackageSubmitted, packageInfo.getDatePackageSubmitted());
		assertEquals("submitterFirstName submitterLastName", packageInfo.getSubmitter());
		assertEquals("specimenId", packageInfo.getSpecimenId());
		assertEquals(Boolean.class, classCaptor.getValue());
		verify(logger, times(0)).logErrorMessage(any(Class.class), any(String.class), any(String.class),
				any(String.class), any(String.class));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSendNotification_whenMessageFails() {
		Date datePackageSubmitted = new Date();
		when(restTemplate.postForObject(any(String.class), any(PackageNotificationInfo.class), any(Class.class)))
				.thenReturn(false);

		service.sendNotification("packageId", "packageType", datePackageSubmitted, "submitterFirstName",
				"submitterLastName", "specimenId");

		ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<PackageNotificationInfo> packageInfoCaptor = ArgumentCaptor
				.forClass(PackageNotificationInfo.class);
		ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
		verify(restTemplate).postForObject(uriCaptor.capture(), packageInfoCaptor.capture(), classCaptor.capture());
		assertEquals("hostname/uri/to/endpoint", uriCaptor.getValue());
		PackageNotificationInfo packageInfo = packageInfoCaptor.getValue();
		assertEquals("packageId", packageInfo.getPackageId());
		assertEquals("packageType", packageInfo.getPackageType());
		assertEquals(datePackageSubmitted, packageInfo.getDatePackageSubmitted());
		assertEquals("submitterFirstName submitterLastName", packageInfo.getSubmitter());
		assertEquals("specimenId", packageInfo.getSpecimenId());
		assertEquals(Boolean.class, classCaptor.getValue());
		verify(logger, times(1)).logErrorMessage(StateHandlerService.class, null, "packageId",
				"StateHandlerService.sendNotification", "Notification message failed to send.");
	}

}
