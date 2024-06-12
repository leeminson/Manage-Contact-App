package com.g1.contactapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.g1.contactapp.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    public void addCategory(Category category);
    @Query("Select * from category")
    public List<Category> getAllCategory();

}
