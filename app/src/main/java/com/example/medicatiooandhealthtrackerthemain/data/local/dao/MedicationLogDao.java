package com.example.medicatiooandhealthtrackerthemain.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.medicatiooandhealthtrackerthemain.data.local.entities.MedicationLog;

import java.util.List;

@Dao
public interface MedicationLogDao {

    @Insert
    long insert(MedicationLog log);

    @Query("SELECT * FROM medication_logs WHERE userId = :userId ORDER BY timestamp DESC")
    List<MedicationLog> getAllLogs(int userId);

    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId AND status = 'TAKEN' AND timestamp BETWEEN :start AND :end")
    int countTakenBetween(int userId, long start, long end);

    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId AND status = 'MISSED' AND timestamp BETWEEN :start AND :end")
    int countMissedBetween(int userId, long start, long end);

    @Query("DELETE FROM medication_logs WHERE userId = :userId")
    int clearLogsForUser(int userId);
}
