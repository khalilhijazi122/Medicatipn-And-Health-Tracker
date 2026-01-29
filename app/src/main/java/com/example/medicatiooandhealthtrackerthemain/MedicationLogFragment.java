package com.example.medicatiooandhealthtrackerthemain;

import android.os.Bundle;
import android.util.Log;
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

        // 1. تعريف العناصر وقاعدة البيانات
        RecyclerView rv = view.findViewById(R.id.rvPendingLogs);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = AppDatabase.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());
        int userId = sessionManager.getUserId();

        // 2. إعداد الـ Adapter مع الأكشن (Taken / Missed)
        adapter = new MedicationLogAdapter(new MedicationLogAdapter.OnLogAction() {
            @Override
            public void onTaken(PendingLogItem item) {
                processLogUpdate(item, "TAKEN");
            }

            @Override
            public void onMissed(PendingLogItem item) {
                processLogUpdate(item, "MISSED");
            }
        });
        rv.setAdapter(adapter);

        // 3. مراقبة البيانات الحية (LiveData)
        db.medicationLogDao().getPendingLogs(userId).observe(getViewLifecycleOwner(), pending -> {
            if (pending != null) {
                adapter.setItems(pending);
            }
        });

        // 4. توليد بيانات تجريبية إذا لزم الأمر
        generateTestDataIfNeeded(userId);

        return view;
    }

    private void processLogUpdate(PendingLogItem item, String status) {
        Executors.newSingleThreadExecutor().execute(() -> {
            long currentTime = System.currentTimeMillis();

            // تحديث حالة السجل في قاعدة البيانات
            db.medicationLogDao().updateStatus(item.logId, status, currentTime);
            Log.d("MEDICATION_LOG", "Updated " + item.logId + " to " + status);

            // إذا أخذ الدواء (TAKEN)، نجعله غير نشط (حسب منطقك)
            if ("TAKEN".equals(status)) {
                db.medicationDao().setInactive(item.medicationId);
            }

            // تحديث الواجهة برسالة بسيطة
            if (isAdded()) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Status: " + status, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void generateTestDataIfNeeded(int userId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // جلب أول دواء موجود للمستخدم لعمل تجربة
            var firstMed = db.medicationDao().getById(1);
            if (firstMed != null) {
                if (db.medicationLogDao().getLastLogForMedication(userId, firstMed.id) == null) {
                    MedicationLog testLog = new MedicationLog();
                    testLog.medicationId = firstMed.id;
                    testLog.userId = userId;
                    testLog.status = "PENDING";
                    testLog.timestamp = System.currentTimeMillis();
                    db.medicationLogDao().insert(testLog);
                }
            }
        });
    }
}