package com.example.medicatiooandhealthtrackerthemain.data.local.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "medications",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = CASCADE
        ),
        indices = {@Index("userId")}
)
public class Medication {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;

    public String name;
    public String dosage;


    public int hour;
    public int minute;

    public int frequencyPerDay;

    public boolean isActive;
}
