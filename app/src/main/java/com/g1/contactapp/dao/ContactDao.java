package com.g1.contactapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.g1.contactapp.model.Contact;

import java.util.List;

@Dao
public interface ContactDao {
    @Insert
    public long addContact(Contact contact);
    @Update
    public void updateContact(Contact contact);
    @Delete
    public void deleteContact(Contact contact);
    @Query("SELECT * FROM contact ORDER BY name")
    public List<Contact> getAllContact();
    @Query("Select * from contact where id=:id")
    public Contact getContact(long id);
    @Query("SELECT * from contact where name=:name")
    public Contact getContactByName(String name);
}
