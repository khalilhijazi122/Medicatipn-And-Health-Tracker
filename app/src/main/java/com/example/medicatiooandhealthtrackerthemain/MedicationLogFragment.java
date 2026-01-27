package com.example.medicatiooandhealthtrackerthemain;

import static com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase.*;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.Medication;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.MedicationLog;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.PendingLogItem;

import java.util.concurrent.Executors;

public class MedicationLogFragment extends Fragment {

    private AppDatabase db;
    private MedicationLogAdapter adapter;

    private final int USER_ID = 1;

    public MedicationLogFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_medication_log, container, false);

        RecyclerView rv = view.findViewById(R.id.rvPendingLogs);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = getInstance(requireContext());

        adapter = new MedicationLogAdapter(new MedicationLogAdapter.OnLogAction() {
            @Override
            public void onTaken(PendingLogItem item) {
                Log.d("MEDICATION_LOG", "=== MARKING AS TAKEN ===");
                Log.d("MEDICATION_LOG", "Log ID: " + item.logId);
                Log.d("MEDICATION_LOG", "Medication ID: " + item.medicationId);
                Executors.newSingleThreadExecutor().execute(() -> {
                    // 1) update status -> TAKEN
                    db.medicationLogDao().updateStatus(item.logId, "TAKEN", System.currentTimeMillis());
                    Log.d("MEDICATION_LOG", "Updated to TAKEN successfully");
                    MedicationLog updatedLog = db.medicationLogDao().getLogById(item.logId);
                    if (updatedLog != null) {
                        Log.d("MEDICATION_LOG", "Verified - userId: '" + updatedLog.userId + "', status: '" + updatedLog.status + "'");
                    }
                    // 2) remove from medication list -> setInactive
                    db.medicationDao().setInactive(item.medicationId);
                });
            }

            @Override
            public void onMissed(PendingLogItem item) {
                Log.d("MEDICATION_LOG", "=== MARKING AS MISSED ===");
                Log.d("MEDICATION_LOG", "Log ID: " + item.logId);
                Executors.newSingleThreadExecutor().execute(() -> {
                    // MISSED: بس نشيل من log (بإخفائه من pending)
                    db.medicationLogDao().updateStatus(item.logId, "MISSED", System.currentTimeMillis());
                    Log.d("MEDICATION_LOG", "Updated to MISSED successfully");

                    // Verify the update
                    MedicationLog updatedLog = db.medicationLogDao().getLogById(item.logId);
                    // لا نلمس medication (يبقى active)
                    if (updatedLog != null) {
                        Log.d("MEDICATION_LOG", "Verified - userId: '" + updatedLog.userId + "', status: '" + updatedLog.status + "'");
                    }
                });
            }
        });



                rv.setAdapter(adapter);

                db.medicationLogDao().getPendingLogs(String.valueOf(USER_ID))
                        .observe(getViewLifecycleOwner(), pending -> adapter.setItems(pending));
                return view;
    }
}