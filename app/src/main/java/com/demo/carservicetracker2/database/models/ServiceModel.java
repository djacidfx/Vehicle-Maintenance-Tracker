package com.demo.carservicetracker2.database.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;


@Entity(tableName = "ServiceModel")
public class ServiceModel implements Parcelable {
    public static final Parcelable.Creator<ServiceModel> CREATOR = new Parcelable.Creator<ServiceModel>() { 
        
        @Override 
        public ServiceModel createFromParcel(Parcel in) {
            return new ServiceModel(in);
        }

        
        @Override 
        public ServiceModel[] newArray(int size) {
            return new ServiceModel[size];
        }
    };

    @ColumnInfo(name = "CarId")
    String CarId;
    @ColumnInfo(name = "Comments")
    String Comments;
    @ColumnInfo(name = "Date")
    long Date;
    @ColumnInfo(name = "LabourCosts")
    double LabourCosts;
    @ColumnInfo(name = "Mileage")
    long Mileage;
    @ColumnInfo(name = "PartsCosts")
    double PartsCosts;
    @ColumnInfo(name = "SelectedServiceID")
    String SelectedServiceID;
    @PrimaryKey()
    @NonNull
    String ServiceId;
    @ColumnInfo(name = "ServiceName")
    String ServiceName;
    @ColumnInfo(name = "VendorCodes")
    String VendorCodes;

    @Override 
    public int describeContents() {
        return 0;
    }

    public ServiceModel() {
    }

    public ServiceModel(String serviceId, String selectedServiceID, String serviceName, long mileage, long date, double partsCosts, double labourCosts, String vendorCodes, String comments, String carId) {
        this.ServiceId = serviceId;
        this.SelectedServiceID = selectedServiceID;
        this.ServiceName = serviceName;
        this.Mileage = mileage;
        this.Date = date;
        this.PartsCosts = partsCosts;
        this.LabourCosts = labourCosts;
        this.VendorCodes = vendorCodes;
        this.Comments = comments;
        this.CarId = carId;
    }

    protected ServiceModel(Parcel in) {
        this.ServiceId = in.readString();
        this.SelectedServiceID = in.readString();
        this.ServiceName = in.readString();
        this.Mileage = in.readLong();
        this.Date = in.readLong();
        this.PartsCosts = in.readDouble();
        this.LabourCosts = in.readDouble();
        this.VendorCodes = in.readString();
        this.Comments = in.readString();
        this.CarId = in.readString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.ServiceId.equals(((ServiceModel) o).ServiceId);
    }

    public int hashCode() {
        return Objects.hash(this.ServiceId);
    }

    public String getServiceId() {
        return this.ServiceId;
    }

    public void setServiceId(String serviceId) {
        this.ServiceId = serviceId;
    }

    public String getSelectedServiceID() {
        return this.SelectedServiceID;
    }

    public void setSelectedServiceID(String selectedServiceID) {
        this.SelectedServiceID = selectedServiceID;
    }

    public String getServiceName() {
        return this.ServiceName;
    }

    public void setServiceName(String serviceName) {
        this.ServiceName = serviceName;
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

    public double getPartsCosts() {
        return this.PartsCosts;
    }

    public void setPartsCosts(double partsCosts) {
        this.PartsCosts = partsCosts;
    }

    public double getLabourCosts() {
        return this.LabourCosts;
    }

    public void setLabourCosts(double labourCosts) {
        this.LabourCosts = labourCosts;
    }

    public String getVendorCodes() {
        return this.VendorCodes;
    }

    public void setVendorCodes(String vendorCodes) {
        this.VendorCodes = vendorCodes;
    }

    public String getComments() {
        return this.Comments;
    }

    public void setComments(String comments) {
        this.Comments = comments;
    }

    public String getCarId() {
        return this.CarId;
    }

    public void setCarId(String carId) {
        this.CarId = carId;
    }

    @Override 
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.ServiceId);
        parcel.writeString(this.SelectedServiceID);
        parcel.writeString(this.ServiceName);
        parcel.writeLong(this.Mileage);
        parcel.writeLong(this.Date);
        parcel.writeDouble(this.PartsCosts);
        parcel.writeDouble(this.LabourCosts);
        parcel.writeString(this.VendorCodes);
        parcel.writeString(this.Comments);
        parcel.writeString(this.CarId);
    }
}
