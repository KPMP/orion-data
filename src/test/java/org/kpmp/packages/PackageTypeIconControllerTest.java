package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.logging.LoggingService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PackageTypeIconControllerTest {

	@Mock
	private PackageTypeIconRepository packageTypeIconRepository;
	@Mock
	private LoggingService logger;
	private PackageTypeIconController controller;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new PackageTypeIconController(packageTypeIconRepository, logger);
	}

	@After
	public void tearDown() throws Exception {

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
