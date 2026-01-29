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
    private String description;
    private String severity; // "Mild", "Moderate", "Severe"

    public long getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // ✅ Symptom getters (needed by SymptomAdapter)
    public String getDescription() {
        return description;
    }

    public String getSeverity() {
        return severity;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    // Vital sign setters
    public void setVitalType(String type) {
        this.type = type;
    }

    public void setValue(double value) {
        this.value = value;
    }


    public void setUnit(String unit) {
        this.unit = unit;
    }


    // ==================== HELPER METHODS ====================

    /**
     * Check if this is a symptom record
     */
    public boolean isSymptom() {
        return "SYMPTOM".equals(type);
    }

    /**
     * Check if this is a vital sign record
     */
    public boolean isVitalSign() {
        return "VITAL_SIGN".equals(type);
    }




}
