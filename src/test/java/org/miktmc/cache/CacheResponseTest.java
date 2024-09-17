package org.miktmc.cache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CacheResponseTest {

    private CacheResponse cacheResponse;

    @BeforeEach
    public void setUp() throws Exception {
        cacheResponse = new CacheResponse();
    }

    @AfterEach
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
