package com.example.medicatiooandhealthtrackerthemain;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.medicatiooandhealthtrackerthemain.data.local.entities.HealthRecord;
import com.example.medicatiooandhealthtrackerthemain.data.local.repository.HealthRepository;
import com.example.medicatiooandhealthtrackerthemain.data.local.repository.MedicationRepository;

import java.util.List;

public class ReportsViewModel extends AndroidViewModel {

    private MedicationRepository medicationRepository;
    private HealthRepository healthRepository;
    private String currentUserId; // Get from SharedPreferences or session manager

    public ReportsViewModel(@NonNull Application application) {
        super(application);
        medicationRepository = new MedicationRepository(application);
        healthRepository = new HealthRepository(application);

        // TODO: Get actual user ID from session/preferences
        currentUserId = "user123";
    }

    /**
     * Get adherence data for time period
     */
    public LiveData<AdherenceData> getAdherenceData(long startTime, long endTime) {
        MediatorLiveData<AdherenceData> result = new MediatorLiveData<>();

        // Get taken count
        LiveData<Integer> takenCount = medicationRepository.getTakenCount(currentUserId, startTime, endTime);

        // Get missed count
        LiveData<Integer> missedCount = medicationRepository.getMissedCount(currentUserId, startTime, endTime);

        // Combine both counts
        result.addSource(takenCount, taken -> {
            Integer missed = missedCount.getValue();
            if (missed != null) {
                result.setValue(new AdherenceData(taken, missed));
            }
        });

        result.addSource(missedCount, missed -> {
            Integer taken = takenCount.getValue();
            if (taken != null) {
                result.setValue(new AdherenceData(taken, missed));
            }
        });

        return result;
    }

    /**
     * Get symptom data
     */
    public LiveData<List<HealthRecord>> getSymptomData(long startTime, long endTime) {
        return healthRepository.getSymptoms(currentUserId, startTime, endTime);
    }
}
