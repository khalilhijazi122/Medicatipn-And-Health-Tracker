package com.example.medicatiooandhealthtrackerthemain.data.local.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "users",
        indices = {@Index(value = {"email"}, unique = true)}
)
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String email;
    public String password;
    public String bloodType;   // ex: "A+", "O-"
    public Integer age;        // Integer (nullable) safer than int
    public Integer heightCm;   // Integer (nullable)
    public String profilePicUri; //for photo
}
