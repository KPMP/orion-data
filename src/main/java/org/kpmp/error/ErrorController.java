package org.kpmp.error;

import javax.servlet.http.HttpServletRequest;

import org.kpmp.JWTHandler;
import org.kpmp.logging.LoggingService;
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

	private JWTHandler jwtHandler;
	private LoggingService logger;

	@Autowired
	public ErrorController(JWTHandler jwtHandler, LoggingService logger) {
		this.jwtHandler = jwtHandler;
		this.logger = logger;
	}

	@RequestMapping(value = "/v1/error", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ResponseEntity<Boolean> logError(@RequestBody FrontEndError errorMessage,
			HttpServletRequest request) {
		String userId = jwtHandler.getUserIdFromHeader(request);

		logger.logErrorMessage(this.getClass(), userId, null, request.getRequestURI(),
				errorMessage.getError() + " with stacktrace: " + errorMessage.getStackTrace());

		return new ResponseEntity<>(true, HttpStatus.OK);
	}

}