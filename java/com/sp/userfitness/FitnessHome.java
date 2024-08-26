package com.sp.userfitness;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FitnessHome extends AppCompatActivity {

    private static final String TAG = "FitnessHome";

    BottomNavigationView nav;
    ImageButton Back2clinicBtn, StateOfMindBtn, BreatheBtn, MeditateBtn, WorkOutBtn;
    TextView HomeCal, HomeTotalTime, HomeProgressActivity, HomeProgressDuration;
    DatabaseHelper helper;
    BarChart barChart;

    private BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String activityName = intent.getStringExtra("activityName");
            long time = intent.getLongExtra("time", 0);

            if (activityName != null) {
                HomeProgressActivity.setText(activityName);
            }

            updateTimerText(time);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fintess_home);

        nav = findViewById(R.id.navi);
        Back2clinicBtn = findViewById(R.id.back_to_clinic_btn);
        StateOfMindBtn = findViewById(R.id.stateOfMindBtn);
        BreatheBtn = findViewById(R.id.breatheBtn);
        MeditateBtn = findViewById(R.id.meditateBtn);
        WorkOutBtn = findViewById(R.id.workoutBtn);
        HomeCal = findViewById(R.id.home_calories);
        HomeTotalTime = findViewById(R.id.home_total_time);
        HomeProgressActivity = findViewById(R.id.home_progress_activity);
        HomeProgressDuration = findViewById(R.id.home_progress_duration);
        barChart = findViewById(R.id.barChart);
        setupBarChart();


        helper = new DatabaseHelper(this);

        nav.setSelectedItemId(R.id.home);

        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.daily) {
                    intent = new Intent(FitnessHome.this, Daily.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.history) {
                    intent = new Intent(FitnessHome.this, WorkoutHistoryActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        WorkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FitnessHome.this, WorkOutList.class);
                startActivity(intent);
            }
        });

        // Fetch and display weekly totals
        updateWeeklyStats();

        // Register receiver
        registerReceiver(timerReceiver, new IntentFilter("TIMER_UPDATED"));
    }

    private void updateWeeklyStats() {
        double weeklyCalories = helper.getWeeklyTotalCalories();
        long weeklyDurationMillis = helper.getWeeklyTotalDuration();

        // Display calories
        HomeCal.setText(String.valueOf((int) weeklyCalories));

        // Display total time in hours
        double weeklyDurationHours = weeklyDurationMillis / 3600000.0; // Convert milliseconds to hours
        HomeTotalTime.setText(String.format("%.2f hrs", weeklyDurationHours));
    }

    private void setupBarChart() {
        DatabaseHelper helper = new DatabaseHelper(this);
        Map<String, Double> dailyCalories = helper.getDailyCalories();

        List<BarEntry> entries = new ArrayList<>();
        List<String> days = new ArrayList<>(dailyCalories.keySet());

        for (int i = 0; i < days.size(); i++) {
            entries.add(new BarEntry(i, dailyCalories.get(days.get(i)).floatValue()));
        }

        if (entries.isEmpty()) {
            Log.w(TAG, "No data available for bar chart.");
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Calories Burned");
        dataSet.setColor(Color.LTGRAY); // Set bar color to light grey

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f); // Set bar width

        barChart.setData(barData);
        barChart.setBackgroundColor(Color.parseColor("#87CEFA")); // Set background color to light blue
        barChart.getDescription().setEnabled(false); // Disable description text
        barChart.getLegend().setEnabled(false); // Disable legend

        // Customize X-Axis
        barChart.getXAxis().setDrawGridLines(false); // Disable grid lines
        barChart.getXAxis().setDrawAxisLine(false); // Disable axis line
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(days));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setTextColor(Color.WHITE); // Set text color to white
        barChart.getXAxis().setTextSize(12f); // Set text size
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // Set position to bottom

        // Customize Y-Axis (Left)
        barChart.getAxisLeft().setDrawGridLines(false); // Disable grid lines
        barChart.getAxisLeft().setDrawAxisLine(false); // Disable axis line
        barChart.getAxisLeft().setDrawLabels(false); // Enable labels
        barChart.getAxisLeft().setAxisMinimum(0); // Set minimum value
        barChart.getAxisLeft().setAxisMaximum(500); // Set maximum value
        barChart.getAxisLeft().setGranularity(1f); // Ensure that each step is 1 unit
        barChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Convert float to integer
            }
        });

        // Customize Y-Axis (Right)
        barChart.getAxisRight().setDrawGridLines(false); // Disable grid lines
        barChart.getAxisRight().setDrawAxisLine(false); // Disable axis line
        barChart.getAxisRight().setDrawLabels(false); // Disable labels
        barChart.getAxisRight().setAxisMinimum(0); // Set minimum value
        barChart.getAxisRight().setAxisMaximum(500); // Set maximum value

        barChart.setFitBars(true); // Make the bars fit into the chart
        barChart.invalidate(); // Refresh the chart
    }

    private void updateTimerText(long time) {
        int secs = (int) (time / 1000);
        int mins = secs / 60;
        int hours = mins / 60;
        secs = secs % 60;
        mins = mins % 60;
        HomeProgressDuration.setText(String.format("%02d:%02d:%02d", hours, mins, secs));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timerReceiver);
    }
}