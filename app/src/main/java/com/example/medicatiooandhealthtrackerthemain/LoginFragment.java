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
import androidx.navigation.fragment.NavHostFragment;

import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.User;
import com.example.medicatiooandhealthtrackerthemain.utils.AppExecutors;
import com.example.medicatiooandhealthtrackerthemain.utils.SessionManager;


public class LoginFragment extends Fragment {

    EditText etEmail, etPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_login, container, false);

        etEmail = v.findViewById(R.id.etEmail);
        etPassword = v.findViewById(R.id.etPassword);

        Button btnLogin = v.findViewById(R.id.btnLogin);



        btnLogin.setOnClickListener(view -> doLogin());

        return v;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View tvGoRegister = view.findViewById(R.id.tvGoRegister);

        tvGoRegister.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(v)
                    .navigate(R.id.action_login_to_register);
        });
    }


    private void doLogin() {
        String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
        String pass = etPassword.getText() == null ? "" : etPassword.getText().toString();

        // Validation بسيط
        if (email.isEmpty()) { etEmail.setError("Required"); return; }
        if (pass.isEmpty()) { etPassword.setError("Required"); return; }

        AppExecutors.io().execute(() -> {
            User user = AppDatabase.getInstance(requireContext())
                    .userDao()
                    .login(email, pass);

            requireActivity().runOnUiThread(() -> {
                if (user == null) {
                    etEmail.setError("Wrong email or password");
                } else {
                    // Save session
                    new SessionManager(requireContext()).saveUserId(user.id);

                    // Go Main
                    startActivity(new Intent(requireContext(), MainActivity.class));
                    requireActivity().finish();
                }
            });
        });
    }
}
