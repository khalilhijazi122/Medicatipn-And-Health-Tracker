package com.example.medicatiooandhealthtrackerthemain.data.local.entities;

public class PendingLogItem {
    public int logId;
    public int medicationId;

    public String name;
    public String dosage;
    public int hour;
    public int minute;

    public long timestamp; // وقت إنشاء الـ pending
}
