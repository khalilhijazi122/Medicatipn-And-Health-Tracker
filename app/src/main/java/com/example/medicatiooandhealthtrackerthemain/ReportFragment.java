package com.example.medicatiooandhealthtrackerthemain;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.MedicationLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportFragment extends Fragment {

    // UI Components
    private Spinner spinnerTimePeriod;
    private TextView tvAdherencePercent, tvTakenCount, tvMissedCount;
    private ProgressBar progressAdherence;
    private RecyclerView rvSymptoms;

    // ViewModel
    private ReportsViewModel viewModel;

    // Adapter
    private SymptomAdapter symptomAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ReportsViewModel.class);

        // Initialize views
        initializeViews(view);

        // Setup components
        setupTimePeriodSpinner();
        setupRecyclerView();

        // Load default data (Last 7 days)
        loadReportsForPeriod(0);
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews(View view) {
        spinnerTimePeriod = view.findViewById(R.id.spinnerTimePeriod);
        tvAdherencePercent = view.findViewById(R.id.tvAdherencePercent);
        tvTakenCount = view.findViewById(R.id.tvTakenCount);
        tvMissedCount = view.findViewById(R.id.tvMissedCount);
        progressAdherence = view.findViewById(R.id.progressAdherence);
        rvSymptoms = view.findViewById(R.id.rvSymptoms);
    }

    /**
     * Setup time period dropdown
     */
    private void setupTimePeriodSpinner() {
        // Create time period options
        String[] periods = {"Last 7 Days", "Last 30 Days", "Last 90 Days", "All Time"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                periods
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimePeriod.setAdapter(adapter);

        // Handle selection
        spinnerTimePeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadReportsForPeriod(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    /**
     * Setup RecyclerView for symptoms
     */
    private void setupRecyclerView() {
        symptomAdapter = new SymptomAdapter(new ArrayList<>());
        rvSymptoms.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSymptoms.setAdapter(symptomAdapter);
        rvSymptoms.setNestedScrollingEnabled(false);
    }

    /**
     * Load reports data for selected time period
     */
    private void loadReportsForPeriod(int periodIndex) {
        long endTime = System.currentTimeMillis();
        long startTime;

        switch(periodIndex) {
            case 0: // Last 7 days
                startTime = endTime - (7 * 24 * 60 * 60 * 1000L);
                break;
            case 1: // Last 30 days
                startTime = endTime - (30 * 24 * 60 * 60 * 1000L);
                break;
            case 2: // Last 90 days
                startTime = endTime - (90 * 24 * 60 * 60 * 1000L);
                break;
            default: // All time
                startTime = 0;
        }
        Log.d("DEBUG_REPORTS", "=== DEBUGGING ADHERENCE ===");
        Log.d("DEBUG_REPORTS", "Time range: " + new Date(startTime) + " to " + new Date(endTime));

        // Check what's actually in the database
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());

            // Get ALL medication logs (no filter)
            List<MedicationLog> allLogs = db.medicationLogDao().getAllLogsSync();

            Log.d("DEBUG_REPORTS", "Total logs in database: " + allLogs.size());

            if (allLogs.size() > 0) {
                for (int i = 0; i < Math.min(5, allLogs.size()); i++) {
                    MedicationLog log = allLogs.get(i);
                    Log.d("DEBUG_REPORTS", "Log #" + i + ": " +
                            "userId='" + log.userId + "', " +
                            "medicationId=" + log.medicationId + ", " +
                            "status='" + log.status + "', " +
                            "timestamp=" + new Date(log.timestamp));
                }
            } else {
                Log.d("DEBUG_REPORTS", "⚠️ NO LOGS IN DATABASE!");
            }

            int currentUser = 1; // Match what you use in ViewModel
            // Check counts for current user
            int takenSync = db.medicationLogDao().getTakenCountSync(currentUser, startTime, endTime);
            int missedSync = db.medicationLogDao().getMissedCountSync(currentUser, startTime, endTime);

            Log.d("DEBUG_REPORTS", "Taken count (sync): " + takenSync);
            Log.d("DEBUG_REPORTS", "Missed count (sync): " + missedSync);

        }).start();

        // Observe adherence data
        viewModel.getAdherenceData(startTime, endTime).observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                Log.d("DEBUG_REPORTS", "LiveData - Taken: " + data.getTakenCount() + ", Missed: " + data.getMissedCount());
                updateAdherenceUI(data);
            }
        });

        // Observe symptom data
        viewModel.getSymptomData(startTime, endTime).observe(getViewLifecycleOwner(), symptoms -> {
            if (symptoms != null) {
                symptomAdapter.updateData(symptoms);
            }
        });
    }

    /**
     * Update adherence statistics UI
     */
    private void updateAdherenceUI(AdherenceData data) {
        int total = data.getTakenCount() + data.getMissedCount();
        int percentage = total > 0 ? (data.getTakenCount() * 100 / total) : 0;

        // Update text views
        tvAdherencePercent.setText(percentage + "% adherence");
        tvTakenCount.setText("Taken: " + data.getTakenCount() + " doses");
        tvMissedCount.setText("Missed: " + data.getMissedCount() + " doses");

        // Update progress bar
        progressAdherence.setProgress(percentage);

        // Change color based on adherence level
        int color;
        if (percentage >= 90) {
            color = getResources().getColor(R.color.success, null); // Green - Excellent
        } else if (percentage >= 70) {
            color = getResources().getColor(R.color.warning, null); // Yellow - Good
        } else {
            color = getResources().getColor(R.color.error, null); // Red - Needs improvement
        }
        tvAdherencePercent.setTextColor(color);
    }
}