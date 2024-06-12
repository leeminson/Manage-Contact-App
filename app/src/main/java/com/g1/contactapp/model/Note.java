package com.g1.contactapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(foreignKeys = @ForeignKey(entity = Contact.class,parentColumns = "id",childColumns = "contactId",onDelete = ForeignKey.CASCADE))
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String label;
    private String content;
    private long contactId;

    public Note(String label, String content) {
        this.label = label;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }
}
