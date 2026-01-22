package com.example.medicatiooandhealthtrackerthemain.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";

    private SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // حفظ userId بعد Login/Register
    public void saveUserId(int userId) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply();
    }

    // جلب userId
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    // هل المستخدم مسجّل دخول؟
    public boolean isLoggedIn() {
        return getUserId() != -1;
    }

    // تسجيل خروج
    public void logout() {
        prefs.edit().remove(KEY_USER_ID).apply();
    }
}

