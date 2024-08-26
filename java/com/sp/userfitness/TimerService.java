package com.sp.userfitness;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimerService extends Service {
    private final Handler handler = new Handler();
    private long startTime, timeInMillis, timeSwapBuff, updateTime = 0L;
    private Runnable updateTimerThread;
    private String activityName;

    private static final String CHANNEL_ID = "TimerServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final double USER_WEIGHT = 70; // Example weight in kg
    private double MET;


    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        if (checkNotificationPermission()) {
            startForeground(NOTIFICATION_ID, getNotification("00:00:00"));
        }

        startTime = SystemClock.uptimeMillis();
        updateTimerThread = new Runnable() {
            public void run() {
                timeInMillis = SystemClock.uptimeMillis() - startTime;
                updateTime = timeSwapBuff + timeInMillis;
                long elapsedTimeInSeconds = updateTime / 1000;
                double caloriesBurned = CalorieCalculator.calculateCaloriesBurned(elapsedTimeInSeconds, USER_WEIGHT, MET);

                Intent intent = new Intent("TIMER_UPDATED");
                intent.putExtra("time", updateTime);
                intent.putExtra("calories", caloriesBurned);
                intent.putExtra("activityName", activityName); // Send activity name
                sendBroadcast(intent);

                if (checkNotificationPermission()) {
                    String timeText = formatTime(updateTime);
                    Notification notification = getNotification(timeText);
                    NotificationManagerCompat.from(TimerService.this).notify(NOTIFICATION_ID, notification);
                }

                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateTimerThread);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            activityName = intent.getStringExtra("activityName");
            setMETValue(activityName);
        }

        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case "PAUSE":
                    timeSwapBuff += timeInMillis;
                    handler.removeCallbacks(updateTimerThread);
                    break;
                case "RESUME":
                    startTime = SystemClock.uptimeMillis();
                    handler.post(updateTimerThread);
                    break;
                case "END":
                    handler.removeCallbacks(updateTimerThread);
                    saveWorkoutData();
                    stopForeground(true);
                    stopSelf();
                    break;
            }
        }
        return START_STICKY;
    }

    private void setMETValue(String activityName) {
        switch (activityName) {
            case "Walking":
                MET = 3.8;
                break;
            case "Running":
                MET = 9.8;
                break;
            case "Cycling":
                MET = 7.5;
                break;
            case "Swimming":
                MET = 6.0;
                break;
            case "Sports":
                MET = 5.0;
                break;
            default:
                MET = 3.8; // Default MET value
                break;
        }
    }

    private void saveWorkoutData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        long durationInSeconds = updateTime / 1000;
        int caloriesBurned = CalorieCalculator.calculateCaloriesBurned(durationInSeconds, USER_WEIGHT, MET);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dbHelper.insertWorkoutData(currentDate, activityName, durationInSeconds, caloriesBurned);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Timer Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification getNotification(String timeText) {
        Intent notificationIntent = new Intent(this, WorkoutTimer.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Workout Timer")
                .setContentText(timeText)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentIntent(pendingIntent)
                .setSound(null)
                .build();
    }

    private String formatTime(long time) {
        int secs = (int) (time / 1000);
        int mins = secs / 60;
        int hours = mins / 60;
        secs = secs % 60;
        mins = mins % 60;
        return String.format("%02d:%02d:%02d", hours, mins, secs);
    }

    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return NotificationManagerCompat.from(this).areNotificationsEnabled();
        }
        return true;
    }

    public String getTimeText() {
        return getTimeText();
    }
}
