package com.demo.carservicetracker2.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.demo.carservicetracker2.database.models.DbVersionModel;

@Dao
public interface DbVersionDAO {
    @Delete
    void delete(DbVersionModel rowModel);
    @Query("DELETE FROM DbVersionModel")
    void deleteAll();
    @Insert
    long insert(DbVersionModel rowModel);
    @Update
    void update(DbVersionModel rowModel);
}
