package com.demo.carservicetracker2.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;


public class CurrencyModel implements Parcelable {
    public static final Parcelable.Creator<CurrencyModel> CREATOR = new Parcelable.Creator<CurrencyModel>() {
        @Override 
        public CurrencyModel createFromParcel(Parcel in) {
            return new CurrencyModel(in);
        }
        @Override 
        public CurrencyModel[] newArray(int size) {
            return new CurrencyModel[size];
        }
    };
    private String Currency;
    private String CurrencyName;
    private String CurrencySymbol;
    private boolean isSelect;

    @Override 
    public int describeContents() {
        return 0;
    }

    public CurrencyModel() {
        this.isSelect = false;
    }

    public CurrencyModel(String CurrencySymbol) {
        this.isSelect = false;
        this.CurrencySymbol = CurrencySymbol;
    }

    public CurrencyModel(String currency, String currencySymbol, String currencyName, boolean isSelect) {
        this.Currency = currency;
        this.CurrencySymbol = currencySymbol;
        this.CurrencyName = currencyName;
        this.isSelect = isSelect;
    }

    public CurrencyModel(Parcel in) {
        this.isSelect = false;
        this.Currency = in.readString();
        this.CurrencySymbol = in.readString();
        this.CurrencyName = in.readString();
        this.isSelect = in.readByte() != 0;
    }

    public String getCurrency() {
        return this.Currency;
    }

    public void setCurrency(String currency) {
        this.Currency = currency;
    }

    public String getCurrencySymbol() {
        return this.CurrencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.CurrencySymbol = currencySymbol;
    }

    public String getCurrencyName() {
        return this.CurrencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.CurrencyName = currencyName;
    }

    public boolean isSelect() {
        return this.isSelect;
    }

    public void setSelect(boolean select) {
        this.isSelect = select;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CurrencyModel) {
            CurrencyModel currencyModel = (CurrencyModel) o;
            return this.isSelect == currencyModel.isSelect && Objects.equals(this.Currency, currencyModel.Currency) && Objects.equals(this.CurrencySymbol, currencyModel.CurrencySymbol) && Objects.equals(this.CurrencyName, currencyModel.CurrencyName);
        }
        return false;
    }
    public int hashCode() {
        return Objects.hash(this.CurrencySymbol);
    }
    @Override 
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.Currency);
        parcel.writeString(this.CurrencySymbol);
        parcel.writeString(this.CurrencyName);
        parcel.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
    }
}
