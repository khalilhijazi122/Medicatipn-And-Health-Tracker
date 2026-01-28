package com.example.medicatiooandhealthtrackerthemain.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.medicatiooandhealthtrackerthemain.data.local.entities.HealthRecord;

import java.util.List;

@Dao
public interface HealthRecordDao {

    @Insert
    long insert(HealthRecord record);

    @Delete
    int delete(HealthRecord record);

    @Query("SELECT * FROM health_records WHERE userId = :userId ORDER BY timestamp DESC")
    LiveData<List<HealthRecord>> getAll(int userId);

    @Query("SELECT * FROM health_records WHERE userId = :userId AND type = :type ORDER BY timestamp DESC")
    List<HealthRecord> getByType(int userId, String type);

    @Query("SELECT COUNT(*) FROM health_records WHERE userId = :userId")
    LiveData<Integer> getCount(int userId);

}
