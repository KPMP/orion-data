package org.miktmc.packages;

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

public class PackageTypeIconControllerTest {

	@Mock
	private PackageTypeIconRepository packageTypeIconRepository;
	@Mock
	private LoggingService logger;
	private PackageTypeIconController controller;
	private AutoCloseable mocks;

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		controller = new PackageTypeIconController(packageTypeIconRepository, logger);
	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
		controller = null;
	}

	@Test
	public void testGetAllPackageTypeIcons() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		List<PackageTypeIcon> expectedList = Arrays.asList(mock(PackageTypeIcon.class));
		when(packageTypeIconRepository.findAll()).thenReturn(expectedList);

		List<PackageTypeIcon> packageTypeIcons = controller.getAllPackageTypeIcons(request);

		assertEquals(expectedList, packageTypeIcons);
		verify(logger).logInfoMessage(PackageTypeIconController.class, null, "Getting list of package type icons",
				request);

	}

}
