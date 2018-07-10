package com.example.android.bookStore.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class would contain the item details coming from db.
 */
public class CatalogItemDetail implements Parcelable {

    private int id;
    private String itemName;
    private int itemPrice;
    private int itemQuantity;
    private String supplierName;
    private String phoneNumber;

    public CatalogItemDetail(int id, String itemName, int itemPrice, int itemQuantity,
                             String supplierName, String phoneNumber) {
        this.id = id;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
        this.supplierName = supplierName;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "CatalogItemDetail{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", itemPrice=" + itemPrice +
                ", itemQuantity=" + itemQuantity +
                ", supplierName='" + supplierName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.itemName);
        dest.writeInt(this.itemPrice);
        dest.writeInt(this.itemQuantity);
        dest.writeString(this.supplierName);
        dest.writeString(this.phoneNumber);
    }

    public CatalogItemDetail() {
    }

    protected CatalogItemDetail(Parcel in) {
        this.id = in.readInt();
        this.itemName = in.readString();
        this.itemPrice = in.readInt();
        this.itemQuantity = in.readInt();
        this.supplierName = in.readString();
        this.phoneNumber = in.readString();
    }

    public static final Creator<CatalogItemDetail> CREATOR = new Creator<CatalogItemDetail>() {
        @Override
        public CatalogItemDetail createFromParcel(Parcel source) {
            return new CatalogItemDetail(source);
        }

        @Override
        public CatalogItemDetail[] newArray(int size) {
            return new CatalogItemDetail[size];
        }
    };
}
