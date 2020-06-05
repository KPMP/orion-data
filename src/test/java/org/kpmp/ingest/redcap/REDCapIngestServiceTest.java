package org.kpmp.ingest.redcap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class REDCapIngestServiceTest {

	@Mock
	private REDCapIngestRepository repository;
	private REDCapIngestService service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new REDCapIngestService(repository);
	}

	@After
	public void tearDown() throws Exception {
		service = null;
	}

	@Test
	public void testSaveDataDump() throws JSONException {
		service.saveDataDump("{}");

		ArgumentCaptor<JSONObject> jsonCaptor = ArgumentCaptor.forClass(JSONObject.class);
		verify(repository).saveDump(jsonCaptor.capture());
		assertEquals(true, jsonCaptor.getValue().has("created_at"));
	}

}
