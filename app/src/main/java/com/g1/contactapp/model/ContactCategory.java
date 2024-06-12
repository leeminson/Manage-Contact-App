package com.g1.contactapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(primaryKeys = {"contactId", "categoryId"},
        foreignKeys = {@ForeignKey(entity = Contact.class,parentColumns = "id",childColumns = "contactId",onDelete = ForeignKey.CASCADE),
                       @ForeignKey(entity = Category.class,parentColumns = "id",childColumns = "categoryId",onDelete = ForeignKey.CASCADE)}
)
public class ContactCategory {
    public long contactId;
    public int categoryId;

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
