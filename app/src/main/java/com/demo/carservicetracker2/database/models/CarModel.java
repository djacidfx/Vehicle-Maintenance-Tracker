package com.demo.carservicetracker2.database.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;


@Entity(tableName = "CarModel")
public class CarModel implements Parcelable {
    public static final Parcelable.Creator<CarModel> CREATOR = new Parcelable.Creator<CarModel>() { 
        
        @Override 
        public CarModel createFromParcel(Parcel in) {
            return new CarModel(in);
        }

        
        @Override 
        public CarModel[] newArray(int size) {
            return new CarModel[size];
        }
    };


    @PrimaryKey()
    @NonNull
    String CarId;

    @ColumnInfo(name = "CarImageName")
    String CarImageName;
    @ColumnInfo(name = "CarName")
    String CarName;
    @ColumnInfo(name = "Comments")
    String Comments;

    @ColumnInfo(name = "CreationDate")
    long CreationDate;

    @ColumnInfo(name = "LicensePlate")
    String LicensePlate;

    @ColumnInfo(name = "OilChangeMileage")
    long OilChangeMileage;
    @ColumnInfo(name = "OilIntervalDate")
    int OilIntervalDate;
    @ColumnInfo(name = "PurchaseDate")
    long PurchaseDate;

    @ColumnInfo(name = "ServiceChangeMileage")
    long ServiceChangeMileage;


    @ColumnInfo(name = "ServiceIntervalDate")
    int ServiceIntervalDate;

    @ColumnInfo(name = "isSelected")
    boolean isSelected;

    @Override 
    public int describeContents() {
        return 0;
    }

    public CarModel() {
    }

    public CarModel(String carId, String carImageName, String carName, long purchaseDate, String licensePlate, long oilChangeMileage, int oilIntervalDate, long serviceChangeMileage, int serviceIntervalDate, String comments, long creationDate) {
        this.CarId = carId;
        this.CarImageName = carImageName;
        this.CarName = carName;
        this.PurchaseDate = purchaseDate;
        this.LicensePlate = licensePlate;
        this.OilChangeMileage = oilChangeMileage;
        this.OilIntervalDate = oilIntervalDate;
        this.ServiceChangeMileage = serviceChangeMileage;
        this.ServiceIntervalDate = serviceIntervalDate;
        this.Comments = comments;
        this.CreationDate = creationDate;
    }

    public CarModel(String carId) {
        this.CarId = carId;
    }

    protected CarModel(Parcel in) {
        this.CarId = in.readString();
        this.CarImageName = in.readString();
        this.CarName = in.readString();
        this.PurchaseDate = in.readLong();
        this.LicensePlate = in.readString();
        this.OilChangeMileage = in.readLong();
        this.OilIntervalDate = in.readInt();
        this.ServiceChangeMileage = in.readLong();
        this.ServiceIntervalDate = in.readInt();
        this.Comments = in.readString();
        this.CreationDate = in.readLong();
    }

    public String getCarId() {
        return this.CarId;
    }

    public void setCarId(String carId) {
        this.CarId = carId;
    }

    public String getCarImageName() {
        return this.CarImageName;
    }

    public void setCarImageName(String carImageName) {
        this.CarImageName = carImageName;
    }

    public String getCarName() {
        return this.CarName;
    }

    public void setCarName(String carName) {
        this.CarName = carName;
    }

    public long getPurchaseDate() {
        return this.PurchaseDate;
    }

    public void setPurchaseDate(long purchaseDate) {
        this.PurchaseDate = purchaseDate;
    }

    public String getLicensePlate() {
        return this.LicensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.LicensePlate = licensePlate;
    }

    public long getOilChangeMileage() {
        return this.OilChangeMileage;
    }

    public void setOilChangeMileage(long oilChangeMileage) {
        this.OilChangeMileage = oilChangeMileage;
    }

    public int getOilIntervalDate() {
        return this.OilIntervalDate;
    }

    public void setOilIntervalDate(int oilIntervalDate) {
        this.OilIntervalDate = oilIntervalDate;
    }

    public long getServiceChangeMileage() {
        return this.ServiceChangeMileage;
    }

    public void setServiceChangeMileage(long serviceChangeMileage) {
        this.ServiceChangeMileage = serviceChangeMileage;
    }

    public int getServiceIntervalDate() {
        return this.ServiceIntervalDate;
    }

    public void setServiceIntervalDate(int serviceIntervalDate) {
        this.ServiceIntervalDate = serviceIntervalDate;
    }

    public String getComments() {
        return this.Comments;
    }

    public void setComments(String comments) {
        this.Comments = comments;
    }

    public long getCreationDate() {
        return this.CreationDate;
    }

    public void setCreationDate(long creationDate) {
        this.CreationDate = creationDate;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.CarId.equals(((CarModel) o).CarId);
    }

    public int hashCode() {
        return Objects.hash(this.CarId);
    }

    @Override 
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.CarId);
        parcel.writeString(this.CarImageName);
        parcel.writeString(this.CarName);
        parcel.writeLong(this.PurchaseDate);
        parcel.writeString(this.LicensePlate);
        parcel.writeLong(this.OilChangeMileage);
        parcel.writeInt(this.OilIntervalDate);
        parcel.writeLong(this.ServiceChangeMileage);
        parcel.writeInt(this.ServiceIntervalDate);
        parcel.writeString(this.Comments);
        parcel.writeLong(this.CreationDate);
    }
}
