package com.g1.contactapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.g1.contactapp.model.Email;

import java.util.List;
@Dao
public interface EmailDao {
    @Insert
    public void addEmail(Email e);
    @Update
    public void updateEmail(Email e);
    @Delete
    public void deleteEmail(Email e);
    @Query("Select * from email where id=:id")
    public Email getEmail(int id);
    @Query("Select * from email where contactId=:contact_id")
    public List<Email> getEmailbyContact(long contact_id);
}
