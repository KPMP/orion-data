package org.miktmc.apiTokens;

import jakarta.servlet.http.HttpServletRequest;

import org.miktmc.shibboleth.ShibbolethUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TokenController {

	private ShibbolethUserService userService;
	private TokenService tokenService;

	@Autowired
	public TokenController(ShibbolethUserService userService, TokenService tokenService) {
		this.userService = userService;
		this.tokenService = tokenService;
	}

	@RequestMapping(value = "/v1/token", method = RequestMethod.GET)
	public @ResponseBody TokenResponse getToken(HttpServletRequest request) {
		String shibId = userService.getUser(request).getShibId();
		Token token = tokenService.getOrSetToken(shibId);
		TokenResponse tokenResponse = new TokenResponse();
		if (!tokenService.checkToken(token)) {
			tokenResponse.setMessage("Your token is inactive or expired. Please contact KPMP DLU support.");
		} else {
			tokenResponse.setMessage("Success!");
		}
		tokenResponse.setToken(token);
		return tokenResponse;
	}

}
