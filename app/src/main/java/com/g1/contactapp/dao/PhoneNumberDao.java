package com.g1.contactapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.g1.contactapp.model.PhoneNumber;

import java.util.List;

@Dao
public interface PhoneNumberDao {
    @Insert
    public void addPhoneNumber(PhoneNumber phoneNumber);
    @Delete
    public void deletePhoneNumber(PhoneNumber phoneNumber);
    @Update
    public void updatePhoneNumber(PhoneNumber phoneNumber);
    @Query("Select * from phonenumber where id=:id")
    public PhoneNumber getPhoneNumber(int id);
    @Query("Select * from phonenumber where contactId=:contact_id")
    public List<PhoneNumber> getPhoneNumberbyContact(long contact_id);
}
