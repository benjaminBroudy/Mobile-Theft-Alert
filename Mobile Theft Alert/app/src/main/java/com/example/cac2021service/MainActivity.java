package com.example.cac2021service;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.app.Notification;
import android.app.NotificationManager;
import android.widget.ImageButton;
import android.widget.TextView;

import java.sql.Time;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public final class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private Button button;
    private Button debugButton;

    private TextView acceleration;

    private SensorManager sensorManager;

    boolean off = true;

    boolean debug = true;
    boolean debug2 = false;

    float[] arr;
    int place = 0;

    long startTime = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#5e9c00")));

        button = (Button) findViewById(R.id.button);
        button.setBackgroundColor(android.graphics.Color.parseColor("#1ebd31"));

        debugButton = (Button) findViewById(R.id.debugButton);

        sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        acceleration = (TextView)findViewById(R.id.textView);

        button.setOnClickListener(this);
        debugButton.setOnClickListener(this);

        arr = new float[20000];

        AudioManager manager = null;

        manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {

        if (view == button) {

            if (off) {

                off = false;
                button.setText("Alarm On");
                button.setBackgroundColor(Color.RED);
                getSupportActionBar().setBackgroundDrawable(
                        new ColorDrawable(Color.RED));
                //startService(new Intent(this, extension.class));
                startForegroundService(new Intent(this, extension.class));

            } else {

                off = true;
                button.setText("Alarm Off");
                button.setBackgroundColor(android.graphics.Color.parseColor("#1ebd31"));
                getSupportActionBar().setBackgroundDrawable(
                        new ColorDrawable(Color.parseColor("#5e9c00")));
                stopService(new Intent(this, extension.class));

            }

        }

        if (view == debugButton) {

            if (debug) {

                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

                try {
                    TimeUnit.SECONDS.sleep(4);
                    vibrator.vibrate(VibrationEffect.createWaveform(new long[]{100, 1000, 100, 1000}, new int[]{255, 255, 255, 255}, -1));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                debug2 = true;
                debug = false;
                startTime = System.currentTimeMillis();
                debugButton.setText("Reset");

            } else {

                debug2 = false;
                debug = true;
                place = 0;
                debugButton.setText("Start");

            }

        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float accX = event.values[0];
        float accY = event.values[1];
        float accZ = event.values[2];

        accX = (float) Math.round(accX * 100f) / 100f;
        accY = (float) Math.round(accY * 100f) / 100f;
        accZ = (float) Math.round(accZ * 100f) / 100f;

        accX = Math.abs(accX);
        accY = Math.abs(accY);
        accZ = Math.abs(accZ);

        float currentAcceleration = accX + accY + accZ;

        currentAcceleration = (float) Math.round(currentAcceleration * 100f) / 100f;

        //acceleration.setText(String.valueOf(currentAcceleration));

        if (debug2) {

            if (place < 20000) {

                arr[place] = currentAcceleration;
                place++;

            }

            float average = 0;
            float sum = 0;

            for (int i = 0; i < (place - 1); i++) {

                sum = sum + arr[i];

            }

            average = sum / (place - 1);

            acceleration.setText(String.format("%.3f", Double.valueOf(average)) + ", " + ((System.currentTimeMillis() - startTime) / 1000));

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (!off) {

            stopService(new Intent(this, extension.class));

        }

    }

}
