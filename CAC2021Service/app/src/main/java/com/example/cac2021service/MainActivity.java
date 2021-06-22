package com.example.cac2021service;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public final class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button start, stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button) findViewById(R.id.buttonStart);
        stop = (Button) findViewById(R.id.buttonStop);


        start.setOnClickListener(this);
        stop.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view==start) {

            startService(new Intent(this, extension.class));

        } else if (view==stop) {

            stopService(new Intent(this, extension.class));
        }

    }
}
