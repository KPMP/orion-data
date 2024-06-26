package org.kpmp.dmd;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.kpmp.logging.LoggingService;
import org.kpmp.packages.Attachment;
import org.kpmp.packages.Package;
import org.kpmp.users.User;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DmdServiceTest {

    private DmdService dmdService;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private LoggingService logger;

    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        dmdService = new DmdService(restTemplate, logger);
        ReflectionTestUtils.setField(dmdService, "dataManagerHost", "dmd.hostname");
        ReflectionTestUtils.setField(dmdService, "dataManagerEndpoint", "/uri/to/dmd/endpoint");
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
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

    @Test
    public void testMoveFiles() throws JsonProcessingException {
        HashMap<String, String> payload = new HashMap<>();
        when(restTemplate.postForObject("dmd.hostname" + "/uri/to/dmd/endpoint/package/123/move", payload, String.class))
                .thenReturn("{\"success\": true, \"message\":\"message\", \"file_list\":[{\"name\":\"file name\", \"size\": 123, \"path\":\"file path\", \"checksum\": \"checksum val\", \"file_id\": \"1234\"}]}");
        DmdResponse response = dmdService.moveFiles("123");
        verify(restTemplate).postForObject("dmd.hostname" + "/uri/to/dmd/endpoint/package/123/move", payload, String.class);
        assertEquals("message", response.getMessage());
        assertTrue(response.isSuccess());
        assertEquals("file name", response.getFileNameList().get(0));
    }
}