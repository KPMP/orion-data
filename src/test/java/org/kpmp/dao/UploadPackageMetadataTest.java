package org.kpmp.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.JsonProcessingException;

public class UploadPackageMetadataTest {

    UploadPackageMetadata uploadPackageMetadata;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UploadPackage uploadPackage = mock(UploadPackage.class);
        when(uploadPackage.getPackageType()).thenReturn(mock(PackageType.class));
        when(uploadPackage.getExperimentId()).thenReturn("2");
        uploadPackageMetadata = new UploadPackageMetadata(uploadPackage);
    }

    @After
    public void tearDown() throws Exception {
        uploadPackageMetadata = null;
    }

    @Test
    public void testGenerateJSON() throws JsonProcessingException {
        String actual = uploadPackageMetadata.generateJSON();
        assertEquals("foo", actual);
    }


}
