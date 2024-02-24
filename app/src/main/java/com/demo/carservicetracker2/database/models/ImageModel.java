package com.demo.carservicetracker2.database.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "ImageModel")

public class ImageModel implements Parcelable {
    public static final Parcelable.Creator<ImageModel> CREATOR = new Parcelable.Creator<ImageModel>() { 
        
        @Override 
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        
        @Override 
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    @PrimaryKey()
    @NonNull
    String ImageId;
    @ColumnInfo(name = "ImageName")
    String ImageName;
    @ColumnInfo(name = "ReferId")
    String ReferId;
    @ColumnInfo(name = "isDeleted")
    boolean isDeleted;
    @ColumnInfo(name = "isNew")
    boolean isNew;
    @ColumnInfo(name = "uri")
    String uri;

    @Override 
    public int describeContents() {
        return 0;
    }

    public ImageModel() {
        this.isNew = false;
        this.uri = "";
        this.isDeleted = false;
    }

    public ImageModel(String imageId, String referId, String imageName) {
        this.isNew = false;
        this.uri = "";
        this.isDeleted = false;
        this.ImageId = imageId;
        this.ReferId = referId;
        this.ImageName = imageName;
    }

    public ImageModel(String imageId, String referId, String imageName, boolean isNew, String uri) {
        this.isDeleted = false;
        this.ImageId = imageId;
        this.ReferId = referId;
        this.ImageName = imageName;
        this.isNew = isNew;
        this.uri = uri;
    }

    protected ImageModel(Parcel in) {
        this.isNew = false;
        this.uri = "";
        this.isDeleted = false;
        this.ImageId = in.readString();
        this.ReferId = in.readString();
        this.ImageName = in.readString();
        this.isNew = in.readByte() != 0;
        this.uri = in.readString();
        this.isDeleted = in.readByte() != 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.ImageId.equals(((ImageModel) o).ImageId);
    }

    public int hashCode() {
        return Objects.hash(this.ImageId);
    }

    public String getImageId() {
        return this.ImageId;
    }

    public void setImageId(String imageId) {
        this.ImageId = imageId;
    }

    public String getReferId() {
        return this.ReferId;
    }

    public void setReferId(String referId) {
        this.ReferId = referId;
    }

    public String getImageName() {
        return this.ImageName;
    }

    public void setImageName(String imageName) {
        this.ImageName = imageName;
    }

    public boolean isNew() {
        return this.isNew;
    }

    public void setNew(boolean aNew) {
        this.isNew = aNew;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }

    @Override 
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.ImageId);
        parcel.writeString(this.ReferId);
        parcel.writeString(this.ImageName);
        parcel.writeByte(this.isNew ? (byte) 1 : (byte) 0);
        parcel.writeString(this.uri);
        parcel.writeByte(this.isDeleted ? (byte) 1 : (byte) 0);
    }
}
