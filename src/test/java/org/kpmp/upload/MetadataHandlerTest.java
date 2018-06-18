package org.kpmp.upload;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.dao.FileSubmission;
import org.kpmp.dao.UploadPackage;
import org.springframework.test.util.ReflectionTestUtils;

public class MetadataHandlerTest {

    private MetadataHandler metadataHandler;

    @Before
    public void setUp() throws Exception {
        metadataHandler = new MetadataHandler();
        ReflectionTestUtils.setField(metadataHandler, "metadataFileName", "metadata.json");
    }

    @After
    public void tearDown() throws Exception {
        metadataHandler = null;
    }

    @Test
    public void test_getFilePathFromUploadPackage() throws Exception {
        UploadPackage uploadPackage = mock(UploadPackage.class);
        FileSubmission fileSubmission = mock(FileSubmission.class);
        when(fileSubmission.getFilename()).thenReturn("filename");
        when(fileSubmission.getFilePath()).thenReturn("/data/package1/filename");
        List<FileSubmission> fileSubmissions = Arrays.asList(fileSubmission);
        when(uploadPackage.getFileSubmissions()).thenReturn(fileSubmissions);

        String actual = metadataHandler.getFilePathFromUploadPackage(uploadPackage);

        assertEquals("/data/package1/metadata.json", actual);
    }
}
