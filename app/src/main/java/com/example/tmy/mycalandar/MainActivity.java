package com.example.tmy.mycalandar;


import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity
        implements DayViewDecorator, BasicDialogFragment.LoginInputListener{
    String syear,smonth,sday;
    JSONObject obj = new JSONObject();
    MaterialCalendarView calendarview;

    ArrayList<String> myList = new ArrayList<String>();
    ListView listview;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    int color;
    HashSet<CalendarDay> dates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarview = (MaterialCalendarView) findViewById(R.id.simpleCalendarView);
        calendarview.setDateSelected(CalendarDay.today(), true);


        Calendar calendar = Calendar.getInstance();
        String thisYear =String.valueOf(calendar.get(Calendar.YEAR));
        String thisMonth = "0"+String.valueOf(calendar.get(Calendar.MONTH)+1);
        Log.i("log",thisMonth);
        showDot(thisYear,thisMonth,calendarview);

        listview = (ListView) findViewById(R.id.listview);


        calendarview.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                myList.clear();
                syear=(date.getDate().toString()).split("-")[0];
                smonth=(date.getDate().toString()).split("-")[1];
                String formatdate=date.getDate().format(FORMATTER);
                sday=(date.getDate().toString()).split("-")[2];

                File dir = widget.getContext().getFilesDir();
                File outFile = new File(dir, syear+"_"+smonth+".txt");
                String mydata=readFromFile(outFile);
                String[] data_arr=mydata.split("&");
                for (int i=0; i<data_arr.length;i++) {
                    try {
                        JSONObject obj1 = new JSONObject(data_arr[i]);
                        String getdate = obj1.getString("date");
//                        Log.i("log","formatdate:"+formatdate);
//                        Log.i("log","getdate:"+getdate);

                        if(formatdate.equals(getdate)){
                            String getevent=obj1.getString("edit");
                            String gettime=obj1.getString("time");

                            myList.add(gettime+" "+getevent);
                        }else{
                            //do nothing
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(myList.size()==0){
                    Toast.makeText(widget.getContext(),"這天沒有任何活動!",Toast.LENGTH_SHORT).show();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(widget.getContext(), android.R.layout.simple_list_item_1, myList);
                listview.setAdapter(adapter);
            }
        });

        calendarview.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                syear=(date.getDate().toString()).split("-")[0];
                smonth=(date.getDate().toString()).split("-")[1];
                showDot(syear,smonth,widget);

            }
        });
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            showBDialog();
        }
        return super.onOptionsItemSelected(item);
    }


    private void showBDialog() {
        BasicDialogFragment dialog = new BasicDialogFragment();
        dialog.show(getFragmentManager(), "exDialog");

    }


    public void onLoginInputComplete(String pass) {
        Collection<CalendarDay> dates=new ArrayList<>();
        dates.add(CalendarDay.from(Integer.parseInt(pass.split("-")[0]),Integer.parseInt(pass.split("-")[1]),Integer.parseInt(pass.split("-")[2])));

        calendarview.addDecorator(new EventDecorator(Color.RED,dates));
        showDot(pass.split("-")[0],pass.split("-")[1],calendarview);


    }


    private String readFromFile(File fin) {
        StringBuilder data = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fin), "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (Exception e) {
            //
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                //
            }
        }
        return data.toString();
    }

    public void showDot(String Year,String Month,MaterialCalendarView calendarview){
        File dir = this.getFilesDir();
        File outFile = new File(dir, Year+"_"+Month+".txt");
        String mydata=readFromFile(outFile);
        String[] data_arr=mydata.split("&");
        for (int i=0; i<data_arr.length;i++) {
            try {
                JSONObject obj1 = new JSONObject(data_arr[i]);
                String date = obj1.getString("date");
                int year = Integer.parseInt(date.split("-")[0]);
                int month = Integer.parseInt(date.split("-")[1]);
                int day = Integer.parseInt(date.split("-")[2]);

                Collection<CalendarDay> dates=new ArrayList<>();
                dates.add(CalendarDay.from(year,month,day));

                calendarview.addDecorator(new EventDecorator(Color.RED,dates));;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5, color));
    }

}

