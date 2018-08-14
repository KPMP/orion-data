package org.kpmp.upload.deprecated;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.upload.deprecated.FilePathHelper;
import org.springframework.test.util.ReflectionTestUtils;

public class FilePathHelperTest {

    private FilePathHelper filePathHelper;

    @Before
    public void setUp() throws Exception {
        filePathHelper = new FilePathHelper();
        ReflectionTestUtils.setField(filePathHelper, "metadataFileName", "metadata.json");
        ReflectionTestUtils.setField(filePathHelper, "basePath", File.separator + "data");
    }

    @After
    public void tearDown() throws Exception {
        filePathHelper = null;
    }

    @Test
    public void test_getPackagePath() throws Exception {
        assertEquals(File.separator  + "data" + File.separator  + "package_1234_UUID" + File.separator, filePathHelper.getPackagePath("", "1234_UUID"));
    }

    @Test
    public void test_getMetadataFileName() {
        assertEquals("metadata.json", filePathHelper.getMetadataFileName());

    }
}