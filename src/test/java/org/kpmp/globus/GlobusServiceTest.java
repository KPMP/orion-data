package org.kpmp.globus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.api.client.http.HttpTransport;

public class GlobusServiceTest {

	@Mock
	private HttpTransport httpTransport;
	@Mock
	private GlobusAuthService globusAuthService;
	@Mock
	private Environment env;

	private GlobusService globusService;
	private AutoCloseable mocks;

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		globusService = new GlobusService(httpTransport, globusAuthService, env);
		when(env.getProperty("GLOBUS_DIR")).thenReturn("thePathToEnlightenment");
		ReflectionTestUtils.setField(globusService, "endpointID", "epID");
		ReflectionTestUtils.setField(globusService, "fileManagerUrl", "http://filemanager");

	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
		globusService = null;
	}

	@Test
	public void testGetFileManagerUrl() {
		String path = "thePathToEnlightenment";
		assertEquals("http://filemanager?origin_id=epID&origin_path=thePathToEnlightenment",
				globusService.getFileManagerUrl(path));
	}

}
