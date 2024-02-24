package com.demo.carservicetracker2.database.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;


@Entity(tableName = "ReminderModel")
public class ReminderModel implements Parcelable {
    public static final Parcelable.Creator<ReminderModel> CREATOR = new Parcelable.Creator<ReminderModel>() { 
        
        @Override 
        public ReminderModel createFromParcel(Parcel in) {
            return new ReminderModel(in);
        }

        
        @Override 
        public ReminderModel[] newArray(int size) {
            return new ReminderModel[size];
        }
    };

    @ColumnInfo(name = "AdditionalText")
    String AdditionalText;
    @ColumnInfo(name = "CarId")
    String CarId;
    @ColumnInfo(name = "Date")
    long Date;
    @ColumnInfo(name = "DateOfCreation")
    long DateOfCreation;
    @ColumnInfo(name = "Mileage")
    long Mileage;
    @ColumnInfo(name = "RemainderText")
    String RemainderText;

    @PrimaryKey()
    @NonNull
    String ReminderId;
    @ColumnInfo(name = "ReminderType")
    int ReminderType;

    @ColumnInfo(name = "SelectedServiceDocId")
    String SelectedServiceDocId;

    @Override 
    public int describeContents() {
        return 0;
    }

    public ReminderModel() {
    }

    public ReminderModel(String reminderId, String selectedServiceDocId, String remainderText, long mileage, long date, String additionalText, long dateOfCreation, String carId, int reminderType) {
        this.ReminderId = reminderId;
        this.SelectedServiceDocId = selectedServiceDocId;
        this.RemainderText = remainderText;
        this.Mileage = mileage;
        this.Date = date;
        this.AdditionalText = additionalText;
        this.DateOfCreation = dateOfCreation;
        this.CarId = carId;
        this.ReminderType = reminderType;
    }

    protected ReminderModel(Parcel in) {
        this.ReminderId = in.readString();
        this.SelectedServiceDocId = in.readString();
        this.RemainderText = in.readString();
        this.Mileage = in.readLong();
        this.Date = in.readLong();
        this.AdditionalText = in.readString();
        this.DateOfCreation = in.readLong();
        this.CarId = in.readString();
        this.ReminderType = in.readInt();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.ReminderId.equals(((ReminderModel) o).ReminderId);
    }

    public int hashCode() {
        return Objects.hash(this.ReminderId);
    }

    public String getReminderId() {
        return this.ReminderId;
    }

    public void setReminderId(String reminderId) {
        this.ReminderId = reminderId;
    }

    public String getSelectedServiceDocId() {
        return this.SelectedServiceDocId;
    }

    public void setSelectedServiceDocId(String selectedServiceDocId) {
        this.SelectedServiceDocId = selectedServiceDocId;
    }

    public String getRemainderText() {
        return this.RemainderText;
    }

    public void setRemainderText(String remainderText) {
        this.RemainderText = remainderText;
    }

    public long getMileage() {
        return this.Mileage;
    }

    public void setMileage(long mileage) {
        this.Mileage = mileage;
    }

    public long getDate() {
        return this.Date;
    }

    public void setDate(long date) {
        this.Date = date;
    }

    public String getAdditionalText() {
        return this.AdditionalText;
    }

    public void setAdditionalText(String additionalText) {
        this.AdditionalText = additionalText;
    }

    public long getDateOfCreation() {
        return this.DateOfCreation;
    }

    public void setDateOfCreation(long dateOfCreation) {
        this.DateOfCreation = dateOfCreation;
    }

    public String getCarId() {
        return this.CarId;
    }

    public void setCarId(String carId) {
        this.CarId = carId;
    }

    public int getReminderType() {
        return this.ReminderType;
    }

    public void setReminderType(int reminderType) {
        this.ReminderType = reminderType;
    }

    @Override 
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.ReminderId);
        parcel.writeString(this.SelectedServiceDocId);
        parcel.writeString(this.RemainderText);
        parcel.writeLong(this.Mileage);
        parcel.writeLong(this.Date);
        parcel.writeString(this.AdditionalText);
        parcel.writeLong(this.DateOfCreation);
        parcel.writeString(this.CarId);
        parcel.writeInt(this.ReminderType);
    }
}
