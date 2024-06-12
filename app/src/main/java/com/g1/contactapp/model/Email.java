package com.g1.contactapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Contact.class,parentColumns = "id",childColumns = "contactId",onDelete = ForeignKey.CASCADE))
public class Email {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String email;
    private long contactId;

    public Email(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }
}
