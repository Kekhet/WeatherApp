package com.example.kekhet.lab12;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    double lon, lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SpinnerAdapter adapter;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);






        final Spinner spinner = (Spinner) findViewById(R.id.spinner);


        adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                City city = adapter.getItem(position);
                // Here you can do the action you want to...
                //Toast.makeText(MainActivity.this, "Name: " + city.getName(), Toast.LENGTH_SHORT).show();
                try {
                    getWeatherData(city);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });



    }


    static final City[] cities = City.setDefault();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    void getWeatherData(City city) throws JSONException {


        final TextView cityNameTV = (TextView) findViewById(R.id.cityNameTV);
        final TextView coordTV = findViewById(R.id.coordinatesTV);
        final TextView tempTV = findViewById(R.id.temperatureTV);
        final TextView windTV = findViewById(R.id.windTV);
        final TextView presTV = findViewById(R.id.pressureTV);
        final ImageView image = findViewById(R.id.imageView);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                finishActivity(2);
            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(getApplicationContext(), "GPS disabled", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
            }
        };

        String url = "http://api.openweathermap.org/data/2.5/weather?lat=";
        if (city.getName().equals("GPS")) {
           // JSONObject coord = getGPS();




            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
                return;
            } else {
                locationManager.requestLocationUpdates("gps", 5000, 20, locationListener);
            }
            url += lat;
            url += "&lon=";
            url += lon;
            url += "&units=metric&appid=aecedf95ec03fd187d64f8f0573f5577";
            Log.d("URL", url);

        } else {
            url += city.getLat();
            url += "&lon=";
            url += city.getLon();
            url += "&units=metric&appid=aecedf95ec03fd187d64f8f0573f5577";
            Log.d("URL", url);
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (com.android.volley.Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("Response", response.toString());
                            cityNameTV.setText(response.getString("name"));

                            JSONArray jarr = response.getJSONArray("weather");
                            JSONObject buffIco = jarr.getJSONObject(0);
                            String ico = "i" + buffIco.getString("icon");
                            int id = getResources().getIdentifier
                                    ("com.example.kekhet.lab12:drawable/" + ico,
                                            null, null);
                            image.setImageResource(id);

                            JSONObject buffArr = response.getJSONObject("coord");
                            String lon = buffArr.getString("lon");
                            String lat = buffArr.getString("lat");
                            coordTV.setText("Latitude: " + lat + " Longitude: " + lon);

                            buffArr = response.getJSONObject("main");
                            String temp = buffArr.getString("temp");
                            tempTV.setText(temp + " C");
                            String press = buffArr.getString("pressure");
                            presTV.setText(press + " hPa");

                            buffArr = response.getJSONObject("wind");
                            String wind = buffArr.getString("speed");
                            windTV.setText(wind + " km/h");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
// Access the RequestQueue through your singleton class.
        //MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

//    JSONObject getGPS() throws JSONException {
//        final JSONObject retVal = new JSONObject();
//
//
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
//
//
//
//        return retVal;
//    }

}
