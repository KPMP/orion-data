package org.miktmc.apiTokens;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.shibboleth.ShibbolethUserService;
import org.miktmc.users.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TokenControllerTest {

    @Mock
    private ShibbolethUserService userService;
    @Mock
    private TokenService tokenService;
    private TokenController tokenController;
    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        tokenController = new TokenController(userService, tokenService);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
        tokenController = null;
    }

    @Test
    public void testGetToken() {
        TokenResponse tokenResponse = new TokenResponse();
        HttpServletRequest request = mock(HttpServletRequest.class);
        Token token = new Token();
        User user = new User();
        user.setShibId("shibId");
        when(userService.getUser(request)).thenReturn(user);
        tokenResponse.setToken(token);
        tokenResponse.setMessage("This is the message");
        when(tokenService.getOrSetToken("shibId")).thenReturn(token);
        assertEquals(token, tokenController.getToken(request).getToken());
    }

}
