package org.kpmp.releases;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReleaseControllerTest {

    @Mock
    private ReleaseRepository repository;
    private ReleaseController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = new ReleaseController(repository);
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
        assertEquals(expectedRelease, controller.getMetadataRelease());
    }

    @Test
    public void testGetMetadataReleaseByVersion() {
        Release expectedRelease = mock(Release.class);
        when(repository.findByVersion("1.01")).thenReturn(expectedRelease);
        assertEquals(expectedRelease, controller.getMetadataReleaseByVersion("1.01"));
    }

}
