package org.kpmp.ingest.redcap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.apiTokens.TokenService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

public class REDCapIngestControllerTest {

	@Mock
	private REDCapIngestService service;
	private REDCapIngestController controller;
	@Mock
	private TokenService tokenService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new REDCapIngestController(service, tokenService);
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testIngestREDCapDataGoodToken() throws JSONException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		String token = "ABCD";
		when(tokenService.checkAndValidate("ABCD")).thenReturn(true);
		controller.ingestREDCapData("json dump", token, request);
		ArgumentCaptor<String> dumpCaptor = ArgumentCaptor.forClass(String.class);
		verify(service).saveDataDump(dumpCaptor.capture());
		assertEquals("json dump", dumpCaptor.getValue());
	}

	@Test
	public void testIngestREDCapDataBadToken() throws JSONException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		String token = "ABCD";
		when(tokenService.checkAndValidate("ABCD")).thenReturn(false);
		ResponseEntity response = controller.ingestREDCapData("json dump", token, request);
		ArgumentCaptor<String> dumpCaptor = ArgumentCaptor.forClass(String.class);
		verify(service, never()).saveDataDump(dumpCaptor.capture());
		assertEquals(org.springframework.http.HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	public void testIngestREDCapData_throwsException() throws Exception { // eslint-disable-line no-eval
		HttpServletRequest request = mock(HttpServletRequest.class);
		String token = "ABCD";
		doThrow(new JSONException("oopsies")).when(service).saveDataDump(any(String.class));

		try {
			controller.ingestREDCapData("json dump", token, request);
		} catch (JSONException expected) {
			assertEquals("oopsies", expected.getMessage());
		}
	}

}
