package com.example.medicatiooandhealthtrackerthemain;

import android.annotation.SuppressLint;
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

    EditText etName, etEmail, etPassword, etConfirmPassword;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_register, container, false);

        etName = v.findViewById(R.id.etName);
        etEmail = v.findViewById(R.id.etEmail);
        etPassword = v.findViewById(R.id.etPassword);
        etConfirmPassword = v.findViewById(R.id.etConfirmPassword);

        Button btnRegister = v.findViewById(R.id.btnRegister);
        View tvGoLogin = v.findViewById(R.id.tvGoLogin);

        tvGoLogin.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.action_register_to_login)
        );

        btnRegister.setOnClickListener(view -> doRegister());

        return v;
    }

    private void doRegister() {
        String name = etName.getText() == null ? "" : etName.getText().toString().trim();
        String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
        String pass = etPassword.getText() == null ? "" : etPassword.getText().toString();
        String confirm = etConfirmPassword.getText() == null ? "" : etConfirmPassword.getText().toString();

        // Validation بسيط
        if (name.isEmpty()) { etName.setError("Required"); return; }
        if (email.isEmpty()) { etEmail.setError("Required"); return; }
        if (pass.isEmpty()) { etPassword.setError("Required"); return; }
        if (!pass.equals(confirm)) { etConfirmPassword.setError("Not match"); return; }

        AppExecutors.io().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());

            // Check duplicate email
            if (db.userDao().findByEmail(email) != null) {
                requireActivity().runOnUiThread(() -> etEmail.setError("Email already used"));
                return;
            }

            User u = new User();
            u.name = name;
            u.email = email;
            u.password = pass;

            long id = db.userDao().insert(u);

            requireActivity().runOnUiThread(() -> {
                // Save session
                new SessionManager(requireContext()).saveUserId((int) id);

                // Go Main
                startActivity(new Intent(requireContext(), MainActivity.class));
                requireActivity().finish();
            });
        });
    }
}
