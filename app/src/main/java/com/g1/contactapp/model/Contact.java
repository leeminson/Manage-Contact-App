package com.g1.contactapp.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;
@Entity
public class Contact implements Serializable , Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private byte[] image;

    public Contact(String name) {
        this.name = name;

    }

    @SuppressLint("NewApi")
    protected Contact(Parcel in) {
        id = in.readLong();
        name = in.readString();
        image = in.readBlob();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }


    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressLint("NewApi")
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeBlob(image);
    }
}
