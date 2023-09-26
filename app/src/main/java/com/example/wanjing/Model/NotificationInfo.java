package com.example.wanjing.Model;

public class NotificationInfo {
    private String notificationTitle, notificationContent, notificationDate, notificationID, notificationStatus;
    private Boolean notificationRead;

    public NotificationInfo() {
    }

    public NotificationInfo(String notificationTitle, String notificationContent, String notificationDate, String notificationID, String notificationStatus, Boolean notificationRead) {
        this.notificationTitle = notificationTitle;
        this.notificationContent = notificationContent;
        this.notificationDate = notificationDate;
        this.notificationID = notificationID;
        this.notificationStatus = notificationStatus;
        this.notificationRead = notificationRead;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public Boolean getNotificationRead() {
        return notificationRead;
    }

    public void setNotificationRead(Boolean notificationRead) {
        this.notificationRead = notificationRead;
    }
}
