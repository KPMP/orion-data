package org.kpmp.apiTokens;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TokenResponseTest {

    private TokenResponse tokenResponse;

    @BeforeEach
    public void setUp() throws Exception {
        tokenResponse = new TokenResponse();
    }

    @AfterEach
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
