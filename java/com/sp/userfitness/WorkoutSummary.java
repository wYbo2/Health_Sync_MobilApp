package com.sp.userfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class WorkoutSummary extends AppCompatActivity {

    TextView Time, Calories;
    ImageButton Home;
    DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_summary);
        Time=findViewById(R.id.summary_time);
        Calories=findViewById(R.id.summary_calories);
        Home=findViewById(R.id.back_home);
        helper = new DatabaseHelper(this);

        int totalCal= (int) helper.getLatestCaloriesBurnt();
        String totalCalString = String.valueOf(totalCal);
        Calories.setText(totalCalString);

        int totalTime=(int)helper.getLatestWorkoutDuration();
        convertTimeFormat(totalTime);



        Home.setOnClickListener(v -> {

            Intent intent = new Intent(this, FitnessHome.class);
            startActivity(intent);
            finish();

        });

    }

    private void convertTimeFormat(int time) {
        int secs = time;
        int mins = secs / 60;
        int hours = mins / 60;
        secs = secs % 60;
        mins = mins % 60;
        Time.setText(String.format(Locale.getDefault(), "%02d : %02d : %02d", hours, mins, secs));
    }

}