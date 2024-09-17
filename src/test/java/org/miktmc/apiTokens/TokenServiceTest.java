package org.miktmc.apiTokens;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.users.User;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Date;

public class TokenServiceTest {

    private TokenService tokenService;
    @Mock
    private TokenRepository tokenRepository;
    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        tokenService = new TokenService(tokenRepository);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
        tokenService = null;
    }

    @Test
    public void testCheckAndValidateGoodTokenString() {
        Token token = new Token();
        token.setTokenString("ABCD");
        token.setActive(true);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        Date nextYear = cal.getTime();
        token.setExpiration(nextYear);
        when(tokenRepository.findByTokenString("ABCD")).thenReturn(token);
        assertEquals(true, tokenService.checkAndValidate("ABCD"));
    }

    @Test
    public void testCheckAndValidateBadTokenString() {
        Token token = new Token();
        token.setTokenString("ABCD");
        token.setActive(true);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        Date nextYear = cal.getTime();
        token.setExpiration(nextYear);
        when(tokenRepository.findByTokenString("ABCD")).thenReturn(null);
        assertEquals(false, tokenService.checkAndValidate("ABCD"));
    }

    @Test
    public void testCheckTokenExpired() {
        Token token = new Token();
        token.setTokenString("ABCD");
        token.setActive(true);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        Date lastYear = cal.getTime();
        token.setExpiration(lastYear);
        assertEquals(false, tokenService.checkToken(token));
    }

    @Test
    public void testCheckTokenInactive() {
        Token token = new Token();
        token.setTokenString("ABCD");
        token.setActive(false);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        Date nextYear = cal.getTime();
        token.setExpiration(nextYear);
        assertEquals(false, tokenService.checkToken(token));
    }

    @Test
    public void testGenerateToken() {
        User user = new User();
        user.setShibId("shibId");
        Token token = tokenService.generateToken("shibId");
        assertEquals(true, token.getActive());
        assertEquals("shibId", token.getShibId());
        assertEquals(44, token.getTokenString().length());
    }

    @Test
    public void testGetOrSetTokenExists() {
        Token token = new Token();
        token.setTokenString("ABCD");
        token.setShibId("shibId");
        token.setActive(true);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        when(tokenRepository.findByShibId("shibId")).thenReturn(token);
        assertEquals(token, tokenService.getOrSetToken("shibId"));
    }

    @Test
    public void testGetOrSetTokenDoesntExist() {
        when(tokenRepository.findByShibId("shibId")).thenReturn(null);
        assertEquals("shibId", tokenService.getOrSetToken("shibId").getShibId());
    }

    @Test
    public void testGetTokenByTokenString() {
        Token token = new Token();
        when(tokenRepository.findByTokenString("ABCD")).thenReturn(token);
        assertEquals(token, tokenService.getTokenByTokenString("ABCD"));
    }

}
