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
	private UserController controller;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new UserController(userRepository);
	}

	@AfterEach
	void tearDown() throws Exception {
		controller = null;
	}

	@Test
	void testGetUsers() {
		List<User> users = Arrays.asList(mock(User.class));
		when(userRepository.findAll()).thenReturn(users);

		List<User> result = controller.getUsers();

		assertEquals(users, result);
	}

}
