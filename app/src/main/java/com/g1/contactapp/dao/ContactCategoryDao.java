package com.g1.contactapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.g1.contactapp.model.Category;
import com.g1.contactapp.model.ContactCategory;

import java.util.List;

@Dao
public interface ContactCategoryDao {
    @Insert
    void add(ContactCategory contactCategory);
    @Update
    void update(ContactCategory contactCategory);
    @Delete
    void delete(ContactCategory contactCategory);
    @Query("SELECT * FROM ContactCategory WHERE categoryId = :categoryId AND contactId = :contactId")
    ContactCategory getContactCategoryByCategoryIdAndContactId( long contactId,int categoryId);
    @Query("SELECT Category.* FROM Category " +
            "INNER JOIN ContactCategory ON Category.id = ContactCategory.categoryId " +
            "WHERE ContactCategory.contactId = :contactId")
    List<Category> getCategoriesByContactId(long contactId);
}
