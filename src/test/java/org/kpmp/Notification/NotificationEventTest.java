package org.kpmp.Notification;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.users.User;

public class NotificationEventTest {
    
    private NotificationEvent notificationEvent;

    @Before
    public void setUp() throws Exception {
        notificationEvent = new NotificationEvent("shibId", "origin");
    }

    @After
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
