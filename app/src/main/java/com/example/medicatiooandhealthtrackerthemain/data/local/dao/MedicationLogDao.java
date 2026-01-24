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
    @Query("SELECT * FROM medication_logs WHERE userId = :userId ORDER BY timestamp DESC")
    LiveData<List<MedicationLog>> getAllLogs(int userId);

    // مسح logs للمستخدم
    @Query("DELETE FROM medication_logs WHERE userId = :userId")
    int clearLogsForUser(int userId);

    @Query("SELECT ml.id AS logId, ml.medicationId AS medicationId, m.name AS name, m.dosage AS dosage, m.hour AS hour, m.minute AS minute, ml.timestamp AS timestamp " +
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

    // ✅ جديد: عدد status لدواء معيّن بين وقتين (مفيد للتنبيهات)
    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId AND medicationId = :medId AND status = :status AND timestamp BETWEEN :start AND :end")
    int countStatusForMedicationBetween(int userId, int medId, String status, long start, long end);

    // ✅ جديد: آخر log لدواء معين (مفيد لتقرر إذا تنبه أو لا)
    @Query("SELECT * FROM medication_logs WHERE userId = :userId AND medicationId = :medId ORDER BY timestamp DESC LIMIT 1")
    MedicationLog getLastLogForMedication(int userId, int medId);

    @Query("UPDATE medication_logs SET status = :newStatus, timestamp = :newTimestamp WHERE id = :logId")
    int updateStatus(int logId, String newStatus, long newTimestamp);

    @Query("SELECT COUNT(*) FROM medication_logs " +
            "WHERE userId = :userId AND medicationId = :medId AND status = 'PENDING' " +
            "AND timestamp BETWEEN :startDay AND :endDay")
    int countPendingToday(int userId, int medId, long startDay, long endDay);
}
