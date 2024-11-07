package com.example.ckqlct;

public class ThuNhapItem {
    private String expense_type;
    private String expense_name;

    public ThuNhapItem(String expense_name, String expense_type) {
        this.expense_name = expense_name;
        this.expense_type = expense_type;
    }

    public String getExpense_type() {
        return expense_type;
    }

    public void setExpense_type(String expense_type) {
        this.expense_type = expense_type;
    }

    public String getExpense_name() {
        return expense_name;
    }

    public void setExpense_name(String expense_name) {
        this.expense_name = expense_name;
    }
}
