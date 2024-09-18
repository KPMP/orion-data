package org.miktmc.Notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.users.User;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

public class NotificationHandlerTest {
    
    @Mock
    private RestTemplate restTemplate;
    private NotificationHandler handler;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        handler = new NotificationHandler(restTemplate);
        ReflectionTestUtils.setField(handler, "notificationServiceHost", "host");
        ReflectionTestUtils.setField(handler, "notificationEndpoint", "/endpoint");
    }

    @AfterEach
    public void tearDown() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        handler = null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testSendNotfication() {
        User user = new User();
        user.setShibId("shibId");

        when(restTemplate.postForObject(any(String.class), any(NotificationEvent.class), any(Class.class)))
				.thenReturn(true);
        
        handler.sendNotification(user.getShibId(), "origin");

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        ArgumentCaptor<Class> returnCaptor = ArgumentCaptor.forClass(Class.class);
        verify(restTemplate).postForObject(urlCaptor.capture(), eventCaptor.capture(), returnCaptor.capture());

        assertEquals("host/endpoint", urlCaptor.getValue());
        NotificationEvent notificationEvent = eventCaptor.getValue();
        assertEquals("shibId", notificationEvent.getUserId());
        assertEquals("origin", notificationEvent.getOrigin());
        assertEquals(Boolean.class, returnCaptor.getValue());
    }
}
