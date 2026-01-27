package com.example.medicatiooandhealthtrackerthemain.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.medicatiooandhealthtrackerthemain.data.local.entities.MedicationLog;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.PendingLogItem;

import java.util.List;

@Dao
public interface MedicationLogDao {

    @Insert
    long insert(MedicationLog log);

    @Update
    default long update(MedicationLog log) {
        return 0;
    }

    // LiveData لعرض كل logs في RecyclerView
    @Query("SELECT * FROM medication_logs WHERE userId = :userId ORDER BY timestamp DESC")
    LiveData<List<MedicationLog>> getAllLogs(String userId);

    // مسح logs للمستخدم
    @Query("DELETE FROM medication_logs WHERE userId = :userId")
    int clearLogsForUser(String userId);

    @Query("SELECT ml.id AS logId, ml.medicationId AS medicationId, m.name AS name, m.dosage AS dosage, m.hour AS hour, m.minute AS minute, ml.timestamp AS timestamp " +
            "FROM medication_logs ml " +
            "JOIN medications m ON m.id = ml.medicationId " +
            "WHERE ml.userId = :userId AND ml.status = 'PENDING' " +
            "ORDER BY ml.timestamp DESC")
    LiveData<List<PendingLogItem>> getPendingLogs(String userId);


    // احصائيات
    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId AND status = 'TAKEN' AND timestamp BETWEEN :start AND :end")
    int countTakenBetween(String userId, long start, long end);

    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId AND status = 'MISSED' AND timestamp BETWEEN :start AND :end")
    int countMissedBetween(String userId, long start, long end);

    // ✅ جديد: عدد status لدواء معيّن بين وقتين (مفيد للتنبيهات)
    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId AND medicationId = :medId AND status = :status AND timestamp BETWEEN :start AND :end")
    int countStatusForMedicationBetween(String userId, int medId, String status, long start, long end);

    // ✅ جديد: آخر log لدواء معين (مفيد لتقرر إذا تنبه أو لا)
    @Query("SELECT * FROM medication_logs WHERE userId = :userId AND medicationId = :medId ORDER BY timestamp DESC LIMIT 1")
    MedicationLog getLastLogForMedication(String userId, int medId);

    @Query("UPDATE medication_logs SET status = :newStatus, timestamp = :newTimestamp WHERE id = :logId")
    int updateStatus(int logId, String newStatus, long newTimestamp);

    @Query("SELECT COUNT(*) FROM medication_logs " +
            "WHERE userId = :userId AND medicationId = :medId AND status = 'PENDING' " +
            "AND timestamp BETWEEN :startDay AND :endDay")
    int countPendingToday(String userId, int medId, long startDay, long endDay);
    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId " +
            "AND timestamp BETWEEN :startTime AND :endTime " +
            "AND status = 'TAKEN'")
    LiveData<Integer> getTakenCount(String userId, long startTime, long endTime);

    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId " +
            "AND timestamp BETWEEN :startTime AND :endTime " +
            "AND status = 'MISSED'")
    LiveData<Integer> getMissedCount(String userId, long startTime, long endTime);

    @Query("SELECT * FROM medication_logs WHERE userId = :userId AND DATE(timestamp/1000, 'unixepoch') = DATE(:date/1000, 'unixepoch')")
    LiveData<List<MedicationLog>> getLogsForDate(String userId, long date);

    @Query("SELECT * FROM medication_logs WHERE medicationId = :medicationId ORDER BY timestamp DESC")
    LiveData<List<MedicationLog>> getLogsForMedication(String medicationId);

    @Query("SELECT * FROM medication_logs WHERE userId = :userId " +
            "AND timestamp BETWEEN :startTime AND :endTime")
    LiveData<List<MedicationLog>> getLogsInRange(String userId, long startTime, long endTime);

    @Query("SELECT * FROM medication_logs")
    List<MedicationLog> getAllLogsSync();

    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId " +
            "AND timestamp BETWEEN :startTime AND :endTime " +
            "AND status = 'TAKEN'")
    int getTakenCountSync(String userId, long startTime, long endTime);

    @Query("SELECT COUNT(*) FROM medication_logs WHERE userId = :userId " +
            "AND timestamp BETWEEN :startTime AND :endTime " +
            "AND status = 'MISSED'")
    int getMissedCountSync(String userId, long startTime, long endTime);

    @Query("SELECT * FROM medication_logs WHERE id = :logId LIMIT 1")
    MedicationLog getLogById(int logId);

}
