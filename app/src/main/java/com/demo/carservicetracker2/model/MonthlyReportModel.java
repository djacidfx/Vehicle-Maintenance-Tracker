package com.demo.carservicetracker2.model;


public class MonthlyReportModel {
    String MonthName;
    double TotalAmt;

    public MonthlyReportModel(String monthName, double totalAmt) {
        this.MonthName = monthName;
        this.TotalAmt = totalAmt;
    }

    public String getMonthName() {
        return this.MonthName;
    }

    public void setMonthName(String monthName) {
        this.MonthName = monthName;
    }

    public double getTotalAmt() {
        return this.TotalAmt;
    }

    public void setTotalAmt(double totalAmt) {
        this.TotalAmt = totalAmt;
    }
}
