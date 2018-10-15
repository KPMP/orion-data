package org.kpmp.shibboleth;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.users.User;
import org.kpmp.users.UserRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ShibbolethUserServiceTest {

    private ShibbolethUserService shibbolethUserService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UTF8Encoder utf8Encoder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shibbolethUserService = new ShibbolethUserService(userRepository);
    }

    @After
    public void tearDown() throws Exception {
        shibbolethUserService = null;
    }

    @Test
    public void testGetUserItExists() throws UnsupportedEncodingException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("mail")).thenReturn("maninblack@jcash.com");
        User testUser = new User();
        testUser.setEmail("maninblack@jcash.com");
        when(utf8Encoder.convertFromLatin1("maninblack@jcash.com")).thenReturn("maninblack@jcash.com");
        when(userRepository.findByEmail("maninblack@jcash.com")).thenReturn(testUser);
        assertEquals(testUser, shibbolethUserService.getUser(request, utf8Encoder));
    }

    @Test
    public void testGetUserItDoesntExist() throws UnsupportedEncodingException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("mail")).thenReturn("maninblack@jcash.com");
        when(utf8Encoder.convertFromLatin1("maninblack@jcash.com")).thenReturn("maninblack@jcash.com");
        when(userRepository.findByEmail("maninblack@jcash.com")).thenReturn(null);
        assertEquals("maninblack@jcash.com", shibbolethUserService.getUser(request, utf8Encoder).getEmail());
    }

}
