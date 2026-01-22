package com.example.medicatiooandhealthtrackerthemain.data.local.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "medication_logs",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = CASCADE),
                @ForeignKey(entity = Medication.class,
                        parentColumns = "id",
                        childColumns = "medicationId",
                        onDelete = CASCADE)
        },
        indices = {@Index("userId"), @Index("medicationId")}
)
public class MedicationLog {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public int medicationId;


    public long timestamp;

    // "TAKEN" أو "MISSED"
    public String status;
}
