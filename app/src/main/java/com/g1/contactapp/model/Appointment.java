package com.g1.contactapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Contact.class,parentColumns = "id",childColumns = "contactId",onDelete = ForeignKey.CASCADE))
public class Appointment {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private long contactId;
    private String time;
    private String location;
    private String note;


    public Appointment(String time, String location, String note) {
        this.time = time;
        this.location = location;
        this.note = note;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public int getId() {
        return id;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


}