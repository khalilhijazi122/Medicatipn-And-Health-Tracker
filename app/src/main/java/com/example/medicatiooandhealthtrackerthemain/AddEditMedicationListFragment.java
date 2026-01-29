package com.example.medicatiooandhealthtrackerthemain;

import static com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase.getInstance;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.MedicationLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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
    private com.example.medicatiooandhealthtrackerthemain.utils.SessionManager sessionManager;

    public AddEditMedicationListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_medication_list, container, false);
        sessionManager = new com.example.medicatiooandhealthtrackerthemain.utils.SessionManager(requireContext());
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

        db = AppDatabase.getInstance(requireContext());




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
            long medId;

            // داخل executor.execute في دالة saveMedication
            if (isEdit) {
                med.id = editMedId;

                Medication old = db.medicationDao().getById(editMedId);
                med.userId = (old != null) ? old.userId : sessionManager.getUserId();
                db.medicationDao().update(med);
            } else {

                med.userId = sessionManager.getUserId();
                medId = db.medicationDao().insert(med);
                createPendingLogForToday(medId, med.userId, hour, minute);
            }

            if (!isAdded()) return;

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), isEdit ? "Updated!" : "Saved!", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
            });
        });
    }

    /**
     * Creates a PENDING medication log for today's scheduled time
     */
    private void createPendingLogForToday(long medicationId, int userId, int hour, int minute) {
        // Get today's date at the scheduled time
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long scheduledTime = cal.getTimeInMillis();
        long now = System.currentTimeMillis();

        // Only create log if the scheduled time hasn't passed yet today
        if (scheduledTime > now) {
            MedicationLog log = new MedicationLog();
            log.userId = userId;
            log.medicationId = (int) medicationId;
            log.timestamp = scheduledTime;
            log.status = "PENDING";

            long logId = db.medicationLogDao().insert(log);

            Log.d("ADD_MEDICATION", "Created PENDING log ID: " + logId +
                    " for time: " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(scheduledTime)));
        } else {
            Log.d("ADD_MEDICATION", "Scheduled time already passed today, no log created");
        }
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
