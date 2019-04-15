package com.example.tmy.mycalandar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;


public class BasicDialogFragment extends DialogFragment {
    JSONObject obj = new JSONObject();
    String correctdate,correcttime,cmonth;

    public interface LoginInputListener {
        void onLoginInputComplete(String pass);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.basic_dialog, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setPositiveButton("確定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                EditText edittext = (EditText) view.findViewById(R.id.edit1);
                                DatePicker datepicker=(DatePicker)view.findViewById(R.id.datepicker);
                                TimePicker mTimePicker = (TimePicker) view.findViewById(R.id.timePicker);
                                int year=datepicker.getYear();
                                int month=datepicker.getMonth();
                                int day=datepicker.getDayOfMonth();

                                int hour=mTimePicker.getHour();
                                int min=mTimePicker.getMinute();

                                Calendar calendar1 = Calendar.getInstance();
                                calendar1.set(year, month, day);

                                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                String strDate = format1.format(calendar1.getTime());

                                String strTime = String.format("%02d:%02d", hour, min);


                                File dir = view.getContext().getFilesDir();
                                File outFile = new File(dir, year+"_"+CorrectMonth(month+1)+".txt");
                                JSONObject obj=new JSONObject();
                                try {
                                    obj.put("date",strDate);
                                    obj.put("time",strTime);
                                    obj.put("edit",edittext.getText());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                writeToFile(outFile, obj.toString()+"&");


                                long ID=Long.parseLong(String.valueOf(year)+String.valueOf(month+1)+String.valueOf(day)+String.valueOf(hour)+String.valueOf(min));


                                new AsyncAlarm(view.getContext()).execute(String.valueOf(ID),edittext.getText().toString(),
                                        strTime,String.valueOf(year),String.valueOf(month),String.valueOf(day),String.valueOf(hour),String.valueOf(min));

                                LoginInputListener listener = (LoginInputListener) getActivity();
                                listener.onLoginInputComplete(strDate);
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );
        return builder.create();
    }

    private class AsyncAlarm extends AsyncTask<String,Integer,String> {
        private Context mContext;

        public AsyncAlarm (Context context){
            mContext = context;
        }
        @Override
        protected String doInBackground(String... params) {
            Log.i("log",params[0]);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(params[3]), Integer.parseInt(params[4]), Integer.parseInt(params[5]),Integer.parseInt(params[6]),Integer.parseInt(params[7]));

            Intent i = new Intent( mContext, DisplayNotification.class );

            i.putExtra( "event", params[1]);
            i.putExtra( "time",  params[2]);
            i.putExtra( "id",  Long.parseLong(params[0]));

            PendingIntent displayIntent = PendingIntent.getBroadcast(mContext,(int)Long.parseLong(params[0]), i, 0 );

            AlarmManager alarmManager = ( AlarmManager )mContext.getSystemService( ALARM_SERVICE );
            //---sets the alarm to trigger---
            alarmManager.set( AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()-(1000 * 60 * 30), displayIntent);
            return null;
        }

    }

    private void writeToFile(File fout, String data) {
        FileOutputStream osw = null;
        try {
            osw = new FileOutputStream(fout,true);
            osw.write(data.getBytes());
            osw.flush();
        } catch (Exception e) {

        } finally {
            try {
                osw.close();
            } catch (Exception e) {
                ;
            }
        }
    }


    private String CorrectMonth(int month){
        if(month<=9) {
            cmonth="0"+month;
            return cmonth;
        }else{
            cmonth=String.valueOf(month);
            return cmonth;
        }
    }
}