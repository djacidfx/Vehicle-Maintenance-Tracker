package com.demo.carservicetracker2.database.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;


@Entity(tableName = "FuelModel")
public class FuelModel implements Parcelable {
    public static final Parcelable.Creator<FuelModel> CREATOR = new Parcelable.Creator<FuelModel>() { 
        
        @Override 
        public FuelModel createFromParcel(Parcel in) {
            return new FuelModel(in);
        }

        
        @Override 
        public FuelModel[] newArray(int size) {
            return new FuelModel[size];
        }
    };


    @ColumnInfo(name = "CarId")
    String CarId;

    @ColumnInfo(name = "Comment")
    String Comment;
    @ColumnInfo(name = "Date")
    long Date;

    @PrimaryKey()
    @NonNull
    String FuelId;
    @ColumnInfo(name = "FuelType")
    String FuelType;
    @ColumnInfo(name = "FuelVolume")
    double FuelVolume;
    @ColumnInfo(name = "Mileage")
    long Mileage;
    @ColumnInfo(name = "PricePerLiter")
    double PricePerLiter;
    @ColumnInfo(name = "TotalCost")
    double TotalCost;

    @Override 
    public int describeContents() {
        return 0;
    }

    public FuelModel() {
    }

    public FuelModel(String fuelId, double totalCost, String fuelType, double pricePerLiter, double fuelVolume, long mileage, long date, String comment, String carId) {
        this.FuelId = fuelId;
        this.TotalCost = totalCost;
        this.FuelType = fuelType;
        this.PricePerLiter = pricePerLiter;
        this.FuelVolume = fuelVolume;
        this.Mileage = mileage;
        this.Date = date;
        this.Comment = comment;
        this.CarId = carId;
    }

    protected FuelModel(Parcel in) {
        this.FuelId = in.readString();
        this.TotalCost = in.readDouble();
        this.FuelType = in.readString();
        this.PricePerLiter = in.readDouble();
        this.FuelVolume = in.readDouble();
        this.Mileage = in.readLong();
        this.Date = in.readLong();
        this.Comment = in.readString();
        this.CarId = in.readString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.FuelId.equals(((FuelModel) o).FuelId);
    }

    public int hashCode() {
        return Objects.hash(this.FuelId);
    }

    public String getFuelId() {
        return this.FuelId;
    }

    public void setFuelId(String fuelId) {
        this.FuelId = fuelId;
    }

    public double getTotalCost() {
        return this.TotalCost;
    }

    public void setTotalCost(double totalCost) {
        this.TotalCost = totalCost;
    }

    public String getFuelType() {
        return this.FuelType;
    }

    public void setFuelType(String fuelType) {
        this.FuelType = fuelType;
    }

    public double getPricePerLiter() {
        return this.PricePerLiter;
    }

    public void setPricePerLiter(double pricePerLiter) {
        this.PricePerLiter = pricePerLiter;
    }

    public double getFuelVolume() {
        return this.FuelVolume;
    }

    public void setFuelVolume(double fuelVolume) {
        this.FuelVolume = fuelVolume;
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

    public String getComment() {
        return this.Comment;
    }

    public void setComment(String comment) {
        this.Comment = comment;
    }

    public String getCarId() {
        return this.CarId;
    }

    public void setCarId(String carId) {
        this.CarId = carId;
    }

    @Override 
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.FuelId);
        parcel.writeDouble(this.TotalCost);
        parcel.writeString(this.FuelType);
        parcel.writeDouble(this.PricePerLiter);
        parcel.writeDouble(this.FuelVolume);
        parcel.writeLong(this.Mileage);
        parcel.writeLong(this.Date);
        parcel.writeString(this.Comment);
        parcel.writeString(this.CarId);
    }
}
