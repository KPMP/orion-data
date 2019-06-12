package org.kpmp.error;

import javax.servlet.http.HttpServletRequest;

import org.kpmp.ApplicationConstants;
import org.kpmp.JWTHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private JWTHandler jwtHandler;

	@Autowired
	public ErrorController(JWTHandler jwtHandler) {
		this.jwtHandler = jwtHandler;
	}

	@RequestMapping(value = "/v1/error", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ResponseEntity<Boolean> logError(@RequestBody FrontEndError errorMessage,
			HttpServletRequest request) {
		String userId = jwtHandler.getUserIdFromHeader(request);

		log.error(ApplicationConstants.LOG_MESSAGE_FORMAT, userId, null, request.getRequestURI(),
				errorMessage.getError() + " with stacktrace: " + errorMessage.getStackTrace());

		return new ResponseEntity<>(true, HttpStatus.OK);
	}

}