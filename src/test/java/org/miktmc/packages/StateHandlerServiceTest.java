package org.miktmc.packages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.logging.LoggingService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

public class StateHandlerServiceTest {

	private StateHandlerService service;
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private LoggingService logger;
	private AutoCloseable mocks;

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		service = new StateHandlerService(restTemplate, logger);
		ReflectionTestUtils.setField(service, "stateServiceHost", "state.hostname");
		ReflectionTestUtils.setField(service, "stateServiceEndpoint", "/uri/to/state/endpoint");
	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
		service = null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetState() throws Exception {
		State newState = mock(State.class);
		when(newState.getPackageId()).thenReturn("1");
		ResponseEntity response = mock(ResponseEntity.class);
		when(response.getBody()).thenReturn(Arrays.asList(newState));
		when(restTemplate.exchange("state.hostname/uri/to/state/endpoint", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<State>>() {
				})).thenReturn(response);

		Map<String, State> stateMap = service.getState();

		assertEquals(1, stateMap.size());
		assertEquals(newState, stateMap.get("1"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSendStateChange_whenSuccess() throws Exception {
		when(restTemplate.postForObject(any(String.class), any(State.class), any(Class.class))).thenReturn("newId");

		service.sendStateChange("packageId", "stateString", null,"codicil", "host.name");

		ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<State> stateCaptor = ArgumentCaptor.forClass(State.class);
		ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
		verify(restTemplate).postForObject(uriCaptor.capture(), stateCaptor.capture(), classCaptor.capture());
		assertEquals("state.hostname/uri/to/state/endpoint/host/host_name", uriCaptor.getValue());
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

		service.sendStateChange("packageId", "stateString", null,"codicil", "host.name.namey");

		ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<State> stateCaptor = ArgumentCaptor.forClass(State.class);
		ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
		verify(restTemplate).postForObject(uriCaptor.capture(), stateCaptor.capture(), classCaptor.capture());
		assertEquals("state.hostname/uri/to/state/endpoint/host/host_name_namey", uriCaptor.getValue());
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
