package com.sp.userfitness;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class WorkoutTimer extends AppCompatActivity {
    private TextView Timer, ActivityName, LiveCaloriesBurnt;
    private Button btnStart;
    private ImageButton btnPauseResume, btnEnd;
    private LinearLayout Buttons;
    private boolean isPaused = false;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    TimerService timerService;
    Double calburnt;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_timer);

        Timer = findViewById(R.id.timer);
        btnStart = findViewById(R.id.workout_start_btn);
        btnPauseResume = findViewById(R.id.workout_pause_btn);
        btnEnd = findViewById(R.id.workout_end_btn);
        Buttons = findViewById(R.id.buttons);
        ActivityName = findViewById(R.id.current_activity);
        LiveCaloriesBurnt = findViewById(R.id.live_cal_burnt);

        String activityName = getIntent().getStringExtra("activityName");
        ActivityName.setText(activityName);

        SharedPreferences preferences = getSharedPreferences("TimerPrefs", MODE_PRIVATE);
        boolean isTimerRunning = preferences.getBoolean("isTimerRunning", false);
        isPaused = preferences.getBoolean("isPaused", false);
        long savedTime = preferences.getLong("time", 0);

        if (isTimerRunning) {
            btnStart.setVisibility(View.GONE);
            Buttons.setVisibility(View.VISIBLE);
            updateTimerText(savedTime);
        } else {
            btnStart.setVisibility(View.VISIBLE);
            Buttons.setVisibility(View.GONE);
            Timer.setText("00 : 00 : 00");
        }

        btnStart.setOnClickListener(v -> {
            requestNotificationPermission();
            Intent serviceIntent = new Intent(WorkoutTimer.this, TimerService.class);
            serviceIntent.putExtra("activityName", activityName);
            startService(serviceIntent);
            btnStart.setVisibility(View.GONE);
            Buttons.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isTimerRunning", true);
            editor.apply();

            // Return result to WorkOutList activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("isTimerRunning", true);
            resultIntent.putExtra("activityName", activityName);
            setResult(RESULT_OK, resultIntent);
        });

        btnPauseResume.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            if (isPaused) {
                startService(new Intent(this, TimerService.class).setAction("RESUME"));
                editor.putBoolean("isPaused", false);
            } else {
                startService(new Intent(this, TimerService.class).setAction("PAUSE"));
                editor.putBoolean("isPaused", true);
            }
            editor.apply();
            isPaused = !isPaused;
        });

        btnEnd.setOnClickListener(v -> {
            startService(new Intent(this, TimerService.class).setAction("END"));
            btnStart.setVisibility(View.VISIBLE);
            Buttons.setVisibility(View.GONE);
            Timer.setText("00 : 00 : 00");
            LiveCaloriesBurnt.setText("0");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isTimerRunning", false);
            editor.putBoolean("isPaused", false);
            editor.putLong("time", 0);
            editor.apply();

            // Return result to WorkOutList activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("isTimerRunning", false);
            setResult(RESULT_OK, resultIntent);

            Intent intent=new Intent(this, WorkoutSummary.class);
            startActivity(intent);
            finish();
        });

        registerReceiver(timerReceiver, new IntentFilter("TIMER_UPDATED"));
    }

    private final BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long time = intent.getLongExtra("time", 0);
            double calories = intent.getDoubleExtra("calories", 0);

            SharedPreferences preferences = getSharedPreferences("TimerPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("time", time);
            editor.apply();

            updateTimerText(time);
            LiveCaloriesBurnt.setText(String.format("%.0f", calories));
        }
    };

    private void updateTimerText(long time) {
        int secs = (int) (time / 1000);
        int mins = secs / 60;
        int hours = mins / 60;
        secs = secs % 60;
        mins = mins % 60;
        Timer.setText(String.format(Locale.getDefault(), "%02d : %02d : %02d", hours, mins, secs));
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timerReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with showing notifications
            } else {
                // Permission denied, you can show a message to the user or handle it gracefully
            }
        }
    }
}
