package com.example.medicatiooandhealthtrackerthemain.data.local.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "health_records",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = CASCADE
        ),
        indices = {@Index("userId")}
)
public class HealthRecord {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;

    public String type;

    public double value;
    public String unit;
    public String note;

    public long timestamp;
}
