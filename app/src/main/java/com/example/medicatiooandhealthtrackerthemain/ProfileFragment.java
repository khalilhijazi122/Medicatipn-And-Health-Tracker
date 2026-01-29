package com.example.medicatiooandhealthtrackerthemain;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.User;
import com.example.medicatiooandhealthtrackerthemain.utils.AppExecutors;
import com.example.medicatiooandhealthtrackerthemain.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private ImageView ivProfilePic;
    private TextView tvName, tvEmail, tvBloodType, tvAge, tvHeight;
    private int currentUserId = -1;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null || currentUserId == -1) return;

                ivProfilePic.setImageURI(uri);

                AppExecutors.io().execute(() ->
                        AppDatabase.getInstance(requireContext())
                                .userDao()
                                .updateProfilePic(currentUserId, uri.toString())
                );
            });

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvBloodType = view.findViewById(R.id.tvBloodType);
        tvAge = view.findViewById(R.id.tvAge);
        tvHeight = view.findViewById(R.id.tvHeight);

        Button btnGoSettings = view.findViewById(R.id.btnGoSettings);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        ivProfilePic.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnGoSettings.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.SettingFragment)
        );

        btnLogout.setOnClickListener(v -> {
            new SessionManager(requireContext()).logout();
            Intent i = new Intent(requireContext(), AuthActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        loadUserFromDb();

        return view;
    }

    private void loadUserFromDb() {
        SessionManager sm = new SessionManager(requireContext());
        currentUserId = sm.getUserId();

        if (currentUserId == -1) {
            goAuth();
            return;
        }

        AppExecutors.io().execute(() -> {
            User u = AppDatabase.getInstance(requireContext()).userDao().findById(currentUserId);

            requireActivity().runOnUiThread(() -> bindUser(u));
        });
    }

    private void bindUser(User u) {
        if (!isAdded()) return;

        if (u == null) {
            tvName.setText("Unknown user");
            tvEmail.setText("-");
            tvBloodType.setText("Blood Type: -");
            tvAge.setText("Age: -");
            tvHeight.setText("Height: -");
            return;
        }

        tvName.setText(u.name != null ? u.name : "-");
        tvEmail.setText(u.email != null ? u.email : "-");
        tvBloodType.setText("Blood Type: " + (u.bloodType != null ? u.bloodType : "-"));
        tvAge.setText("Age: " + (u.age != null ? u.age : "-"));
        tvHeight.setText("Height: " + (u.heightCm != null ? (u.heightCm + " cm") : "-"));

        if (u.profilePicUri != null && !u.profilePicUri.trim().isEmpty()) {
            try {
                ivProfilePic.setImageURI(Uri.parse(u.profilePicUri));
            } catch (Exception ignored) {}
        }
    }

    private void goAuth() {
        Intent i = new Intent(requireContext(), AuthActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
