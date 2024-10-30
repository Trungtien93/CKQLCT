package com.example.ckqlct;

public class RatingItem {
    private String userName;
    private String note;
    private String time; // Thời gian lưu dưới dạng chuỗi

    public RatingItem(String userName, String note, String time) {
        this.userName = userName;
        this.note = note;
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public String getNote() {
        return note;
    }

    public String getTime() {
        return time;
    }
}
