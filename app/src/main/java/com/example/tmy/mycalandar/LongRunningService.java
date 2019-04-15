package com.example.tmy.mycalandar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LongRunningService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        Log.i("log","oncreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("log","onstartcommand");
        String time=intent.getStringExtra("time");
        String event=intent.getStringExtra("event");
        long ID=intent.getLongExtra("id",0);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder .setContentTitle(time)
                .setContentText(event)
                .setSmallIcon(R.drawable.alarm)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        Notification notification = builder.build();
        manager.notify((int)ID,notification);

        return START_NOT_STICKY;
    }
}
