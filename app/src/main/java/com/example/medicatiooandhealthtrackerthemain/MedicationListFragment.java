package com.example.medicatiooandhealthtrackerthemain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.Medication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MedicationListFragment extends Fragment {

    private AppDatabase db;
    private RecyclerView rv;
    private MedicationAdapter adapter;

    public MedicationListFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_medication_list, container, false);

        rv = view.findViewById(R.id.recyclerMedications);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddMedication);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        // DB
        db = Room.databaseBuilder(requireContext(),
                        AppDatabase.class,
                        "medication_db"
                ).allowMainThreadQueries() // (مؤقتاً للتعلم فقط)
                .build();

        // Adapter with click listener for Edit
        adapter = new MedicationAdapter(new MedicationAdapter.OnMedicationClick() {
            @Override
            public void onEdit(Medication med) {
                Bundle b = new Bundle();
                b.putInt("medId", med.id);
                Navigation.findNavController(requireView())
                        .navigate(R.id.AddEditMedicationListFragment, b);
            }

            @Override
            public void onDelete(Medication med) {
                // لاحقاً
            }
        });




        rv.setAdapter(adapter);

        // Observe data
        db.medicationDao().getAllMedication().observe(getViewLifecycleOwner(),
                new Observer<List<Medication>>() {
                    @Override
                    public void onChanged(List<Medication> medications) {
                        adapter.setItems(medications);

                    }
                });

        // FAB -> ADD (no bundle)
        fabAdd.setOnClickListener(v -> {
            NavHostFragment.findNavController(MedicationListFragment.this)
                    .navigate(R.id.AddEditMedicationListFragment);
        });

        return view;
    }
}
