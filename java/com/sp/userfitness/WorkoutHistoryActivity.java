package com.sp.userfitness;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WorkoutAdapter workoutAdapter;
    private DatabaseHelper databaseHelper;

    private ImageButton Home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Home=findViewById(R.id.back_home_history);

        databaseHelper = new DatabaseHelper(this);
        List<Workout> workoutList = loadWorkoutData();
        workoutAdapter = new WorkoutAdapter(workoutList);
        recyclerView.setAdapter(workoutAdapter);
        Home.setOnClickListener(v -> {

            Intent intent = new Intent(this, FitnessHome.class);
            startActivity(intent);
            finish();

        });
    }

    private List<Workout> loadWorkoutData() {
        List<Workout> workoutList = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllWorkouts();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String activityName = cursor.getString(cursor.getColumnIndex("activity_name"));
                long duration = cursor.getLong(cursor.getColumnIndex("duration"));
                double caloriesBurnt = cursor.getDouble(cursor.getColumnIndex("calories_burnt"));
                workoutList.add(new Workout(date, activityName, duration, caloriesBurnt));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return workoutList;
    }
}
