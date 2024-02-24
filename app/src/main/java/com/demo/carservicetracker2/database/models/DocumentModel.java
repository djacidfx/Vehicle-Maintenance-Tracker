package com.demo.carservicetracker2.database.models;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;
@Entity(tableName = "DocumentModel")
public class DocumentModel implements Parcelable {
    public static final Parcelable.Creator<DocumentModel> CREATOR = new Parcelable.Creator<DocumentModel>() {
        @Override 
        public DocumentModel createFromParcel(Parcel in) {
            return new DocumentModel(in);
        }
        @Override 
        public DocumentModel[] newArray(int size) {
            return new DocumentModel[size];
        }
    };


    @ColumnInfo(name = "CarId")
    String CarId;

    @ColumnInfo(name = "Comments")
    String Comments;
    @ColumnInfo(name = "Date")
    long Date;
    @ColumnInfo(name = "DocName")
    String DocName;
    @ColumnInfo(name = "DocumentId")

    @PrimaryKey()
    @NonNull
    String DocumentId;
    @ColumnInfo(name = "Mileage")
    long Mileage;
    @ColumnInfo(name = "SelectedDocId")
    String SelectedDocId;
    @ColumnInfo(name = "TotalCoast")
    double TotalCoast;

    @Override 
    public int describeContents() {
        return 0;
    }

    public DocumentModel() {
    }

    public DocumentModel(String documentId, String selectedDocId, String docName, long mileage, long date, double totalCoast, String comments, String carId) {
        this.DocumentId = documentId;
        this.SelectedDocId = selectedDocId;
        this.DocName = docName;
        this.Mileage = mileage;
        this.Date = date;
        this.TotalCoast = totalCoast;
        this.Comments = comments;
        this.CarId = carId;
    }

    protected DocumentModel(Parcel in) {
        this.DocumentId = in.readString();
        this.SelectedDocId = in.readString();
        this.DocName = in.readString();
        this.Mileage = in.readLong();
        this.Date = in.readLong();
        this.TotalCoast = in.readDouble();
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
        return this.DocumentId.equals(((DocumentModel) o).DocumentId);
    }

    public int hashCode() {
        return Objects.hash(this.DocumentId);
    }

    public String getDocumentId() {
        return this.DocumentId;
    }

    public void setDocumentId(String documentId) {
        this.DocumentId = documentId;
    }

    public String getSelectedDocId() {
        return this.SelectedDocId;
    }

    public void setSelectedDocId(String selectedDocId) {
        this.SelectedDocId = selectedDocId;
    }

    public String getDocName() {
        return this.DocName;
    }

    public void setDocName(String docName) {
        this.DocName = docName;
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

    public double getTotalCoast() {
        return this.TotalCoast;
    }

    public void setTotalCoast(double totalCoast) {
        this.TotalCoast = totalCoast;
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
        parcel.writeString(this.DocumentId);
        parcel.writeString(this.SelectedDocId);
        parcel.writeString(this.DocName);
        parcel.writeLong(this.Mileage);
        parcel.writeLong(this.Date);
        parcel.writeDouble(this.TotalCoast);
        parcel.writeString(this.Comments);
        parcel.writeString(this.CarId);
    }
}
