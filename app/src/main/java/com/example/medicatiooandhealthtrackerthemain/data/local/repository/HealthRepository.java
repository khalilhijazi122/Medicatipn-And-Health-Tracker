package com.example.medicatiooandhealthtrackerthemain.data.local.repository;
import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.dao.HealthRecordDao;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.HealthRecord;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HealthRepository {

    public HealthRecordDao healthRecordDao;
    private final ExecutorService executorService;

    public HealthRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        healthRecordDao = database.healthRecordDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // ==================== HEALTH RECORD OPERATIONS ====================

    /**
     * Get all health records for a user
     */
    public LiveData<List<HealthRecord>> getAllHealthRecords(String userId) {
        return (LiveData<List<HealthRecord>>) healthRecordDao.getAll(Integer.parseInt(userId));
    }

    /**
     * Get health records by type (SYMPTOM or VITAL_SIGN)
     */
    public LiveData<List<HealthRecord>> getRecordsByType(int userId, String type) {
        return (LiveData<List<HealthRecord>>) healthRecordDao.getByType(userId, type);
    }

    /**
     * Get health records in date range
     */
    public LiveData<List<HealthRecord>> getRecordsInRange(int userId, long startTime, long endTime) {
        return healthRecordDao.getRecordsInRange(userId, startTime, endTime);
    }

    /**
     * Get heart rate records
     */
    public LiveData<List<HealthRecord>> getHeartRateRecords(int userId, long startTime, long endTime) {
        return healthRecordDao.getVitalSignsByTypeInRange(userId, "Heart Rate", startTime, endTime);
    }

    /**
     * Get weight records
     */
    public LiveData<List<HealthRecord>> getWeightRecords(int userId, long startTime, long endTime) {
        return healthRecordDao.getVitalSignsByTypeInRange(userId, "Weight", startTime, endTime);
    }

    /**
     * Get symptom records
     */
    public LiveData<List<HealthRecord>> getSymptoms(int userId, long startTime, long endTime) {
        return healthRecordDao.getSymptomsInRange(userId, startTime, endTime);
    }

    /**
     * Insert health record
     */
    public void insertHealthRecord(HealthRecord healthRecord) {
        executorService.execute(() -> {
            healthRecordDao.insert(healthRecord);
        });
    }

    /**
     * Update health record
     */
    public void updateHealthRecord(HealthRecord healthRecord) {
        executorService.execute(() -> {
            healthRecordDao.update(healthRecord);
        });
    }

    /**
     * Delete health record
     */
    public void deleteHealthRecord(HealthRecord healthRecord) {
        executorService.execute(() -> {
            healthRecordDao.delete(healthRecord);
        });
    }
}