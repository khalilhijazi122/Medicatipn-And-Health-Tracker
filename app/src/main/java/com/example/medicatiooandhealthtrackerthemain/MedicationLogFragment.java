package com.example.medicatiooandhealthtrackerthemain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.MedicationLog;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.PendingLogItem;
import com.example.medicatiooandhealthtrackerthemain.utils.SessionManager;

import java.util.concurrent.Executors;

public class MedicationLogFragment extends Fragment {

    private AppDatabase db;
    private MedicationLogAdapter adapter;
    private SessionManager sessionManager;

    public MedicationLogFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_medication_log, container, false);

        // 1. إعداد RecyclerView
        RecyclerView rv = view.findViewById(R.id.rvPendingLogs);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = AppDatabase.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        // 2. إعداد الـ Adapter (تعديل الحالة Taken/Missed)
        adapter = new MedicationLogAdapter(new MedicationLogAdapter.OnLogAction() {
            @Override
            public void onTaken(PendingLogItem item) {
                updateLogStatus(item.logId, "TAKEN");
            }

            @Override
            public void onMissed(PendingLogItem item) {
                updateLogStatus(item.logId, "MISSED");
            }
        });
        rv.setAdapter(adapter);

        // 3. جلب الـ ID الحقيقي للمستخدم
        int userId = sessionManager.getUserId();

        // 4. مراقبة البيانات (الالتزام بالـ DAO الخاص بك)
        db.medicationLogDao().getPendingLogs(userId).observe(getViewLifecycleOwner(), pending -> {
            if (pending != null) {
                adapter.setItems(pending);
            }
        });

        // 5. توليد بيانات تجريبية (فقط إذا كان المستخدم يملك أدوية)
        generateTestDataIfNeeded(userId);

        return view;
    }

    private void generateTestDataIfNeeded(int userId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // جلب أول دواء يملكه المستخدم لربطه بالـ Log
            // ملاحظة: تأكد من وجود دالة getFirstMedication في MedicationDao
            var firstMed = db.medicationDao().getById(1); // مثال لربطه بدواء ID = 1

            if (firstMed != null) {
                // التأكد من عدم وجود Log معلق اليوم لهذا الدواء لتجنب التكرار
                if (db.medicationLogDao().getLastLogForMedication(userId, firstMed.id) == null) {
                    MedicationLog testLog = new MedicationLog();
                    testLog.medicationId = firstMed.id;
                    testLog.userId = userId; // ✅ ضروري جداً لكي ينجح الـ JOIN في الـ DAO
                    testLog.status = "PENDING";
                    testLog.timestamp = System.currentTimeMillis();

                    db.medicationLogDao().insert(testLog);
                }
            }
        });
    }

    private void updateLogStatus(int logId, String status) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // استخدام ميثود الـ UPDATE من الـ DAO الخاص بك
            db.medicationLogDao().updateStatus(logId, status, System.currentTimeMillis());

            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Status: " + status, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}