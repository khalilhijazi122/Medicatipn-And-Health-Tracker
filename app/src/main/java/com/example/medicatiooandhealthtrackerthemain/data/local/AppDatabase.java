package com.example.medicatiooandhealthtrackerthemain.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.medicatiooandhealthtrackerthemain.data.local.dao.HealthRecordDao;
import com.example.medicatiooandhealthtrackerthemain.data.local.dao.MedicationDao;
import com.example.medicatiooandhealthtrackerthemain.data.local.dao.MedicationLogDao;
import com.example.medicatiooandhealthtrackerthemain.data.local.dao.UserDao;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.HealthRecord;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.Medication;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.MedicationLog;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.User;

@Database(
        entities = {User.class, Medication.class, MedicationLog.class, HealthRecord.class},
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract MedicationDao medicationDao();
    public abstract MedicationLogDao medicationLogDao();
    public abstract HealthRecordDao healthRecordDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "health_tracker_db"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
