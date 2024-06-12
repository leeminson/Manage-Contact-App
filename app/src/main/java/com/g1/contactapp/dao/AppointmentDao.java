package com.g1.contactapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.g1.contactapp.model.Appointment;

import java.util.List;

@Dao
public interface AppointmentDao {
    @Insert
    public long addAppointment(Appointment appointment);

    @Update
    public void updateAppointment(Appointment appointment);

    @Delete
    public void deleteAppointment(Appointment appointment);
    @Query("SELECT * from appointment ORDER BY time")
    public List<Appointment> getAllAppointment();
    @Query("SELECT * from appointment where id=:id")
    public Appointment getAppointment(long id);
}
