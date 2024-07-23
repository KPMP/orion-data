package org.miktmc.ingest.redcap;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class REDCapIngestService {

	private REDCapIngestRepository repository;

	@Autowired
	public REDCapIngestService(REDCapIngestRepository repository) {
		this.repository = repository;
	}

	public void saveDataDump(String dataDumpString) throws JSONException {
		JSONObject dataDump = new JSONObject(dataDumpString);

		repository.saveDump(dataDump);
	}

}
