package org.kpmp.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserControllerTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private UserService userService;
	private UserController controller;

	@BeforeEach
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new UserController(userRepository, userService);
	}

	@AfterEach
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testGetUsers() {
		List<User> users = Arrays.asList(mock(User.class));
		when(userRepository.findAll()).thenReturn(users);
		List<User> result = controller.getUsers("false");
		List<User> result2 = controller.getUsers("true");
		assertEquals(users, result);
		assertEquals(null, result2);
	}

}
