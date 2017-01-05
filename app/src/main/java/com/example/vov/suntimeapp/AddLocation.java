package com.example.vov.suntimeapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vov.suntimeapp.calc.AstronomicalCalendar;
import com.example.vov.suntimeapp.calc.GeoLocation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class AddLocation extends AppCompatActivity implements TextWatcher {

    private HashMap<String, Location> locations = new HashMap<String, Location>();
    private String cityName=null;
    private ArrayList<String> data = new ArrayList<>();
    private DatePickFrag datePickerFragment;
    private ListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        loadDataFromFile();
        initializeUI();
        datePickerFragment = (DatePickFrag) getSupportFragmentManager().findFragmentById(R.id.fragment2);
        listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment3);
    }

    public void buttonClicked(View view)
    {
        ArrayList<Date> dates = datePickerFragment.getDates();
        if(locations.get(cityName)==null ){
            Toast.makeText(getApplicationContext(), "Please select a valid location first!", Toast.LENGTH_SHORT).show();
        }
        else if(dates.size()==0)
        {
            Toast.makeText(getApplicationContext(), "Please select valid dates!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            listFragment.setListView(getDataForDates(dates));
        }

    }

    private void initializeUI()
    {
        String[] cities = getCityNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);
        AutoCompleteTextView atv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView3);
        atv.addTextChangedListener(this);
        atv.setAdapter(adapter);
    }

    private ArrayList<String> getDataForDates(List<Date> dates)
    {
        ArrayList<String> newData = new ArrayList<>();
        Location loc = locations.get(cityName);
        TimeZone tz = TimeZone.getTimeZone(loc.getTimeZone());
        GeoLocation geolocation = new GeoLocation(cityName, loc.getLat(), loc.getLng(), tz);
        AstronomicalCalendar ac = new AstronomicalCalendar(geolocation);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        for(Date date : dates) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            int monthOfYear = cal.get(Calendar.MONTH)+1;
            int year = cal.get(Calendar.YEAR);
            ac.getCalendar().set(year, monthOfYear, dayOfMonth);
            String sRise = sdf.format(ac.getSunrise());
            String sSet = sdf.format(ac.getSunset());
            newData.add(dayOfMonth+"/"+monthOfYear+"/"+year+"          "+sRise+"       "+sSet);
        }
        return newData;
    }

    private void loadDataFromFile()
    {
        try{
            InputStream fileInputStream = getResources().openRawResource(R.raw.au_locations);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line=null;
            while((line = bufferedReader.readLine())!=null)
            {
                String[] data = line.split(",");
                locations.put(data[0], new Location(Double.parseDouble(data[1]), Double.parseDouble(data[2]), data[3]));
                //Log.i("LoadDataFromFile","Data added "+data[0]+data[3]);
            }
        }
        catch (Exception e)
        {
            Log.i("LoadDataFromFile", "Error in reading from file and adding data to hashmap.");
        }
    }

    private String[] getCityNames()
    {
        String[] cities = new String[locations.size()];
        cities = locations.keySet().toArray(cities);
        return cities;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        AutoCompleteTextView tv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView3);
        this.cityName = tv.getText().toString();
    }
}
