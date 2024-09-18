package org.miktmc.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.packages.Package;
import org.miktmc.packages.PackageRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {

	@Mock
	private PackageRepository packageRepository;
	private UserService userService;
	@Mock
	private UserRepository userRepository;
	private AutoCloseable mocks;

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		userService = new UserService(packageRepository, userRepository);
	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
		userService = null;
	}

	@Test
	public void testFindAllWithPackages() {
		Package package1 = new Package();
		User user1 = new User();
		user1.setId("1");
		package1.setSubmitter(user1);
		List<Package> packageList = new ArrayList<>(Arrays.asList(package1));
		when(packageRepository.findAll()).thenReturn(packageList);
		assertEquals(1, userService.findAllWithPackages().size());
		assertEquals("1", userService.findAllWithPackages().get(0).getId());
	}

	@Test
	public void testFindAll() throws Exception {
		List<User> expectedUsers = Arrays.asList(mock(User.class));
		when(userRepository.findAll()).thenReturn(expectedUsers);

		List<User> actualUsers = userService.findAll();

		assertEquals(expectedUsers, actualUsers);
		verify(userRepository).findAll();
	}
}
