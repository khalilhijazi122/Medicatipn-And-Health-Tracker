package com.example.medicatiooandhealthtrackerthemain.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.medicatiooandhealthtrackerthemain.data.local.entities.Medication;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface MedicationDao {

    @Insert
    long insert(Medication medication);

    @Update
    void update(Medication medication);

    @Insert
    void insertAll(Medication medication);

    @Delete
    int delete(Medication medication);

    @Query("SELECT * FROM medications WHERE userId = :userId ORDER BY hour, minute")
    LiveData<List<Medication>> getAllByUser(int userId);

    @Query("SELECT * FROM medications")
    LiveData<List<Medication>> getAllMedication();

    @Query("SELECT * FROM medications WHERE id = :id LIMIT 1")
    Medication getById(int id);

    @Query("UPDATE medications SET isActive = :active WHERE id = :id")
    int setActive(int id, boolean active);
}
