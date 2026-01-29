package com.example.medicatiooandhealthtrackerthemain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.HealthRecord;
import com.example.medicatiooandhealthtrackerthemain.utils.SessionManager;

public class AddEditHealthFragment extends Fragment {
    private EditText etValue, etNote, etDescription;
    private Spinner spinnerType, spinnerSeverity;
    private LinearLayout symptomFields, vitalSignFields;
    private AppDatabase db;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_health, container, false);

        db = AppDatabase.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        // Vital sign fields
        etValue = view.findViewById(R.id.etValue);
        etNote = view.findViewById(R.id.etNote);
        vitalSignFields = view.findViewById(R.id.vitalSignFields);

        // Symptom fields
        etDescription = view.findViewById(R.id.etDescription);
        spinnerSeverity = view.findViewById(R.id.spinnerSeverity);
        symptomFields = view.findViewById(R.id.symptomFields);

        spinnerType = view.findViewById(R.id.spinnerType);
        Button btnSave = view.findViewById(R.id.btnSaveHealth);

        // Toggle fields based on type selection
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                if ("Symptom".equals(selectedType)) {
                    symptomFields.setVisibility(View.VISIBLE);
                    vitalSignFields.setVisibility(View.GONE);
                } else {
                    symptomFields.setVisibility(View.GONE);
                    vitalSignFields.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSave.setOnClickListener(v -> saveRecord());
        return view;
    }

    private void saveRecord() {
        String selectedType = spinnerType.getSelectedItem().toString();

        HealthRecord hr = new HealthRecord();
        hr.userId = sessionManager.getUserId();
        hr.timestamp = System.currentTimeMillis();

        if ("Symptom".equals(selectedType)) {
            // Save as symptom
            String description = etDescription.getText().toString().trim();
            if (description.isEmpty()) {
                Toast.makeText(getContext(), "Please describe your symptom", Toast.LENGTH_SHORT).show();
                return;
            }

            hr.type = "SYMPTOM";
            hr.setDescription(description);
            hr.setSeverity(spinnerSeverity.getSelectedItem().toString());
        } else {
            // Save as vital sign
            String valStr = etValue.getText().toString();
            if (valStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a value", Toast.LENGTH_SHORT).show();
                return;
            }

            hr.type = selectedType;
            hr.value = Double.parseDouble(valStr);
            hr.note = etNote.getText().toString();
        }

        new Thread(() -> {
            db.healthRecordDao().insert(hr);
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
            });
        }).start();
    }
}