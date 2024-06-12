package com.g1.contactapp.utils;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.g1.contactapp.dao.AppointmentDao;
import com.g1.contactapp.dao.CategoryDao;
import com.g1.contactapp.dao.ContactCategoryDao;
import com.g1.contactapp.dao.ContactDao;
import com.g1.contactapp.dao.EmailDao;
import com.g1.contactapp.dao.NoteDao;
import com.g1.contactapp.dao.PhoneNumberDao;
import com.g1.contactapp.model.Appointment;
import com.g1.contactapp.model.Category;
import com.g1.contactapp.model.Contact;
import com.g1.contactapp.model.ContactCategory;
import com.g1.contactapp.model.Email;
import com.g1.contactapp.model.Note;
import com.g1.contactapp.model.PhoneNumber;

@Database(entities = {Contact.class, Email.class, Appointment.class, Category.class, Note.class, PhoneNumber.class, ContactCategory.class},version = 8)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract ContactDao getContactDao();
    public abstract EmailDao getEmailDao();
    public abstract PhoneNumberDao getPhoneNumberDao();
    public abstract CategoryDao getCategoryDao();
    public abstract ContactCategoryDao getContactCategoryDao();
    public abstract AppointmentDao getAppointmentDao();
    public abstract NoteDao getNoteDao();
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                            "contact_app").
                    allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
