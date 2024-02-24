package com.demo.carservicetracker2.database.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "DbVersionModel")
public class DbVersionModel {


    @PrimaryKey()
    private int versionNumber;


    public int getVersionNumber() {
        return this.versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public DbVersionModel(int versionNumber) {
        this.versionNumber = versionNumber;
    }
}
