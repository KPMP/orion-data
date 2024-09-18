package org.miktmc.ingest.redcap;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import jakarta.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.miktmc.apiTokens.Token;
import org.miktmc.apiTokens.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class REDCapIngestController {

	private static Logger log = LoggerFactory.getLogger(REDCapIngestController.class);
	private static final String LOG_MESSAGE_FORMAT = "URI: {} | MSG: {} ";
	private REDCapIngestService service;
	private TokenService tokenService;

	@Autowired
	public REDCapIngestController(REDCapIngestService service, TokenService tokenService) {
		this.service = service;
		this.tokenService = tokenService;

	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/v1/redcap", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity ingestREDCapData(@RequestBody String dataDump,
			@RequestParam("token") String tokenString, HttpServletRequest request) throws JSONException {
		log.info(LOG_MESSAGE_FORMAT, request.getRequestURI(), "Receiving new REDCap data dump");
		ResponseEntity responseEntity;
		if (tokenService.checkAndValidate(tokenString)) {
			Token token = tokenService.getTokenByTokenString(tokenString);
			try {
				service.saveDataDump(dataDump);
				log.info(LOG_MESSAGE_FORMAT, request.getRequestURI(), "Received new REDCap data dump from shibId "
						+ token.getShibId() + " using token " + token.getTokenString());
				responseEntity = ResponseEntity.ok().body("Successfully ingested REDCap data");
			} catch (JSONException error) {
				log.error(LOG_MESSAGE_FORMAT, request.getRequestURI(), error.getMessage());
				throw error;
			}
		} else {
			log.error(LOG_MESSAGE_FORMAT, request.getRequestURI(), "Invalid token provided: " + tokenString);
			responseEntity = ResponseEntity.status(UNAUTHORIZED).body("Invalid token.");
		}
		return responseEntity;
	}

}
