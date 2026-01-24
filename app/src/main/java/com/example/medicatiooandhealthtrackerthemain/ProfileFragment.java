package com.example.medicatiooandhealthtrackerthemain;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.medicatiooandhealthtrackerthemain.utils.Prefs;

public class ProfileFragment extends Fragment {

    private TextView tvUsername;
    private Button btnGoSettings, btnLogout;

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tvUsername);
        btnGoSettings = view.findViewById(R.id.btnGoSettings);
        btnLogout = view.findViewById(R.id.btnLogout);

        // عرض بيانات بسيطة من SharedPreferences
        String name = Prefs.getUsername(requireContext());
        int userId = Prefs.getUserId(requireContext());
        tvUsername.setText("Username: " + name + " (id=" + userId + ")");

        // الذهاب للإعدادات (لازم تكون موجودة في nav_graph)
        btnGoSettings.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.SettingFragment)
        );

        // Logout
        btnLogout.setOnClickListener(v -> {
            Prefs.clearUser(requireContext());

            // إذا عندك AuthActivity للّوجين:
            Intent i = new Intent(requireContext(), AuthActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        return view;
    }
}
