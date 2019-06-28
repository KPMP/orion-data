package org.kpmp.releases;

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

public class ReleaseControllerTest {

	@Mock
	private ReleaseRepository repository;
	private ReleaseController controller;
	@Mock
	private LoggingService logger;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		controller = new ReleaseController(repository, logger);
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
		repository = null;
	}

	@Test
	public void testGetMetadataRelease() {
		Release expectedRelease = mock(Release.class);
		when(repository.findAll()).thenReturn(Arrays.asList(expectedRelease));
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/v1/releases");

		List<Release> releaseInfo = controller.getMetadataRelease(request);

		assertEquals(Arrays.asList(expectedRelease), releaseInfo);
		verify(logger).logInfoMessage(ReleaseController.class, null, null, "/v1/releases",
				"Getting all release information");
	}

	@Test
	public void testGetMetadataReleaseByVersion() {
		Release expectedRelease = mock(Release.class);
		when(repository.findByVersion("1.01")).thenReturn(expectedRelease);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/v1/releases/version/1.01");

		Release releaseInfo = controller.getMetadataReleaseByVersion("1.01", request);

		assertEquals(expectedRelease, releaseInfo);
		verify(logger).logInfoMessage(ReleaseController.class, null, null, "/v1/releases/version/1.01",
				"Getting release information for version 1.01");
	}

}
