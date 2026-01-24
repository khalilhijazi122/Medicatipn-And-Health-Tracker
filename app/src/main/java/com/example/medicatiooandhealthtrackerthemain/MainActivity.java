package com.example.medicatiooandhealthtrackerthemain;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.Medication;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.MedicationLog;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.User;
import com.example.medicatiooandhealthtrackerthemain.utils.Prefs;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Prefs.isDarkMode(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.DashboardFragment,
                        R.id.MedicationListFragment,R.id.MedicationLogFragment
                        ,R.id.ReportFragment,R.id.ProfileFragment).build();
       // NavHostFragment navHostFragment =
             //   (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        //if (navHostFragment == null) {
           // throw new RuntimeException("nav_host_fragment not found in activity_main.xml");
       // }

       NavController navController;
        navController = Navigation.findNavController(MainActivity.this,R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(bottomNav, navController);
        User u = new User();
         // أو autoGenerate حسب تعريفك
        u.name = "test";

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "medication_db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries() // مؤقتاً للتعلم
                .build();

        startDueChecker(db);




        new Thread(() -> {
            db.userDao().insert(u);
        }).start();


    }

    private final int USER_ID = 1;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable checkerRunnable;

    private void startDueChecker(AppDatabase db) {
        checkerRunnable = new Runnable() {
            @Override
            public void run() {
                Executors.newSingleThreadExecutor().execute(() -> {
                    checkDueMedications(db, USER_ID);
                });
                handler.postDelayed(this, 30_000); // كل 30 ثانية
            }
        };
        handler.post(checkerRunnable);
    }

    private void stopDueChecker() {
        if (checkerRunnable != null) handler.removeCallbacks(checkerRunnable);
    }

    private void checkDueMedications(AppDatabase db, int userId) {
        List<Medication> meds = db.medicationDao().getActiveMedications(userId);

        Calendar now = Calendar.getInstance();
        int h = now.get(Calendar.HOUR_OF_DAY);
        int m = now.get(Calendar.MINUTE);

        long startDay = getStartOfDayMillis();
        long endDay = getEndOfDayMillis();

        for (Medication med : meds) {
            if (med.hour == h && med.minute == m) {

                int already = db.medicationLogDao().countPendingToday(userId, med.id, startDay, endDay);
                if (already > 0) continue;

                MedicationLog log = new MedicationLog();
                log.userId = userId;
                log.medicationId = med.id;
                log.timestamp = System.currentTimeMillis();
                log.status = "PENDING";

                db.medicationLogDao().insert(log);
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
        stopDueChecker();
    }

}
