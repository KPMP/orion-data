package org.kpmp.users;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kpmp.JWTHandler;
import org.kpmp.logging.LoggingService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserControllerTest {

	@Mock
	private UserService userService;
	private UserController controller;
	@Mock
	private JWTHandler jwtHandler;
	@Mock
	private LoggingService logger;

	@BeforeEach
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new UserController(userService, jwtHandler, logger);
	}

	@AfterEach
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testGetUsers_getAll() {
		List<User> users = Arrays.asList(mock(User.class));
		when(userService.findAll()).thenReturn(users);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");
		when(request.getRequestURI()).thenReturn("/v1/users");

		List<User> result = controller.getUsers("false", request);

		assertEquals(users, result);
		verify(logger).logInfoMessage(UserController.class, "userID", null, "/v1/users", "Getting all users");
	}

	@Test
	public void testGetUsers_getUsersWithPackages() {
		List<User> users = Arrays.asList(mock(User.class));
		when(userService.findAllWithPackages()).thenReturn(users);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");
		when(request.getRequestURI()).thenReturn("/v1/users");

		List<User> result = controller.getUsers("true", request);

		assertEquals(users, result);
		verify(logger).logInfoMessage(UserController.class, "userID", null, "/v1/users", "Getting users with packages");
	}

}
