package com.demo.carservicetracker2.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;


public class ServiceDocumentModel implements Parcelable {
    public static final Parcelable.Creator<ServiceDocumentModel> CREATOR = new Parcelable.Creator<ServiceDocumentModel>() { 
        
        @Override 
        public ServiceDocumentModel createFromParcel(Parcel in) {
            return new ServiceDocumentModel(in);
        }

        
        @Override 
        public ServiceDocumentModel[] newArray(int size) {
            return new ServiceDocumentModel[size];
        }
    };
    String IconName;
    String Id;
    String Name;
    int Type;
    boolean isShowInReminder;

    @Override 
    public int describeContents() {
        return 0;
    }

    public ServiceDocumentModel(String id, String name, String iconName, int type, boolean isShowInReminder) {
        this.Id = id;
        this.Name = name;
        this.IconName = iconName;
        this.Type = type;
        this.isShowInReminder = isShowInReminder;
    }

    public ServiceDocumentModel(String id) {
        this.Id = id;
    }

    protected ServiceDocumentModel(Parcel in) {
        this.Id = in.readString();
        this.Name = in.readString();
        this.IconName = in.readString();
        this.Type = in.readInt();
        this.isShowInReminder = in.readByte() != 0;
    }

    public String getId() {
        return this.Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getIconName() {
        return this.IconName;
    }

    public void setIconName(String iconName) {
        this.IconName = iconName;
    }

    public int getType() {
        return this.Type;
    }

    public void setType(int type) {
        this.Type = type;
    }

    public boolean isShowInReminder() {
        return this.isShowInReminder;
    }

    public void setShowInReminder(boolean showInReminder) {
        this.isShowInReminder = showInReminder;
    }

    @Override 
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.Id);
        parcel.writeString(this.Name);
        parcel.writeString(this.IconName);
        parcel.writeInt(this.Type);
        parcel.writeByte(this.isShowInReminder ? (byte) 1 : (byte) 0);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Objects.equals(this.Id, ((ServiceDocumentModel) o).Id);
    }

    public int hashCode() {
        return Objects.hash(this.Id);
    }
}
