package com.g1.contactapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.g1.contactapp.model.Note;

import java.util.List;

@Dao
public interface NoteDao {
@Insert
    public void addNote(Note note);
@Delete
    public void deleteNote(Note note);
@Update
    public void updateNote(Note note);
@Query("Select * from note where id=:id")
    public Note getNote(int id);
    @Query("SELECT * FROM note WHERE contactId = :contact_id ORDER BY label")
    public List<Note> getNotebyContact(long contact_id);
}
