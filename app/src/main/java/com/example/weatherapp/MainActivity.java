package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
{
    EditText city;
    TextView weatherResult;

    public  class DownloadTask extends AsyncTask<String, Void, String>
    {

        //Done only in the background, separately from ui
        @Override
        protected String doInBackground(String... urls)
        {
            StringBuilder result = new StringBuilder();

            URL url;
            HttpURLConnection urlConnection;

            try
            {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1)
                {
                    char ch = (char) data;
                    result.append(ch);
                    data = reader.read();
                }
                return result.toString();
            } catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        //To touch something on the UI. Done after execution
        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);

            try
            {
                JSONObject jsonObject = new JSONObject(s);
                String weather = jsonObject.getString("weather");

                JSONArray jsonArray = new JSONArray(weather);

                String result = "";

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonPart = jsonArray.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if ( !main.equals("") && !description.equals("") )
                    {
                        result += main + ": " +description;
                    }
                    else Toast.makeText( getApplicationContext(), "Could not get weather :(",Toast.LENGTH_LONG).show();
                }

                weatherResult.setText(result);
            } catch (Exception e)
            {
                Toast.makeText( getApplicationContext(), "Could not get weather :(",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }


    public void getWeather(View v)
    {
        try
        {
            //Make sure to get correct string for link
            String encodedCityName = URLEncoder.encode(city.getText().toString(), "UTF-8");

            DownloadTask task = new DownloadTask();
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=439d4b804bc8187953eb36d2a8c26a02");

            //Remove keyboard when button clicked
            InputMethodManager mg = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mg.hideSoftInputFromWindow(city.getWindowToken(), 0);
        }
        catch ( Exception e)
        {
            Toast.makeText( getApplicationContext(), "Could not get weather :(",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = (EditText) findViewById(R.id.city);
        weatherResult = (TextView) findViewById(R.id.weather);
    }
}