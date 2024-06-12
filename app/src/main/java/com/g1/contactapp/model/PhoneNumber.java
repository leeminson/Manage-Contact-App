package com.g1.contactapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
@Entity(foreignKeys = @ForeignKey(entity = Contact.class,parentColumns = "id",childColumns = "contactId",onDelete = ForeignKey.CASCADE))
public class PhoneNumber {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String number;
    private long contactId;

    public PhoneNumber(String number) {
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }
}
