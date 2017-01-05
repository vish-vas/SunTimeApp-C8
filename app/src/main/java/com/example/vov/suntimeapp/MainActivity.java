package com.example.vov.suntimeapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import com.example.vov.suntimeapp.calc.GeoLocation;
import com.example.vov.suntimeapp.calc.AstronomicalCalendar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements TextWatcher, OnMapReadyCallback
{
    private GoogleMap mMap;
    private HashMap<String, Location> locations = new HashMap<String, Location>();
    private String cityName=null;
    String sRise=null;
    String sSet=null;
    String date=null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadDataFromFile();
        initializeUI();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater mf = getMenuInflater();
        mf.inflate(R.menu.main_activity_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.shareSms:
                if(cityName==null || sRise==null || sSet==null || date==null) {
                    Toast.makeText(getApplicationContext(), "Please choose a valid location/date first!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String body = "Sunrise/Sunset Details\nLocation: "+cityName+"\nDate: "+date+"\nSunrise: "+sRise+"\nSunset: "+sSet+"\n\nFrom SunTimeApp.";
                    Uri uri = Uri.parse("smsto:");
                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                    it.putExtra("sms_body", body);
                    startActivity(it);
                }
                return true;
            case R.id.generateSuntimes:
                startActivity(new Intent(getApplicationContext(), com.example.vov.suntimeapp.AddLocation.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeUI()
    {
        String[] cities = getCityNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, cities);
        AutoCompleteTextView atv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        atv.addTextChangedListener(this);
        atv.setAdapter(adapter);
    }

    private void updateTime(int year, int monthOfYear, int dayOfMonth)
    {
        Location loc = locations.get(cityName);
        TimeZone tz = TimeZone.getTimeZone(loc.getTimeZone());
        GeoLocation geolocation = new GeoLocation(cityName, loc.getLat(), loc.getLng(), tz);
        AstronomicalCalendar ac = new AstronomicalCalendar(geolocation);
        ac.getCalendar().set(year, monthOfYear, dayOfMonth);
        Date srise = ac.getSunrise();
        Date sset = ac.getSunset();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        TextView sunriseTV = (TextView) findViewById(R.id.sunriseTimeTV);
        TextView sunsetTV = (TextView) findViewById(R.id.sunsetTimeTV);
        Log.d("SUNRISE Unformatted", srise+"");

        sunriseTV.setText(sdf.format(srise));
        sunsetTV.setText(sdf.format(sset));
        sRise = sdf.format(srise);
        sSet = sdf.format(sset);
        this.date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        LatLng location = new LatLng(loc.getLat(), loc.getLng());
        mMap.addMarker(new MarkerOptions().position(location).title(cityName));
        mMap.setMinZoomPreference(10);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(location));
    }

    DatePicker.OnDateChangedListener dateChangeHandler = new DatePicker.OnDateChangedListener()
    {
        public void onDateChanged(DatePicker dp, int year, int monthOfYear, int dayOfMonth)
        {
            date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
            updateTime(year, monthOfYear, dayOfMonth);
            //Log.i("dateChanged","d1:  -- "+dp.getId());
        }
    };

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
        AutoCompleteTextView tv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        this.cityName = tv.getText().toString();
        if(locations.get(cityName)!=null)
        {
            DatePicker dp = (DatePicker) findViewById(R.id.datePicker);
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            dp.init(year,month,day,dateChangeHandler); // setup initial values and reg. handler
            this.date = day+"/"+(month+1)+"/"+year;
            updateTime(year, month, day);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-25.2744, 133.7751)));
    }
}