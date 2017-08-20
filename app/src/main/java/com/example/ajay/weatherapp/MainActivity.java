package com.example.ajay.weatherapp;

import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class MainActivity extends AppCompatActivity {

    private static final String TAG =MainActivity.class.getSimpleName();
    private CurrentWeather mCurrentWeather;

    private TextView temperature;
    private TextView humadity;
    private TextView percepitation;
    private TextView time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature = (TextView) findViewById(R.id.temperatureLabel);
        humadity = (TextView) findViewById(R.id.humadity);
        percepitation = (TextView) findViewById(R.id.percipitation);
        time = (TextView) findViewById(R.id.tvtime);

        String apiKey="5fd00c32df6fb3a6c92cf23ababc4b05";
        double longitude=23.6703;
        double latitude=77.5536;
        String forecastUrl = "https://api.darksky.net/forecast/"+apiKey+"/"+longitude+","+latitude;

        if(isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG,jsonData) ;
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updatedisplay();
                                }
                            });

                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception Caught", e);
                    }
                    catch (JSONException e)
                    {
                        Log.e(TAG, "Exception Caught", e);
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "Network not available", Toast.LENGTH_LONG).show();
        }
    }
    private void updatedisplay() {


    }

    private CurrentWeather getCurentDetails(String jsonData) throws JSONException {

        JSONObject forecast = new JSONObject(jsonData);
        String timeZone = forecast.getString("timezone");
        Log.i(TAG,"timezone"+timeZone);

        JSONObject currently = forecast.getJSONObject("currently");

        final CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPercepChange(currently.getDouble("precipProbability"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setTimeZone(timeZone);

        Log.d(TAG,currentWeather.getFormattedTime());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                temperature.setText( currentWeather.getTemperature()+"");
                time.setText("At "+currentWeather.getFormattedTime()+"it will be");
                humadity.setText(currentWeather.getHumidity()+"");
                percepitation.setText(currentWeather.getPercepChange()+"%");
            }
        });
        return new CurrentWeather();

    }

    private boolean isNetworkAvailable() {

        boolean isAvailable = false;

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            isAvailable =true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        dialogFragment.show(getFragmentManager(),"error dialog");

    }
}
