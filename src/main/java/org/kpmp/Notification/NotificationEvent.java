package org.kpmp.Notification;

public class NotificationEvent {
    private String origin;
    private String userId;

    public NotificationEvent(String userId, String origin){
        this.origin = origin;
        this.userId = userId;
    }


    public String getOrigin() {
        return this.origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
