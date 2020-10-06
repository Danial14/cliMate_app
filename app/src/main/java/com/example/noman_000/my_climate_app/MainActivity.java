package com.example.noman_000.my_climate_app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST = 12;

    public static final int CITY_REQUEST = 11;

    private LocationManager locationManager;

    private LocationListener locationListener;

    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";

    private static final String API_KEY = "9d5079afc6b5b142faf2b6a3a51e19ee";

    private ImageView weather;

    private TextView temp;

    private TextView city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weather = findViewById(R.id.currentWeather);
        temp = findViewById(R.id.currentTemp);
        city = findViewById(R.id.city);
        ImageButton changeCity = findViewById(R.id.changeCity);
        changeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChangeCity.class);
                startActivityForResult(intent, CITY_REQUEST);
            }
        });
        getWeatherForCurrentLocation();
    }

    private void getWeatherForCurrentLocation(){
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"requestPerMission", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
        }
        else{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    RequestParams requestParams = new RequestParams();
                    requestParams.put("lat", String.valueOf(location.getLatitude()));
                    requestParams.put("lon", String.valueOf(location.getLongitude()));
                    requestParams.put("appid", API_KEY);
                    letsDoSomeNetworking(requestParams);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1000, locationListener);
        }
    }
    private void letsDoSomeNetworking(RequestParams requestParams){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Weather weather = Weather.fromJSON(response);
                updateUI(weather);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void getWeatherForCity(String city){
        RequestParams requestParams = new RequestParams();
        requestParams.put("q", city);
        requestParams.put("appid", API_KEY);
        letsDoSomeNetworking(requestParams);
    }

    private void updateUI(Weather weather){
        temp.setText(weather.getTemp());
        city.setText(weather.getCity());
        int iMageId = getResources().getIdentifier(weather.getWeatherSymbol(), "drawable", getPackageName());
        this.weather.setImageResource(iMageId);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getWeatherForCurrentLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CITY_REQUEST && resultCode == RESULT_OK && data != null){
            String city = data.getStringExtra("city");
            if(!city.equals("")) {
                getWeatherForCity(city);
            }
        }
    }
}
