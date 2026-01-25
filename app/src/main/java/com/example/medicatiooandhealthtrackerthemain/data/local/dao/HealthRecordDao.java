package com.example.medicatiooandhealthtrackerthemain.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.medicatiooandhealthtrackerthemain.data.local.entities.HealthRecord;

import java.util.List;

@Dao
public interface HealthRecordDao {

    @Insert
    long insert(HealthRecord record);

    @Update
    default long update(HealthRecord record) {
        return 0;
    }

    @Delete
    int delete(HealthRecord record);

    @Query("SELECT * FROM health_records WHERE userId = :userId ORDER BY timestamp DESC")
    List<HealthRecord> getAll(int userId);

    @Query("SELECT * FROM health_records WHERE userId = :userId AND type = :type ORDER BY timestamp DESC")
    List<HealthRecord> getByType(int userId, String type);

    @Query("SELECT * FROM health_records WHERE userId = :userId AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    LiveData<List<HealthRecord>> getRecordsInRange(String userId, long startTime, long endTime);

    @Query("SELECT * FROM health_records WHERE userId = :userId " +
            "AND type = 'VITAL_SIGN' " +
            "AND timestamp BETWEEN :startTime AND :endTime " +
            "ORDER BY timestamp ASC")
    default LiveData<List<HealthRecord>> getVitalSignsByTypeInRange(
            String userId, String vitalType, long startTime, long endTime) {
        return null;
    }

    @Query("SELECT * FROM health_records WHERE userId = :userId " +
            "AND type = 'SYMPTOM' " +
            "AND timestamp BETWEEN :startTime AND :endTime " +
            "ORDER BY timestamp DESC")
    LiveData<List<HealthRecord>> getSymptomsInRange(
            String userId, long startTime, long endTime);
}
