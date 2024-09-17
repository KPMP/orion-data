package org.miktmc.Notification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.users.User;

public class NotificationEventTest {
    
    private NotificationEvent notificationEvent;

    @BeforeEach
    public void setUp() throws Exception {
        notificationEvent = new NotificationEvent("shibId", "origin");
    }

    @AfterEach
    public void tearDown() throws Exception {
        notificationEvent = null;
    }

    @Test
    public void testConstructor() throws Exception{
        User user = new User();
        user.setShibId("shibId");
        notificationEvent = new NotificationEvent(user.getShibId(), "origin2");
        assertEquals("origin2", notificationEvent.getOrigin());
        assertEquals("shibId", notificationEvent.getUserId());
        
    }

    @Test
    public void testSetOrigin() throws Exception {
        notificationEvent.setOrigin("localhost");
        assertEquals("localhost", notificationEvent.getOrigin());
    }

    @Test 
    public void testSetUserId() throws Exception {
        notificationEvent.setUserId("shibId");
        assertEquals("shibId", notificationEvent.getUserId());
    }
}
