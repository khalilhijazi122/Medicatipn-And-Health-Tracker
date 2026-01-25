package com.example.medicatiooandhealthtrackerthemain.data.local.repository;
import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.dao.MedicationDao;
import com.example.medicatiooandhealthtrackerthemain.data.local.dao.MedicationLogDao;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.Medication;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.MedicationLog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MedicationRepository {

    private MedicationDao medicationDao;
    private MedicationLogDao medicationLogDao;
    private ExecutorService executorService;

    public MedicationRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        medicationDao = database.medicationDao();
        medicationLogDao = database.medicationLogDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // ==================== MEDICATION OPERATIONS ====================

    /**
     * Get all active medications for a user
     */
    public LiveData<List<Medication>> getAllMedications(String userId) {
        return medicationDao.getActiveMedications(userId);
    }


    /**
     * Get single medication by ID
     */
    public Medication getMedicationById(String medicationId) {
        return medicationDao.getById(Integer.parseInt(medicationId));
    }

    /**
     * Insert new medication
     */
    public void insertMedication(Medication medication) {
        executorService.execute(() -> {
            medicationDao.insert(medication);
        });
    }

    /**
     * Update existing medication
     */
    public void updateMedication(Medication medication) {
        executorService.execute(() -> {
            medicationDao.update(medication);
        });
    }

    /**
     * Delete medication
     */
    public void deleteMedication(Medication medication) {
        executorService.execute(() -> {
            medicationDao.delete(medication);
        });
    }

    // ==================== MEDICATION LOG OPERATIONS ====================

    /**
     * Get medication logs for a specific date
     */
    public LiveData<List<MedicationLog>> getLogsForDate(String userId, long date) {
        return medicationLogDao.getLogsForDate(userId, date);
    }

    /**
     * Get logs for specific medication
     */
    public LiveData<List<MedicationLog>> getLogsForMedication(String medicationId) {
        return medicationLogDao.getLogsForMedication(medicationId);
    }

    /**
     * Get logs in date range for adherence calculation
     */
    public LiveData<List<MedicationLog>> getLogsInRange(String userId, long startTime, long endTime) {
        return medicationLogDao.getLogsInRange(userId, startTime, endTime);
    }

    /**
     * Insert medication log
     */
    public void insertLog(MedicationLog log) {
        executorService.execute(() -> {
            medicationLogDao.insert(log);
        });
    }

    /**
     * Update medication log (mark as taken, skipped, etc.)
     */
    public void updateLog(MedicationLog log) {
        executorService.execute(() -> {
            medicationLogDao.update(log);
        });
    }

    /**
     * Get count of taken medications in range
     */
    public LiveData<Integer> getTakenCount(String userId, long startTime, long endTime) {
        return medicationLogDao.getTakenCount(userId, startTime, endTime);
    }

    /**
     * Get count of missed medications in range
     */
    public LiveData<Integer> getMissedCount(String userId, long startTime, long endTime) {
        return medicationLogDao.getMissedCount(userId, startTime, endTime);
    }
}