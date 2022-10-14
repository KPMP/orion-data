package org.kpmp.dmd;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.logging.LoggingService;
import org.kpmp.packages.Attachment;
import org.kpmp.packages.Package;
import org.kpmp.users.User;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DmdServiceTest {

    private DmdService dmdService;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private LoggingService logger;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dmdService = new DmdService(restTemplate, logger);
        ReflectionTestUtils.setField(dmdService, "dataManagerHost", "dmd.hostname");
        ReflectionTestUtils.setField(dmdService, "dataManagerEndpoint", "/uri/to/dmd/endpoint");
    }

    @After
    public void tearDown() throws Exception {
        dmdService = null;
    }

    @Test
    public void testSendPackageFiles() {
        Package myPackage = new Package();
        myPackage.setPackageId("pid");
        Attachment attachment1 = new Attachment();
        attachment1.setId("file1");
        Attachment attachment2 = new Attachment();
        attachment2.setId("file2");
        ArrayList<Attachment> attachmentList = new ArrayList<Attachment>();
        attachmentList.add(attachment1);
        attachmentList.add(attachment2);
        myPackage.setAttachments(attachmentList);
        List<String> fileIds = dmdService.sendPackageFiles(myPackage);
        assertEquals("file1", fileIds.get(0));
        assertEquals("file2", fileIds.get(1));
    }

    @Test
    public void testConvertAndSendPackage() {
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
        String packageId = dmdService.convertAndSendNewPackage(myPackage);
        assertEquals("123", packageId);
    }

    @Test
    public void testSendNewFile() {
        DluFile file = new DluFile();
        dmdService.sendNewFile(file);
        verify(restTemplate).postForObject("dmd.hostname" + "/uri/to/dmd/endpoint/file", file, String.class);
    }

    @Test
    public void testSendNewPackage() {
        DluPackageInventory dluPackageInventory = new DluPackageInventory();
        dmdService.sendNewPackage(dluPackageInventory);
        verify(restTemplate).postForObject("dmd.hostname" + "/uri/to/dmd/endpoint/package", dluPackageInventory, String.class);
    }
}