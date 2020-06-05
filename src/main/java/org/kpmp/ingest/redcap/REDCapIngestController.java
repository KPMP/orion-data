package org.kpmp.ingest.redcap;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class REDCapIngestController {

	private static Logger log = LoggerFactory.getLogger(REDCapIngestController.class);
	private static final String LOG_MESSAGE_FORMAT = "URI: {} | MSG: {} ";
	private REDCapIngestService service;

	@Autowired
	public REDCapIngestController(REDCapIngestService service) {
		this.service = service;

	}

	@RequestMapping(value = "/v1/redcap", method = RequestMethod.POST)
	public @ResponseBody boolean ingestREDCapData(String dataDump, HttpServletRequest request) throws JSONException {
		log.info(LOG_MESSAGE_FORMAT, request.getRequestURI(), "Receiving new REDCap data dump");
		try {
			service.saveDataDump(dataDump);
		} catch (JSONException error) {
			log.error(LOG_MESSAGE_FORMAT, request.getRequestURI(), error.getMessage());
			throw error;
		}
		return true;
	}

}
