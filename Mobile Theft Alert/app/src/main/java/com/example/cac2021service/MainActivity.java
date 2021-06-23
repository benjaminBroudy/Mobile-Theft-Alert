package com.example.cac2021service;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.Notification;
import android.app.NotificationManager;
import android.widget.TextView;

import org.w3c.dom.Text;

public final class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private Button button;
    private TextView acceleration;
    private SensorManager sensorManager;
    boolean off = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        button.setBackgroundColor(android.graphics.Color.parseColor("#1ebd31"));

        sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        acceleration = (TextView)findViewById(R.id.textView);

        button.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {

        if (off) {

            off = false;
            button.setText("Enabled");
            button.setBackgroundColor(Color.RED);
            //startService(new Intent(this, extension.class));
            startForegroundService(new Intent(this, extension.class));

        } else {

            off = true;
            button.setText("Disabled");

            button.setBackgroundColor(android.graphics.Color.parseColor("#1ebd31"));
            stopService(new Intent(this, extension.class));

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

        acceleration.setText(String.valueOf(currentAcceleration));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
