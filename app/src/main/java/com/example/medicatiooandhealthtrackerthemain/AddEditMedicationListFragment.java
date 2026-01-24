package com.example.medicatiooandhealthtrackerthemain;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.Medication;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditMedicationListFragment extends Fragment {

    private AppDatabase db;

    private TextView tvTitle;
    private EditText etName, etDosage, etHour, etMinute, etFrequency;
    private CheckBox cbActive;
    private Button btnSave;

    private boolean isEdit = false;
    private int editMedId = -1;

    public AddEditMedicationListFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_medication_list, container, false);

        // Bind views
        tvTitle = view.findViewById(R.id.tvTitle);
        etName = view.findViewById(R.id.etName);
        etDosage = view.findViewById(R.id.etDosage);
        etHour = view.findViewById(R.id.etHour);
        etMinute = view.findViewById(R.id.etMinute);
        etFrequency = view.findViewById(R.id.etFrequency);
        cbActive = view.findViewById(R.id.cbActive);
        btnSave = view.findViewById(R.id.btnSave);

        // DB (basic)
        db = Room.databaseBuilder(requireContext(), AppDatabase.class, "medication_db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries() // مؤقتاً للتعلم
                .build();


        // Check bundle
        Bundle args = getArguments();
        if (args != null && args.containsKey("medId")) {
            isEdit = true;
            editMedId = args.getInt("medId", -1);

            tvTitle.setText("Edit Medication");

            // اقرأ الداتا من DB وعبّي الحقول
            Medication old = db.medicationDao().getById(editMedId);
            if (old != null) {
                etName.setText(old.name);
                etDosage.setText(old.dosage);
                etHour.setText(String.valueOf(old.hour));
                etMinute.setText(String.valueOf(old.minute));
                etFrequency.setText(String.valueOf(old.frequencyPerDay));
                cbActive.setChecked(old.isActive);

                // مهم جداً لئلا تخرب foreign key
                // خزن userId الموجود بالدواء القديم
                // حتى لا تكتب userId خطأ
            }
        } else {
            isEdit = false;
            tvTitle.setText("Add Medication");
            cbActive.setChecked(true);
        }


        // Save button
        btnSave.setOnClickListener(v -> saveMedication());

        return view;
    }

    private void saveMedication() {

        String name = etName.getText().toString().trim();
        String dosage = etDosage.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(requireContext(), "Please enter medication name", Toast.LENGTH_SHORT).show();
            return;
        }

        int hour = parseIntSafe(etHour.getText().toString().trim());
        int minute = parseIntSafe(etMinute.getText().toString().trim());
        int frequency = parseIntSafe(etFrequency.getText().toString().trim());
        boolean active = cbActive.isChecked();

        if (frequency <= 0) frequency = 1;

        Medication med = new Medication();
        med.name = name;
        med.dosage = dosage;
        med.hour = hour;
        med.minute = minute;
        med.frequencyPerDay = frequency;
        med.isActive = active;

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {

            if (isEdit) {
                // ✅ مهم جداً: ضع id للدواء قبل update
                med.id = editMedId;

                // ✅ مهم جداً: حافظ على userId الحقيقي من الدواء القديم
                Medication old = db.medicationDao().getById(editMedId);
                if (old != null) {
                    med.userId = old.userId;
                } else {
                    med.userId = 1; // fallback
                }

                db.medicationDao().update(med);

            } else {
                med.userId = 1; // مؤقتاً
                db.medicationDao().insert(med);
            }

            if (!isAdded()) return;

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), isEdit ? "Updated!" : "Saved!", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
            });
        });
    }

    private int parseIntSafe(String s) {
        try {
            if (TextUtils.isEmpty(s)) return 0;
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
