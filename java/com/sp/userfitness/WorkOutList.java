package com.sp.userfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WorkOutList extends AppCompatActivity {

    Button btnWalk, btnRun, btnCycle, btnSwim, btnSports;
    private static final int TIMER_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_out_list);

        btnWalk = findViewById(R.id.walk_btn);
        btnRun = findViewById(R.id.run_btn);
        btnCycle = findViewById(R.id.cycle_btn);
        btnSwim = findViewById(R.id.swim_btn);
        btnSports = findViewById(R.id.sports_btn);

        btnWalk.setOnClickListener(v -> startTimerActivity("Walking"));
        btnRun.setOnClickListener(v -> startTimerActivity("Running"));
        btnCycle.setOnClickListener(v -> startTimerActivity("Cycling"));
        btnSwim.setOnClickListener(v -> startTimerActivity("Swimming"));
        btnSports.setOnClickListener(v -> startTimerActivity("Sports"));
    }

    private void startTimerActivity(String activityName) {
        // Start the Timer Activity
        Intent intent = new Intent(WorkOutList.this, WorkoutTimer.class);
        intent.putExtra("activityName", activityName);
        startActivityForResult(intent, TIMER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TIMER_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                boolean isTimerRunning = data.getBooleanExtra("isTimerRunning", false);
                String activityName = data.getStringExtra("activityName");
                if (isTimerRunning && activityName != null) {
                    setButtonsEnabled(false, activityName);
                } else {
                    setButtonsEnabled(true, null);
                }
            }
        }
    }

    private void setButtonsEnabled(boolean enabled, String activityName) {
        btnWalk.setEnabled(enabled || "Walking".equals(activityName));
        btnRun.setEnabled(enabled || "Running".equals(activityName));
        btnCycle.setEnabled(enabled || "Cycling".equals(activityName));
        btnSwim.setEnabled(enabled || "Swimming".equals(activityName));
        btnSports.setEnabled(enabled || "Sports".equals(activityName));
    }
}
