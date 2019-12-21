package com.example.lordbramasta.praktikum;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface eventDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertEvent(dataEvent data);

    @Update
    int updateEvent(dataEvent data);

    @Query("SELECT * FROM tbEvent")
    public List<dataEvent> getAllItem();

    @Query("DELETE FROM tbEvent")
    public void delete();
//    @Query("SELECT nama_event from tbEvent")
//    dataEvent selectNamaEvent();

}
