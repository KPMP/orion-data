package org.miktmc.apiTokens;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TokenResponseTest {

    private TokenResponse tokenResponse;

    @Before
    public void setUp() throws Exception {
        tokenResponse = new TokenResponse();
    }

    @After
    public void tearDown() throws Exception {
        tokenResponse = null;
    }

    @Test
    public void testSetMessage() {
        tokenResponse.setMessage("This is a token");
        assertEquals("This is a token", tokenResponse.getMessage());
    }

    @Test
    public void testSetToken() {
        Token token = new Token();
        tokenResponse.setToken(token);
        assertEquals(token, tokenResponse.getToken());
    }

}
