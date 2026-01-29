package com.example.medicatiooandhealthtrackerthemain;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.Medication;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.MedicationLog;
import com.example.medicatiooandhealthtrackerthemain.utils.Prefs;
import com.example.medicatiooandhealthtrackerthemain.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private SessionManager sessionManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable checkerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // إعداد الوضع الليلي
        if (Prefs.isDarkMode(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        // إعداد Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // إعداد الداتابيز والـ Session
        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        // تشغيل فاحص المواعيد
        startDueChecker();
    }

    private void startDueChecker() {
        checkerRunnable = new Runnable() {
            @Override
            public void run() {
                int userId = sessionManager.getUserId();
                if (userId != -1) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        checkDueMedications(userId);
                    });
                }
                handler.postDelayed(this, 30_000); // فحص كل 30 ثانية
            }
        };
        handler.post(checkerRunnable);
    }

    private void checkDueMedications(int userId) {
        // جلب الأدوية النشطة للمستخدم الحالي فقط
        List<Medication> meds = db.medicationDao().getActiveMedicationsSync(userId);

        Calendar now = Calendar.getInstance();
        int currentH = now.get(Calendar.HOUR_OF_DAY);
        int currentM = now.get(Calendar.MINUTE);

        long startDay = getStartOfDayMillis();
        long endDay = getEndOfDayMillis();

        for (Medication med : meds) {
            // إذا تطابق الوقت (ساعة ودقيقة)
            if (med.hour == currentH && med.minute == currentM) {

                // التأكد من عدم وجود Log مسجل مسبقاً لهذا الدواء اليوم
                int already = db.medicationLogDao().countStatusForMedicationBetween(
                        userId, med.id, "PENDING", startDay, endDay);

                if (already == 0) {
                    MedicationLog log = new MedicationLog();
                    log.userId = userId;
                    log.medicationId = med.id;
                    log.timestamp = System.currentTimeMillis();
                    log.status = "PENDING";

                    db.medicationLogDao().insert(log);
                    Log.d("Checker", "Created pending log for: " + med.name);
                }
            }
        }
    }

    private long getStartOfDayMillis() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private long getEndOfDayMillis() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTimeInMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (checkerRunnable != null) handler.removeCallbacks(checkerRunnable);
    }
}