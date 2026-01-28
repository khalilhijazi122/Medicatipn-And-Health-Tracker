package com.example.medicatiooandhealthtrackerthemain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.HealthRecord;
import com.example.medicatiooandhealthtrackerthemain.utils.SessionManager;

public class AddEditHealthFragment extends Fragment {
    private EditText etValue, etNote;
    private Spinner spinnerType;
    private AppDatabase db;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_health, container, false);

        db = AppDatabase.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        etValue = view.findViewById(R.id.etValue);
        etNote = view.findViewById(R.id.etNote);
        spinnerType = view.findViewById(R.id.spinnerType);
        Button btnSave = view.findViewById(R.id.btnSaveHealth);

        btnSave.setOnClickListener(v -> saveRecord());
        return view;
    }

    private void saveRecord() {
        String valStr = etValue.getText().toString();
        if (valStr.isEmpty()) return;

        HealthRecord hr = new HealthRecord();
        hr.userId = sessionManager.getUserId(); //
        hr.type = spinnerType.getSelectedItem().toString(); //
        hr.value = Double.parseDouble(valStr);
        hr.note = etNote.getText().toString(); //
        hr.timestamp = System.currentTimeMillis();

        new Thread(() -> {
            db.healthRecordDao().insert(hr); //
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
            });
        }).start();
    }
}