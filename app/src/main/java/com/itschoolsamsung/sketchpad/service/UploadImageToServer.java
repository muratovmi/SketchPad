package com.itschoolsamsung.sketchpad.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.itschoolsamsung.sketchpad.R;

// Класс для загрузки изображения на сервер.
public class UploadImageToServer extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Мой изображение");
        builder.setTicker("Статус загрузки изображения.");
        builder.setProgress(10, 0, true);
        builder.setContentText("Процесс загрузки.");
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(21, builder.build());
        intent.getStringExtra("image_name");
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}