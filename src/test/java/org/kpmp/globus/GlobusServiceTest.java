package org.kpmp.globus;

import com.google.api.client.http.HttpTransport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class GlobusServiceTest {

    @Mock
    private HttpTransport httpTransport;
    @Mock
    private GlobusAuthService globusAuthService;
    @Mock
    private Environment env;

    private GlobusService globusService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        globusService = new GlobusService(httpTransport, globusAuthService, env);
        when(env.getProperty("GLOBUS_DIR")).thenReturn("thePathToEnlightenment");
        ReflectionTestUtils.setField(globusService, "endpointID", "epID");
        ReflectionTestUtils.setField(globusService, "fileManagerUrl", "http://filemanager");

    }

    @After
    public void tearDown() throws Exception {
        globusService = null;
    }

    @Test
    public void testGetFileManagerUrl() {
        String path = "thePathToEnlightenment";
        assertEquals("http://filemanager?origin_id=epID&origin_path=thePathToEnlightenment", globusService.getFileManagerUrl(path));
    }
}
