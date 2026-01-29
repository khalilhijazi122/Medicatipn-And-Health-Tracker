package com.example.medicatiooandhealthtrackerthemain;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.User;
import com.example.medicatiooandhealthtrackerthemain.utils.AppExecutors;
import com.example.medicatiooandhealthtrackerthemain.utils.SessionManager;

public class RegisterFragment extends Fragment {

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private EditText etBloodType, etAge, etHeight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_register, container, false);

        etName = v.findViewById(R.id.etName);
        etEmail = v.findViewById(R.id.etEmail);
        etPassword = v.findViewById(R.id.etPassword);
        etConfirmPassword = v.findViewById(R.id.etConfirmPassword);

        etBloodType = v.findViewById(R.id.etBloodType);
        etAge = v.findViewById(R.id.etAge);
        etHeight = v.findViewById(R.id.etHeight);

        Button btnRegister = v.findViewById(R.id.btnRegister);
        View tvGoLogin = v.findViewById(R.id.tvGoLogin);

        tvGoLogin.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.action_register_to_login)
        );

        btnRegister.setOnClickListener(view -> doRegister());

        return v;
    }

    private void doRegister() {
        String name = txt(etName);
        String email = txt(etEmail);
        String pass = txt(etPassword);
        String confirm = txt(etConfirmPassword);

        String blood = txt(etBloodType).toUpperCase();
        String ageStr = txt(etAge);
        String heightStr = txt(etHeight);

        // Validation
        if (name.isEmpty()) { etName.setError("Required"); return; }
        if (email.isEmpty()) { etEmail.setError("Required"); return; }

        if (blood.isEmpty()) { etBloodType.setError("Required"); return; }
        if (!isValidBloodType(blood)) { etBloodType.setError("Use A+, A-, B+, B-, AB+, AB-, O+, O-"); return; }

        Integer age = parseIntOrNull(ageStr);
        if (age == null) { etAge.setError("Required"); return; }
        if (age < 1 || age > 120) { etAge.setError("Age must be 1 - 120"); return; }

        Integer height = parseIntOrNull(heightStr);
        if (height == null) { etHeight.setError("Required"); return; }
        if (height < 50 || height > 250) { etHeight.setError("Height must be 50 - 250 cm"); return; }

        if (pass.isEmpty()) { etPassword.setError("Required"); return; }
        if (pass.length() < 6) { etPassword.setError("Min 6 chars"); return; }
        if (!pass.equals(confirm)) { etConfirmPassword.setError("Not match"); return; }

        AppExecutors.io().execute(() -> {
            if (getContext() == null) return;

            AppDatabase db = AppDatabase.getInstance(getContext());

            // Check duplicate email
            if (db.userDao().findByEmail(email) != null) {
                safeUi(() -> etEmail.setError("Email already used"));
                return;
            }

            User u = new User();
            u.name = name;
            u.email = email;
            u.password = pass;
            u.bloodType = blood;
            u.age = age;
            u.heightCm = height;

            long id = db.userDao().insert(u);

            safeUi(() -> {
                if (getContext() == null) return;

                new SessionManager(getContext()).saveUserId((int) id);
                startActivity(new Intent(getContext(), MainActivity.class));
                if (getActivity() != null) getActivity().finish();
            });
        });
    }

    private void safeUi(Runnable r) {
        if (!isAdded()) return;//إذا الـ Fragment مش مربوطة بالـ Activity
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            if (!isAdded()) return;
            r.run();
        });
    }

    private String txt(EditText e) {
        return (e.getText() == null) ? "" : e.getText().toString().trim();
    }

    private Integer parseIntOrNull(String s) {
        try {
            if (s == null || s.trim().isEmpty()) return null;
            return Integer.parseInt(s.trim());
        } catch (Exception ex) {
            return null;
        }
    }

    private boolean isValidBloodType(String b) {
        // accepted: A+, A-, B+, B-, AB+, AB-, O+, O-
        return b.equals("A+") || b.equals("A-") ||
                b.equals("B+") || b.equals("B-") ||
                b.equals("AB+") || b.equals("AB-") ||
                b.equals("O+") || b.equals("O-");
    }
}
