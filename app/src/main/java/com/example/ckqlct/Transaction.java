package com.example.ckqlct;

import java.util.Locale;

public class Transaction {
    private String type;
    private String name;
    private String total; // Change this to String to store formatted currency
    private String note;
    private String date;

    public Transaction(String type, String name, String total, String note, String date) {
        this.type = type;
        this.name = name;
        this.total = total;
        this.note = note;
        this.date = date;
    }

    // Getter methods
    public String getType() { return type; }
    public String getName() { return name; }
    public String getTotal() { return total; }
    public String getNote() { return note; }
    public String getDate() { return date; }
    // New method to format total
}


