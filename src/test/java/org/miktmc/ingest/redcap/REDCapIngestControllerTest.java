package org.miktmc.ingest.redcap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.apiTokens.Token;
import org.miktmc.apiTokens.TokenService;
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
	private AutoCloseable mocks;

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		controller = new REDCapIngestController(service, tokenService);
	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
		controller = null;
	}

	@Test
	public void testIngestREDCapDataGoodToken() throws JSONException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		String tokenString = "ABCD";
		Token token = new Token();
		token.setTokenString(tokenString);
		token.setShibId("shibId");
		when(tokenService.checkAndValidate("ABCD")).thenReturn(true);
		when(tokenService.getTokenByTokenString("ABCD")).thenReturn(token);
		controller.ingestREDCapData("json dump", tokenString, request);
		ArgumentCaptor<String> dumpCaptor = ArgumentCaptor.forClass(String.class);
		verify(service).saveDataDump(dumpCaptor.capture());
		assertEquals("json dump", dumpCaptor.getValue());
	}

	@SuppressWarnings("rawtypes")
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
