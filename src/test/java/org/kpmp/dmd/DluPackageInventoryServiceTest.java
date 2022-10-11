package org.kpmp.dmd;

import org.apache.tomcat.jni.Time;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.kpmp.logging.LoggingService;
import org.kpmp.packages.Package;
import org.kpmp.packages.PackageService;
import org.kpmp.users.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DluPackageInventoryServiceTest {

    private DluPackageInventoryService dluPackageInventoryService;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private LoggingService logger;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dluPackageInventoryService = new DluPackageInventoryService(restTemplate, logger);
    }

    @After
    public void tearDown() throws Exception {
        dluPackageInventoryService = null;
    }

    @Test
    void getDluPackageInventoryFromPackage() {
        Date now = new Date();
        User user = new User();
        user.setDisplayName("name");
        Package myPackage = new Package();
        myPackage.setPackageId("123");
        myPackage.setPackageType("type");
        myPackage.setTisName("tis");
        myPackage.setCreatedAt(now);
        myPackage.setSubmitter(user);
        myPackage.setSubjectId("subjid");
        DluPackageInventory dluPackageInventory = dluPackageInventoryService.getDluPackageInventoryFromPackage(myPackage);
        assertEquals("123", dluPackageInventory.getDluPackageId());
        assertEquals("name", dluPackageInventory.getDluSubmitter());
        assertEquals("type", dluPackageInventory.getDluPackageType());
        assertEquals("tis", dluPackageInventory.getDluTis());
        assertEquals(user.getDisplayName(), dluPackageInventory.getDluSubmitter());
        assertEquals("subjid", dluPackageInventory.getDluSubjectId());
    }
}