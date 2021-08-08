package com.example.cac2021service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.os.Vibrator;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class extension extends Service implements SensorEventListener {

    private MediaPlayer player;

    float[] accelerations = new float[90];
    float currentAcceleration = 0;

    float accX = 0;

    float accY = 0;

    float accZ = 0;

    int timesZero = 0;

    boolean alarmOn = false;

    private SensorManager sensorManager;
    private Vibrator vibrator;

    long initialTime = -1;

    //should be low
    double threshold = 0.5;

    ArrayList<Float> accelerationHistory = new ArrayList<Float>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //player = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
        player = MediaPlayer.create(this, R.raw.alert);
        player.setLooping(true);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        return START_NOT_STICKY;

    }

    @Override
    public void onCreate() {

        super.onCreate();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentText("Alarm Enabled")
                .build();

        startForeground(42, notification);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            process(event);

        }

    }

    private void process(SensorEvent event) {

        accX = event.values[0];
        accY = event.values[1];
        accZ = event.values[2];

        if (alarmOn) {

            AudioManager manager = null;

            manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            manager.setStreamVolume(AudioManager.STREAM_MUSIC, manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

            player.setVolume(1, 1);
            player.start();
            vibrator.vibrate(100);

        } else {

            //rounds them to 2 decimal places
            accX = (float) Math.round(accX * 100f) / 100f;
            accY = (float) Math.round(accY * 100f) / 100f;
            accZ = (float) Math.round(accZ * 100f) / 100f;

            accX = Math.abs(accX);
            accY = Math.abs(accY);
            accZ = Math.abs(accZ);

            currentAcceleration = accX + accY + accZ;

            for (int i = accelerations.length - 1; i >= 0; i--) {

                if (i == 0) {

                    accelerations[0] = currentAcceleration;

                } else {

                    accelerations[i] = accelerations[i - 1];

                }

            }

            if ((currentAcceleration >= threshold) && !(initialTime > -1)) {

                startStopwatch();

            }

            System.out.println("Size: " + accelerationHistory.size());

            if (initialTime > -1) {

                accelerationHistory.add(currentAcceleration);

                float jerk = 0;
                float sum = 0;

                for (int i = 0; i < accelerationHistory.size(); i++) {

                    sum = sum + accelerationHistory.get(i);

                }

                if ((timePassed() > 1000)) {

                    jerk = sum / timePassed();
                    jerk = jerk * 10;

                } else {

                    jerk = 0;

                }

                System.out.println("Jerk: " + jerk);

                if (currentAcceleration < threshold) {

                    timesZero++;

                    if (timesZero > 9) {

                        resetStopwatch();
                        timesZero = 0;

                    }

                } else if ((initialTime > -1) && (jerk > 0.3) && (accelerationHistory.size() > 10)) {

                    timesZero = 0;
                    alarmOn = true;

                }

            }

        }

    }

    public void startStopwatch() {

        initialTime = System.currentTimeMillis();

    }

    public void resetStopwatch() {

        initialTime = -1;
        accelerationHistory.clear();

    }

    public int timePassed() {

        return (int) (System.currentTimeMillis() - initialTime);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {

        player.stop();
        vibrator.cancel();
        alarmOn = false;
        resetStopwatch();
        timesZero = 0;
        sensorManager.unregisterListener(this);
        stopForeground(true);
        super.onDestroy();

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "42";
        String channelName = "Foreground Service Notification";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
