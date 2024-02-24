package com.demo.carservicetracker2.model;

import com.demo.carservicetracker2.utils.AppPref;


public class ReportDataModel {
    float TotalDocumentCost;
    float TotalFuelCost;
    float TotalServiceCost;

    public ReportDataModel() {
    }

    public ReportDataModel(float totalServiceCost, float totalDocumentCost, float totalFuelCost) {
        this.TotalServiceCost = totalServiceCost;
        this.TotalDocumentCost = totalDocumentCost;
        this.TotalFuelCost = totalFuelCost;
    }

    public float getTotalServiceCost() {
        return this.TotalServiceCost;
    }

    public void setTotalServiceCost(float totalServiceCost) {
        this.TotalServiceCost = totalServiceCost;
    }

    public float getTotalDocumentCost() {
        return this.TotalDocumentCost;
    }

    public void setTotalDocumentCost(float totalDocumentCost) {
        this.TotalDocumentCost = totalDocumentCost;
    }

    public float getTotalFuelCost() {
        return this.TotalFuelCost;
    }

    public void setTotalFuelCost(float totalFuelCost) {
        this.TotalFuelCost = totalFuelCost;
    }

    public float getTotal() {
        return getTotalServiceCost() + getTotalDocumentCost() + (AppPref.IsShowFuel() ? getTotalFuelCost() : 0.0f);
    }
}
