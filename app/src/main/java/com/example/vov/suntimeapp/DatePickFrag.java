package com.example.vov.suntimeapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DatePickFrag extends Fragment {

    private static DatePicker dp1;
    private static DatePicker dp2;
    private String date1=null;
    private String date2=null;

    public DatePickFrag()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_pick, container, false);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        dp1 = (DatePicker) view.findViewById(R.id.datePicker2);
        dp1.init(year,month,day,dateChangeHandler);
        dp2 = (DatePicker) view.findViewById(R.id.datePicker3);
        dp2.init(year,month,day,dateChangeHandler);
        date1 = day + "-" + (month + 1) + "-" + year;
        date2 = date1;
        return view;
    }

    DatePicker.OnDateChangedListener dateChangeHandler = new DatePicker.OnDateChangedListener()
    {
        public void onDateChanged(DatePicker dp, int year, int monthOfYear, int dayOfMonth)
        {
                if(dp==dp1) {
                    date1 = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                }
                if (dp==dp2)
                {
                    date2 = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                }
        }

    };

    public ArrayList<Date> getDates()
    {
        ArrayList<Date> dates = new ArrayList<>();
        try {
            DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
            Date start = df1.parse(date1);
            Date end = df1.parse(date2);
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(start);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(end);
            while(!cal1.after(cal2))
            {
                dates.add(cal1.getTime());
                cal1.add(Calendar.DATE, 1);
            }
        }
        catch (Exception e)
        {
            Log.i("getDate-DatePickFrag", "exception:"+e.getMessage());
        }
        return dates;
    }



}
