package com.example.tmy.mycalandar;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class DisplayNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String time=intent.getStringExtra("time");
        String event=intent.getStringExtra("event");
        long ID=intent.getLongExtra("id",0);
        Log.i("log",time);
        Log.i("log",event);

        Intent i = new Intent( context, LongRunningService.class);
        i.putExtra( "event", event);
        i.putExtra( "time",  time);
        i.putExtra( "id",  ID);

        context.startService(i);

    }
}
