package com.example.medicatiooandhealthtrackerthemain;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.example.medicatiooandhealthtrackerthemain.utils.Prefs;

public class settingFragment extends Fragment {

    private SwitchCompat switchDarkMode, switchNotifications;

    public settingFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        switchNotifications = view.findViewById(R.id.switchNotifications);

        // Load saved values
        switchDarkMode.setChecked(Prefs.isDarkMode(requireContext()));
        switchNotifications.setChecked(Prefs.isNotifications(requireContext()));

        // Dark mode toggle
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Prefs.setDarkMode(requireContext(), isChecked);

            // تطبيق مباشر
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            Toast.makeText(requireContext(),
                    isChecked ? "Dark Mode ON" : "Dark Mode OFF",
                    Toast.LENGTH_SHORT).show();
        });

        // Notifications toggle (بس حفظ خيار بدون تنفيذ إشعارات الآن)
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Prefs.setNotifications(requireContext(), isChecked);
            Toast.makeText(requireContext(),
                    isChecked ? "Notifications Enabled" : "Notifications Disabled",
                    Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
