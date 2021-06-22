package com.example.cac2021service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;

public class extension extends Service {

    private MediaPlayer player;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        player = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
        player.setLooping(true);
        player.start();

        return START_STICKY;

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        player.stop();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
