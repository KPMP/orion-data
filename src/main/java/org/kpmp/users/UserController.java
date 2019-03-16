package org.kpmp.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

	private UserRepository userRepository;
	private UserService userService;

	@Autowired
	public UserController(UserRepository userRepository, UserService userService) {
		this.userRepository = userRepository;
		this.userService = userService;
	}

	@RequestMapping(value = "/v1/users", method = RequestMethod.GET)
	public @ResponseBody List<User> getUsers(@RequestParam(value = "hasPackage", defaultValue = "false") String hasPackage) {
		List<User> users;
		if (hasPackage.equals("true")) {
			users = userService.findAllWithPackages();
		} else {
			users = userRepository.findAll();
		}
		return users;
	}

}
