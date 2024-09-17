package org.miktmc.users;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.miktmc.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

	private UserService userService;
	private LoggingService logger;

	@Autowired
	public UserController(UserService userService, LoggingService logger) {
		this.userService = userService;
		this.logger = logger;
	}

	@RequestMapping(value = "/v1/users", method = RequestMethod.GET)
	public @ResponseBody List<User> getUsers(
			@RequestParam(value = "hasPackage", defaultValue = "false") String hasPackage, HttpServletRequest request) {
		List<User> users;
		if (hasPackage.equals("true")) {
			logger.logInfoMessage(this.getClass(), null, "Getting users with packages", request);
			users = userService.findAllWithPackages();
		} else {
			logger.logInfoMessage(this.getClass(), null, "Getting all users", request);
			users = userService.findAll();
		}
		return users;
	}

}
