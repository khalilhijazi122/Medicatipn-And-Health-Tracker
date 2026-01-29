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
        // استخدام ThreadPool لإدارة العمليات في الخلفية بكفاءة
        executorService = Executors.newFixedThreadPool(4);
    }

    // ==================== MEDICATION OPERATIONS ====================

    public LiveData<List<Medication>> getAllMedications(int userId) {
        return medicationDao.getActiveMedications(userId);
    }

    public void insertMedication(Medication medication) {
        executorService.execute(() -> medicationDao.insert(medication));
    }

    public void updateMedication(Medication medication) {
        executorService.execute(() -> medicationDao.update(medication));
    }

    public void deleteMedication(Medication medication) {
        executorService.execute(() -> medicationDao.delete(medication));
    }

    public void getMedicationById(int medicationId, RepositoryCallback<Medication> callback) {
        executorService.execute(() -> {
            Medication med = medicationDao.getById(medicationId);
            callback.onComplete(med);
        });
    }

    // ==================== MEDICATION LOG OPERATIONS ====================

    public LiveData<List<MedicationLog>> getLogsInRange(int userId, long startTime, long endTime) {
        return medicationLogDao.getLogsInRange(userId, startTime, endTime);
    }

    public void insertLog(MedicationLog log) {
        executorService.execute(() -> medicationLogDao.insert(log));
    }

    public void updateLogStatus(int logId, String status, long timestamp) {
        executorService.execute(() -> medicationLogDao.updateStatus(logId, status, timestamp));
    }

    public LiveData<Integer> getTakenCount(int userId, long startTime, long endTime) {
        return medicationLogDao.getTakenCount(userId, startTime, endTime);
    }

    public LiveData<Integer> getMissedCount(int userId, long startTime, long endTime) {
        return medicationLogDao.getMissedCount(userId, startTime, endTime);
    }

    // Callback للحصول على نتائج من خيوط الخلفية (Background Threads)
    public interface RepositoryCallback<T> {
        void onComplete(T result);
    }
}