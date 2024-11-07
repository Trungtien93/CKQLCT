package com.example.ckqlct;

public class ThuNhapItem {
    private String expense_type;
    private String expense_name;
    private String expense_total;
    private String expense_date;

    public ThuNhapItem(String expense_name, String expense_type, String expense_total, String expense_date) {
        this.expense_name = expense_name;
        this.expense_type = expense_type;
        this.expense_total = expense_total;
        this.expense_date = expense_date;
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

    public String getExpense_total() {
        return expense_total;
    }

    public void setExpense_total(String expense_total) {
        this.expense_total = expense_total;
    }

    public String getExpense_date() {
        return expense_date;
    }

    public void setExpense_date(String expense_date) {
        this.expense_date = expense_date;
    }
}
