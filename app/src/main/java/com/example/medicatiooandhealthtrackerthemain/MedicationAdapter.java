package com.example.medicatiooandhealthtrackerthemain;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicatiooandhealthtrackerthemain.R;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.Medication;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedViewHolder> {

    private List<Medication> items = new ArrayList<>();


    public interface OnMedicationClick {
        void onEdit(Medication med);
        void onDelete(Medication med);
    }

    private final OnMedicationClick listener;

    public MedicationAdapter(OnMedicationClick listener) {
        this.listener = listener;
    }

    public void setItems(List<Medication> newItems) {
        items = (newItems == null) ? new ArrayList<>() : newItems;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medication, parent, false);
        return new MedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MedViewHolder holder, int position) {
        Medication med = items.get(position);

        holder.tvName.setText(med.name);
        holder.tvDosage.setText(med.dosage == null ? "" : med.dosage);

        String time = String.format(Locale.getDefault(), "%02d:%02d", med.hour, med.minute);
        holder.tvTimeFreq.setText(time + " • " + med.frequencyPerDay + " times/day");
        holder.chipActive.setText(med.isActive ? "Active" : "Inactive");

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(med));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(med));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }


    static class MedViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDosage, tvTimeFreq;
        Chip chipActive;
        ImageButton btnEdit, btnDelete;

        public MedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDosage = itemView.findViewById(R.id.tvDosage);
            tvTimeFreq = itemView.findViewById(R.id.tvTimeFreq);
            chipActive = itemView.findViewById(R.id.chipActive);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
