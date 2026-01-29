package com.example.medicatiooandhealthtrackerthemain;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicatiooandhealthtrackerthemain.R;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.HealthRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SymptomAdapter extends RecyclerView.Adapter<SymptomAdapter.ViewHolder> {

    private List<HealthRecord> symptoms;

    public SymptomAdapter(List<HealthRecord> symptoms) {
        this.symptoms = symptoms;
    }

    public void updateData(List<HealthRecord> newSymptoms) {
        this.symptoms = newSymptoms;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_symptom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthRecord symptom = symptoms.get(position);

        // Format date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
        String dateStr = sdf.format(new Date(symptom.getTimestamp()));

        holder.tvDate.setText(dateStr);
        holder.tvDescription.setText(symptom.getDescription());
        holder.tvSeverity.setText(symptom.getSeverity());

        // Set severity badge color
        int bgResource;
        if (symptom.getSeverity() != null) {
            switch (symptom.getSeverity().toLowerCase()) {
                case "mild":
                    bgResource = R.drawable.bg_severity_mild;
                    break;
                case "moderate":
                    bgResource = R.drawable.bg_severity_moderate;
                    break;
                case "severe":
                    bgResource = R.drawable.bg_severity_severe;
                    break;
                default:
                    bgResource = R.drawable.bg_severity_mild;
            }
            holder.tvSeverity.setBackgroundResource(bgResource);
        }
    }

    @Override
    public int getItemCount() {
        return symptoms != null ? symptoms.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDescription, tvSeverity;

        ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvSymptomDate);
            tvDescription = itemView.findViewById(R.id.tvSymptomDescription);
            tvSeverity = itemView.findViewById(R.id.tvSeverity);
        }
    }
}
