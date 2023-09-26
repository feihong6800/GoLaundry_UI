package com.example.wanjing;

public class MonthData {
    private String month;
    private int spendingAmount;

    public MonthData() {
        // Default constructor required for Firebase
    }

    public MonthData(String month, int spendingAmount) {
        this.month = month;
        this.spendingAmount = spendingAmount;
    }

    public String getMonth() {
        return month;
    }

    public int getSpendingAmount() {
        return spendingAmount;
    }
}

