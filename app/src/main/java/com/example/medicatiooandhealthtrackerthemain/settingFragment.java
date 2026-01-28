package com.example.medicatiooandhealthtrackerthemain;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.medicatiooandhealthtrackerthemain.utils.Prefs;

public class settingFragment extends Fragment {

    private SwitchCompat switchDarkMode, switchNotifications;

    public settingFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        TextView tvAppVersion = view.findViewById(R.id.tvAppVersion);

        TextView btnEditProfile = view.findViewById(R.id.btnEditProfile);
        TextView btnChangePassword = view.findViewById(R.id.btnChangePassword);
        TextView btnRateApp = view.findViewById(R.id.btnRateApp);
        TextView btnShareApp = view.findViewById(R.id.btnShareApp);

        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        switchNotifications = view.findViewById(R.id.switchNotifications);

        // Back
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // Version
        try {
            if (getContext() != null) {
                String ver = getContext().getPackageManager()
                        .getPackageInfo(getContext().getPackageName(), 0).versionName;
                tvAppVersion.setText("Version: " + ver);
            }
        } catch (Exception e) {
            tvAppVersion.setText("Version: -");
        }

        // Load saved values
        if (getContext() != null) {
            switchDarkMode.setChecked(Prefs.isDarkMode(getContext()));
            switchNotifications.setChecked(Prefs.isNotifications(getContext()));
        }

        // Dark mode
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (getContext() == null) return;

            Prefs.setDarkMode(getContext(), isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            Toast.makeText(getContext(),
                    isChecked ? "Dark Mode ON" : "Dark Mode OFF",
                    Toast.LENGTH_SHORT).show();
        });

        // Notifications
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (getContext() == null) return;

            Prefs.setNotifications(getContext(), isChecked);
            Toast.makeText(getContext(),
                    isChecked ? "Notifications Enabled" : "Notifications Disabled",
                    Toast.LENGTH_SHORT).show();
        });

        // Feature
        btnEditProfile.setOnClickListener(v ->
                Toast.makeText(getContext(), "Edit Profile (coming soon)", Toast.LENGTH_SHORT).show()
        );

        // Feature
        btnChangePassword.setOnClickListener(v ->
                Toast.makeText(getContext(), "Change Password (coming soon)", Toast.LENGTH_SHORT).show()
        );

        // Feature: Rate app
        btnRateApp.setOnClickListener(v -> {
            if (getContext() == null) return;
            String pkg = getContext().getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg)));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + pkg)));
            }
        });

        // Feature: Share app
        btnShareApp.setOnClickListener(v -> {
            if (getContext() == null) return;
            String pkg = getContext().getPackageName();

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, "Medication & Health Tracker");
            share.putExtra(Intent.EXTRA_TEXT,
                    "Try this app: https://play.google.com/store/apps/details?id=" + pkg);

            startActivity(Intent.createChooser(share, "Share via"));
        });

        return view;
    }
}
