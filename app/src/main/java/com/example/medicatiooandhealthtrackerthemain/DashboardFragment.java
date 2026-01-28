package com.example.medicatiooandhealthtrackerthemain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.Medication;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.User;
import com.example.medicatiooandhealthtrackerthemain.utils.SessionManager;

import java.util.List;

public class DashboardFragment extends Fragment {

    TextView tvWelcome, tvMedicationCount, tvHealthCount, tvNextDose;
    AppDatabase db;
    SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashbord, container, false);

        // 1. ربط العناصر من الـ XML
        View healthSection = view.findViewById(R.id.layoutHealthSection);
        Button btnGoToHealth = view.findViewById(R.id.btnGoToHealth);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvMedicationCount = view.findViewById(R.id.tvMedicationCount);
        tvHealthCount = view.findViewById(R.id.tvHealthCount);
        tvNextDose = view.findViewById(R.id.tvNextDose);

        // 2. تجهيز الداتابيز والـ Session
        sessionManager = new SessionManager(requireContext());
        db = AppDatabase.getInstance(requireContext());

        // 3. برمجة أكشن الضغط (داخل onCreateView)
        View.OnClickListener openHealthAction = v -> {
            androidx.navigation.Navigation.findNavController(v)
                    .navigate(R.id.action_dashboardFragment_to_healthTrackerFragment);
        };

        // تفعيل الضغط على الكرت وعلى الزر
        if (healthSection != null) healthSection.setOnClickListener(openHealthAction);
        if (btnGoToHealth != null) btnGoToHealth.setOnClickListener(openHealthAction);

        // 4. تحميل البيانات
        loadDashboardData();

        return view; // سطر الـ return يجب أن يكون الأخير دائماً
    }

    private void loadDashboardData() {
        int userId = sessionManager.getUserId();

        // 1. مراقبة عدد السجلات الصحية (Health Records)
        db.healthRecordDao().getCount(userId).observe(getViewLifecycleOwner(), count -> {
            if (isAdded() && count != null) {
                tvHealthCount.setText(count + " records");
            }
        });

        // 2. مراقبة الأدوية وحساب الموعد القادم
        db.medicationDao().getAllByUser(userId).observe(getViewLifecycleOwner(), meds -> {
            if (isAdded() && meds != null) {
                tvMedicationCount.setText(meds.size() + " medications");

                if (meds.isEmpty()) {
                    tvNextDose.setText("No medications added");
                } else {
                    updateNextDoseUI(meds);
                }
            }
        });

        // 3. جلب بيانات المستخدم للترحيب
        new Thread(() -> {
            User user = db.userDao().findById(userId);
            if (getActivity() != null) {
                requireActivity().runOnUiThread(() -> {
                    if (isAdded() && user != null) {
                        tvWelcome.setText("Welcome, " + user.name);
                    }
                });
            }
        }).start();
    }

    // دالة لحساب أقرب موعد دواء
    private void updateNextDoseUI(List<Medication> meds) {
        java.util.Calendar now = java.util.Calendar.getInstance();
        int currentHour = now.get(java.util.Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(java.util.Calendar.MINUTE);

        Medication nextMed = null;

        for (Medication m : meds) {
            if (m.isActive) {
                if (m.hour > currentHour || (m.hour == currentHour && m.minute > currentMinute)) {
                    nextMed = m;
                    break;
                }
            }
        }

        if (nextMed != null) {
            tvNextDose.setText("Next: " + nextMed.name + " at " + formatTime(nextMed.hour, nextMed.minute));
        } else if (!meds.isEmpty()) {
            Medication tomorrowMed = meds.get(0);
            tvNextDose.setText("Tomorrow: " + tomorrowMed.name + " at " + formatTime(tomorrowMed.hour, tomorrowMed.minute));
        }
    }


    private String formatTime(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }
}