package com.demo.carservicetracker2.backupRestore;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import java.util.ArrayList;


public class RestoreListModel extends BaseObservable {
    private ArrayList<RestoreRowModel> arrayList;
    private String noDataDetail;
    private int noDataIcon;
    private String noDataText;

    @Bindable
    public ArrayList<RestoreRowModel> getArrayList() {
        return this.arrayList;
    }

    public void setArrayList(ArrayList<RestoreRowModel> arrayList) {
        this.arrayList = arrayList;
        notifyChange();
    }

    public int getNoDataIcon() {
        return this.noDataIcon;
    }

    public void setNoDataIcon(int noDataIcon) {
        this.noDataIcon = noDataIcon;
    }

    public String getNoDataText() {
        return this.noDataText;
    }

    public void setNoDataText(String noDataText) {
        this.noDataText = noDataText;
    }

    public String getNoDataDetail() {
        return this.noDataDetail;
    }

    public void setNoDataDetail(String noDataDetail) {
        this.noDataDetail = noDataDetail;
    }

    public boolean isListData() {
        return getArrayList() != null && getArrayList().size() > 0;
    }
}
