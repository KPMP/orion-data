package org.kpmp.Notification;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

public class NotificationHandlerTest {
    
    @Mock
    private RestTemplate restTemplate;
    private NotificationHandler handler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        handler = new NotificationHandler(restTemplate);
        ReflectionTestUtils.setField(handler, "notificationServiceHost", "host");
        ReflectionTestUtils.setField(handler, "notificationEndpoint", "/endpoint");
    }

    @After
    public void tearDown() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        handler = null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testSendNotfication() {
        when(restTemplate.postForObject(any(String.class), any(NotificationEvent.class), any(Class.class)))
				.thenReturn(true);
        
        handler.sendNotification("user", "origin");

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        ArgumentCaptor<Class> returnCaptor = ArgumentCaptor.forClass(Class.class);
        verify(restTemplate).postForObject(urlCaptor.capture(), eventCaptor.capture(), returnCaptor.capture());

        assertEquals("host/endpoint", urlCaptor.getValue());
        NotificationEvent notificationEvent = eventCaptor.getValue();
        assertEquals("user", notificationEvent.getUserId());
        assertEquals("origin", notificationEvent.getOrigin());
        assertEquals(Boolean.class, returnCaptor.getValue());
    }
}
