package org.miktmc.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.logging.LoggingService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserControllerTest {

	@Mock
	private UserService userService;
	private UserController controller;
	@Mock
	private LoggingService logger;
	private AutoCloseable mocks;

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		controller = new UserController(userService, logger);
	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
		controller = null;
	}

	@Test
	public void testGetUsers_getAll() {
		List<User> users = Arrays.asList(mock(User.class));
		when(userService.findAll()).thenReturn(users);
		HttpServletRequest request = mock(HttpServletRequest.class);

		List<User> result = controller.getUsers("false", request);

		assertEquals(users, result);
		verify(logger).logInfoMessage(UserController.class, null, "Getting all users", request);
	}

	@Test
	public void testGetUsers_getUsersWithPackages() {
		List<User> users = Arrays.asList(mock(User.class));
		when(userService.findAllWithPackages()).thenReturn(users);
		HttpServletRequest request = mock(HttpServletRequest.class);

		List<User> result = controller.getUsers("true", request);

		assertEquals(users, result);
		verify(logger).logInfoMessage(UserController.class, null, "Getting users with packages", request);
	}

}
