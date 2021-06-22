package com.example.cac2021service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;

public class extension extends Service implements SensorEventListener {

    private MediaPlayer player;

    boolean notEnabled = false;

    float[] accelerations = new float[90];
    float currentAcceleration = 0;

    float accX = 0;

    float accY = 0;

    float accZ = 0;

    int timesZero = 0;

    boolean alarmOn = false;

    private SensorManager sensorManager;
    SensorEventListener listen;
    private long lastUpdate;

    long initialTime = -1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        player = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
        player.setLooping(true);

        sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;

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

            // play sound, vibrate
            player.start();

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

            if (notEnabled) {

                //change button to green

            } else {

                //change button to red

                //make some sort of threashold, if in that threashold (like 0.03 to -0.03 or something)
                //for certain amount of loops then make it stop alarm countdown
                //if not stopped after full alarm countdown then start alarm (maybe make another threashold too, we will see how it goes)

                if ((currentAcceleration >= 0.02) && !(initialTime > -1)) {

                    startStopwatch();

                }

                if (initialTime > -1) {

                    if (currentAcceleration < 0.02) {

                        timesZero++;

                        if (timesZero > 9) {

                            resetStopwatch();
                            timesZero = 0;

                        }

                    } else if ((initialTime > -1) && (timePassed() > 5000)) {

                        timesZero = 0;
                        alarmOn = true;

                    }

                }

            }

        }

    }

    public void startStopwatch() {

        initialTime = System.currentTimeMillis();

    }

    public void resetStopwatch() {

        initialTime = -1;

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
        alarmOn = false;
        resetStopwatch();
        timesZero = 0;
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
