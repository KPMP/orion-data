package org.miktmc.apiTokens;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenTest {

    private Token token;

    @BeforeEach
    public void setUp() throws Exception {
        token = new Token();
    }

    @AfterEach
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
