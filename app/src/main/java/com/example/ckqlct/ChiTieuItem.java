package com.example.ckqlct;

public class ChiTieuItem {
    private String income_type;
    private String income_name;

    public ChiTieuItem(String income_name, String income_type) {
        this.income_name = income_name;
        this.income_type = income_type;
    }

    public String getIncome_type() {
        return income_type;
    }

    public void setIncome_type(String income_type) {
        this.income_type = income_type;
    }

    public String getIncome_name() {
        return income_name;
    }

    public void setIncome_name(String income_name) {
        this.income_name = income_name;
    }
}
