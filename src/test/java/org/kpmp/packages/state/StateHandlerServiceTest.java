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
import org.kpmp.users.User;
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
		ReflectionTestUtils.setField(service, "stateServiceHost", "state.hostname");
		ReflectionTestUtils.setField(service, "stateServiceEndpoint", "/uri/to/state/endpoint");
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
				"submitterLastName", "specimenId", "origin");

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
		assertEquals("origin", packageInfo.getOrigin());
		assertEquals(Boolean.class, classCaptor.getValue());
		verify(logger, times(0)).logErrorMessage(any(Class.class), any(User.class), any(String.class),
				any(String.class), any(String.class));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSendNotification_whenMessageFails() {
		Date datePackageSubmitted = new Date();
		when(restTemplate.postForObject(any(String.class), any(PackageNotificationInfo.class), any(Class.class)))
				.thenReturn(false);

		service.sendNotification("packageId", "packageType", datePackageSubmitted, "submitterFirstName",
				"submitterLastName", "specimenId", "origin");

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
		assertEquals("origin", packageInfo.getOrigin());
		assertEquals(Boolean.class, classCaptor.getValue());
		verify(logger, times(1)).logErrorMessage(StateHandlerService.class, null, "packageId",
				"StateHandlerService.sendNotification", "Notification message failed to send.");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSendStateChange_whenSuccess() throws Exception {
		when(restTemplate.postForObject(any(String.class), any(State.class), any(Class.class))).thenReturn("newId");

		service.sendStateChange("packageId", "stateString", "codicil");

		ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<State> stateCaptor = ArgumentCaptor.forClass(State.class);
		ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
		verify(restTemplate).postForObject(uriCaptor.capture(), stateCaptor.capture(), classCaptor.capture());
		assertEquals("state.hostname/uri/to/state/endpoint", uriCaptor.getValue());
		assertEquals(String.class, classCaptor.getValue());
		State expectedState = stateCaptor.getValue();
		assertEquals("packageId", expectedState.getPackageId());
		assertEquals("stateString", expectedState.getState());
		assertEquals("codicil", expectedState.getCodicil());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSendStateChange_whenFailure() throws Exception {
		when(restTemplate.postForObject(any(String.class), any(State.class), any(Class.class))).thenReturn(null);

		service.sendStateChange("packageId", "stateString", "codicil");

		ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<State> stateCaptor = ArgumentCaptor.forClass(State.class);
		ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
		verify(restTemplate).postForObject(uriCaptor.capture(), stateCaptor.capture(), classCaptor.capture());
		assertEquals("state.hostname/uri/to/state/endpoint", uriCaptor.getValue());
		assertEquals(String.class, classCaptor.getValue());
		State expectedState = stateCaptor.getValue();
		assertEquals("packageId", expectedState.getPackageId());
		assertEquals("stateString", expectedState.getState());
		assertEquals("codicil", expectedState.getCodicil());
		verify(logger, times(1)).logErrorMessage(StateHandlerService.class, null, "packageId",
				"StateHandlerService.sendStateChange",
				"Error saving state change for package id: packageId and state: stateString");
	}

}
