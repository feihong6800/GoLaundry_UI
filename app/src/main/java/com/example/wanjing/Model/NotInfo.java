package com.example.wanjing.Model;

public class NotInfo {
    private String notiTitle, notiContent, notiDate, notiID;
    private int read;

    public NotInfo() {
    }

    public NotInfo(String notiTitle, String notiContent, String notiDate, String notiID, int read) {
        this.notiTitle = notiTitle;
        this.notiContent = notiContent;
        this.notiDate = notiDate;
        this.notiID = notiID;
        this.read = read;
    }

    public String getNotiTitle() {
        return notiTitle;
    }

    public void setNotiTitle(String notiTitle) {
        this.notiTitle = notiTitle;
    }

    public String getNotiContent() {
        return notiContent;
    }

    public void setNotiContent(String notiContent) {
        this.notiContent = notiContent;
    }

    public String getNotiDate() {
        return notiDate;
    }

    public void setNotiDate(String notiDate) {
        this.notiDate = notiDate;
    }

    public String getNotiID() {
        return notiID;
    }

    public void setNotiID(String notiID) {
        this.notiID = notiID;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }
}
