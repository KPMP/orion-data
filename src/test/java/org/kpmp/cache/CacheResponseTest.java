package org.kpmp.cache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class CacheResponseTest {

    private CacheResponse cacheResponse;

    @Before
    public void setUp() throws Exception {
        cacheResponse = new CacheResponse();
    }

    @After
    public void tearDown() throws Exception {
        cacheResponse = null;
    }

    @Test
    public void testSetCacheNames() {
        Collection<String> cacheNames = (Arrays.asList("cacheName1", "cacheName2"));
        cacheResponse.setCacheNames(cacheNames);
        assertEquals(cacheNames, cacheResponse.getCacheNames());
    }

    @Test
    public void testSetMessage() {
        cacheResponse.setMessage("message");
        assertEquals("message", cacheResponse.getMessage());
    }
}
