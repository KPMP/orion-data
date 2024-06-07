package org.kpmp.Notification;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NotificationEventTest {
    
    private NotificationEvent notificationEvent;

    @Before
    public void setUp() throws Exception {
        notificationEvent = new NotificationEvent("origin", "user");
    }

    @After
    public void tearDown() throws Exception {
        notificationEvent = null;
    }

    @Test
    public void testConstructor() throws Exception{
        notificationEvent = new NotificationEvent("user2", "origin2");
        assertEquals("origin2", notificationEvent.getOrigin());
        assertEquals("user2", notificationEvent.getUserId());
        
    }

    @Test
    public void testSetOrigin() throws Exception {
        notificationEvent.setOrigin("localhost");
        assertEquals("localhost", notificationEvent.getOrigin());
    }

    @Test
    public void testSetUser() throws Exception {
        notificationEvent.setUserId("user2");
        assertEquals("user2", notificationEvent.getUserId());
    }
}
