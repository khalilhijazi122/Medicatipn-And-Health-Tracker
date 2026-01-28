package com.example.medicatiooandhealthtrackerthemain;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.HealthRecord;
import com.example.medicatiooandhealthtrackerthemain.utils.SessionManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HealthTrackerFragment extends Fragment {
    private AppDatabase db;
    private SessionManager sessionManager;
    private HealthAdapter adapter;
    private LineChart healthChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_tracker, container, false);

        // 1. إعداد العناصر
        healthChart = view.findViewById(R.id.healthChart);
        RecyclerView rv = view.findViewById(R.id.rvHealthHistory);
        FloatingActionButton fab = view.findViewById(R.id.fabAddHealth);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        db = AppDatabase.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        fab.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_healthTracker_to_addEditHealth);
        });

        // 2. مراقبة البيانات وتحديث القائمة والشارت معاً
        db.healthRecordDao().getAll(sessionManager.getUserId()).observe(getViewLifecycleOwner(), records -> {
            if (records != null && !records.isEmpty()) {
                // تحديث القائمة
                adapter = new HealthAdapter(records);
                rv.setAdapter(adapter);

                // تحديث الشارت بجميع الأنواع
                updateMultiChart(records);
            }
        });

        return view;
    }

    private void updateMultiChart(List<HealthRecord> allRecords) {
        if (allRecords == null || allRecords.isEmpty()) return;

        // 1. ترتيب البيانات زمنياً
        Collections.sort(allRecords, (a, b) -> Long.compare(a.timestamp, b.timestamp));

        // 2. استخدام Map لتجميع البيانات حسب النوع تلقائياً
        // المفتاح (Key) هو اسم النوع، والقيمة (Value) هي قائمة النقاط للشارت
        HashMap<String, List<Entry>> dataMap = new HashMap<>();

        for (HealthRecord record : allRecords) {
            String type = record.type;
            if (!dataMap.containsKey(type)) {
                dataMap.put(type, new ArrayList<>());
            }
            dataMap.get(type).add(new Entry(record.timestamp, (float) record.value));
        }

        // 3. إنشاء DataSets لكل نوع موجود في القائمة
        LineData lineData = new LineData();
        int[] colors = {Color.MAGENTA, Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, Color.YELLOW};
        int colorIndex = 0;

        for (String type : dataMap.keySet()) {
            LineDataSet dataSet = new LineDataSet(dataMap.get(type), type);

            // اختيار لون مختلف لكل خط
            int color = colors[colorIndex % colors.length];
            dataSet.setColor(color);
            dataSet.setCircleColor(color);
            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(4f);

            lineData.addDataSet(dataSet);
            colorIndex++;
        }

        // 4. إعدادات المحور الأفقي (التوقيت)
        healthChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
                return sdf.format(new Date((long) value));
            }
        });

        // تحسينات تقنية للشارت
        healthChart.setData(lineData);
        healthChart.getXAxis().setLabelRotationAngle(-45); // تدوير الوقت لكي لا تتداخل النصوص
        healthChart.getDescription().setText("Health Trends Overview");
        healthChart.animateX(1000);
        healthChart.invalidate(); // تحديث الرسم
    }
}