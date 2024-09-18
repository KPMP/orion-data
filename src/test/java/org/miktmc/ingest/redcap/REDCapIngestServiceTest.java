package org.miktmc.ingest.redcap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class REDCapIngestServiceTest {

	@Mock
	private REDCapIngestRepository repository;
	private REDCapIngestService service;
	private AutoCloseable mocks;

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		service = new REDCapIngestService(repository);
	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
		service = null;
	}

	@Test
	public void testSaveDataDump() throws JSONException {
		service.saveDataDump("{'stuff': 'value'}");

		ArgumentCaptor<JSONObject> jsonCaptor = ArgumentCaptor.forClass(JSONObject.class);
		verify(repository).saveDump(jsonCaptor.capture());
		JSONObject json = jsonCaptor.getValue();
		assertEquals(1, json.length());
		assertEquals("value", json.get("stuff"));
	}

}
