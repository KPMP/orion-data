package org.miktmc.error;

import jakarta.servlet.http.HttpServletRequest;

import org.miktmc.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ErrorController {

	private LoggingService logger;

	@Autowired
	public ErrorController(LoggingService logger) {
		this.logger = logger;
	}

	@RequestMapping(value = "/v1/error", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ResponseEntity<Boolean> logError(@RequestBody FrontEndError errorMessage,
			HttpServletRequest request) {

		logger.logErrorMessage(this.getClass(), null,
				errorMessage.getError() + " with stacktrace: " + errorMessage.getStackTrace(), request);

		return new ResponseEntity<>(true, HttpStatus.OK);
	}

}