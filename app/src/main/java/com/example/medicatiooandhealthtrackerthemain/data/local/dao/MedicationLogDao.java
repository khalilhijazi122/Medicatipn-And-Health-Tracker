package com.example.medicatiooandhealthtrackerthemain.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.medicatiooandhealthtrackerthemain.data.local.entities.MedicationLog;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.PendingLogItem;

import java.util.List;

@Dao
public interface MedicationLogDao {

    @Insert
    long insert(MedicationLog log);

    // LiveData لعرض كل logs في RecyclerView

    @Query("SELECT * FROM medication_logs")
    List<MedicationLog> getAllLogsSync();
    @Query("SELECT * FROM medication_logs WHERE userId = :userId ORDER BY timestamp DESC")
    LiveData<List<MedicationLog>> getAllLogs(int userId);

    @Query("DELETE FROM medication_logs WHERE userId = :userId")
    int clearLogsForUser(int userId);

    // جلب الـ Pending Logs مع عمل Join مع جدول الأدوية
    @Query("SELECT ml.id AS logId, ml.medicationId AS medicationId, m.name AS name, " +
            "m.dosage AS dosage, m.hour AS hour, m.minute AS minute, " +
            "ml.timestamp AS timestamp, ml.status AS status " +
            "FROM medication_logs ml " +
            "JOIN medications m ON m.id = ml.medicationId " +
            "WHERE ml.userId = :userId AND ml.status = 'PENDING' " +
            "ORDER BY ml.timestamp DESC")
    LiveData<List<PendingLogItem>> getPendingLogs(int userId);

    // احصائيات
    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId AND status = 'TAKEN' AND timestamp BETWEEN :start AND :end")
    int countTakenBetween(int userId, long start, long end);

    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId AND status = 'MISSED' AND timestamp BETWEEN :start AND :end")
    int countMissedBetween(int userId, long start, long end);

    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId AND medicationId = :medId AND status = :status AND timestamp BETWEEN :start AND :end")
    int countStatusForMedicationBetween(int userId, int medId, String status, long start, long end);

    @Query("SELECT * FROM medication_logs WHERE userId = :userId AND medicationId = :medId ORDER BY timestamp DESC LIMIT 1")
    MedicationLog getLastLogForMedication(int userId, int medId);

    @Query("UPDATE medication_logs SET status = :newStatus, timestamp = :newTimestamp WHERE id = :logId")
    int updateStatus(int logId, String newStatus, long newTimestamp);

    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId AND status = 'TAKEN' AND timestamp BETWEEN :startTime AND :endTime")
    LiveData<Integer> getTakenCount(int userId, long startTime, long endTime);

    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId AND status = 'MISSED' AND timestamp BETWEEN :startTime AND :endTime")
    LiveData<Integer> getMissedCount(int userId, long startTime, long endTime);

    @Query("SELECT * FROM medication_logs WHERE id = :logId LIMIT 1")
    MedicationLog getLogById(int logId);

    @Query("SELECT * FROM medication_logs WHERE userId = :userId AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    LiveData<List<MedicationLog>> getLogsInRange(int userId, long startTime, long endTime);

    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId AND status = 'TAKEN' AND timestamp BETWEEN :startTime AND :endTime")
    int getTakenCountSync(int userId, long startTime, long endTime);

    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId AND status = 'MISSED' AND timestamp BETWEEN :startTime AND :endTime")
    int getMissedCountSync(int userId, long startTime, long endTime);
}