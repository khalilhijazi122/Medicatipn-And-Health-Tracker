package com.example.medicatiooandhealthtrackerthemain;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicatiooandhealthtrackerthemain.data.local.entities.PendingLogItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MedicationLogAdapter extends RecyclerView.Adapter<MedicationLogAdapter.LogVH> {

    public interface OnLogAction {
        void onTaken(PendingLogItem item);
        void onMissed(PendingLogItem item);
    }

    private List<PendingLogItem> items = new ArrayList<>();
    private final OnLogAction listener;

    public MedicationLogAdapter(OnLogAction listener) {
        this.listener = listener;
    }

    public void setItems(List<PendingLogItem> newItems) {
        items = (newItems == null) ? new ArrayList<>() : newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_log, parent, false);
        return new LogVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LogVH holder, int position) {
        PendingLogItem item = items.get(position);

        holder.tvName.setText(item.name);

        String time = String.format(Locale.getDefault(), "%02d:%02d", item.hour, item.minute);
        String dose = (item.dosage == null) ? "" : item.dosage;
        holder.tvInfo.setText(dose + " • " + time);

        holder.btnTaken.setOnClickListener(v -> listener.onTaken(item));
        holder.btnMissed.setOnClickListener(v -> listener.onMissed(item));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class LogVH extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo;
        Button btnTaken, btnMissed;

        LogVH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMedName);
            tvInfo = itemView.findViewById(R.id.tvMedInfo);
            btnTaken = itemView.findViewById(R.id.btnTaken);
            btnMissed = itemView.findViewById(R.id.btnMissed);
        }
    }
}
