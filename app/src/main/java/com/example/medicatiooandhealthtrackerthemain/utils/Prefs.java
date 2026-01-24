package com.example.medicatiooandhealthtrackerthemain.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static final String PREF_NAME = "app_prefs";

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_DARK_MODE = "dark_mode";
    public static final String KEY_NOTIFICATIONS = "notifications";

    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setUser(Context c, int userId, String username) {
        sp(c).edit()
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_USERNAME, username)
                .apply();
    }

    public static int getUserId(Context c) {
        return sp(c).getInt(KEY_USER_ID, -1);
    }

    public static String getUsername(Context c) {
        return sp(c).getString(KEY_USERNAME, "User");
    }

    public static void clearUser(Context c) {
        sp(c).edit()
                .remove(KEY_USER_ID)
                .remove(KEY_USERNAME)
                .apply();
    }

    public static void setDarkMode(Context c, boolean enabled) {
        sp(c).edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    public static boolean isDarkMode(Context c) {
        return sp(c).getBoolean(KEY_DARK_MODE, false);
    }

    public static void setNotifications(Context c, boolean enabled) {
        sp(c).edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }

    public static boolean isNotifications(Context c) {
        return sp(c).getBoolean(KEY_NOTIFICATIONS, true);
    }
}
