package org.miktmc.apiTokens;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class TokenTest {

    private Token token;

    @Before
    public void setUp() throws Exception {
        token = new Token();
    }

    @After
    public void tearDown() throws Exception {
        token = null;
    }

    @Test
    public void testSetTokenString() {
        token.setTokenString("Token string");
        assertEquals("Token string", token.getTokenString());
    }

    @Test
    public void testSetShibId() {
        token.setShibId("shibby");
        assertEquals("shibby", token.getShibId());
    }

    @Test
    public void testSetExpiration() {
        Date date = new Date();
        token.setExpiration(date);
        assertEquals(date, token.getExpiration());
    }

    @Test
    public void setActive() {
        token.setActive(false);
        assertEquals(false, token.getActive());
    }

}
