package org.kpmp.ingest.redcap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class REDCapIngestControllerTest {

	@Mock
	private REDCapIngestService service;
	private REDCapIngestController controller;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new REDCapIngestController(service);
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testIngestREDCapData() throws JSONException {
		HttpServletRequest request = mock(HttpServletRequest.class);

		controller.ingestREDCapData("json dump", request);

		ArgumentCaptor<String> dumpCaptor = ArgumentCaptor.forClass(String.class);
		verify(service).saveDataDump(dumpCaptor.capture());
		assertEquals("json dump", dumpCaptor.getValue());
	}

	@Test
	public void testIngestREDCapData_throwsException() throws Exception { // eslint-disable-line no-eval
		HttpServletRequest request = mock(HttpServletRequest.class);
		doThrow(new JSONException("oopsies")).when(service).saveDataDump(any(String.class));

		try {
			controller.ingestREDCapData("json dump", request);
		} catch (JSONException expected) {
			assertEquals("oopsies", expected.getMessage());
		}
	}

}
