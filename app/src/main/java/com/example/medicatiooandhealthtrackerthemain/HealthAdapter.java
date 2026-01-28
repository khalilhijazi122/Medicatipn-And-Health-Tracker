package com.example.medicatiooandhealthtrackerthemain;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.HealthRecord;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HealthAdapter extends RecyclerView.Adapter<HealthAdapter.ViewHolder> {
    private List<HealthRecord> records;


    public HealthAdapter(List<HealthRecord> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ربط ملف الـ XML الخاص بالسطر الواحد
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_health_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthRecord record = records.get(position);

        // عرض البيانات في الـ TextViews
        holder.tvType.setText(record.type); //
        holder.tvValue.setText("Value: " + record.value);
        if (record.note != null && !record.note.isEmpty()) {
            holder.tvNote.setText("Notes: " + record.note);
            holder.tvNote.setVisibility(View.VISIBLE);
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String dateString = sdf.format(new Date(record.timestamp));
        holder.tvDate.setText(dateString);
    }

    @Override
    public int getItemCount() {
        return records != null ? records.size() : 0;
    }

    // تعريف العناصر الموجودة في item_health_record.xml
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvValue, tvDate,tvNote;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvValue = itemView.findViewById(R.id.tvValue);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNote = itemView.findViewById(R.id.tvNote);
        }
    }
}