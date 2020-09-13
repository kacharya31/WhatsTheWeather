package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MainActivity extends AppCompatActivity {

    TextView cityText;
    TextView weatherTextView;

    public class JSONDownload extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                String result = "";

                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;

                    result += current;

                    data = reader.read();
                }

                return result;


            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
        }
        @Override
        protected void onPostExecute (String s){
            super.onPostExecute(s);
            try {
                String main = "";
                String description = "";
                String temperatureCelsius = "";
                String feels_like_Celsius = "";
                String temperatureFahrenheit = "";
                String feels_like_Fahrenheit = "";

                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");

                JSONArray weatherArr = new JSONArray(weatherInfo);

                for (int i = 0; i < weatherArr.length(); i++) {
                    JSONObject weatherObject= weatherArr.getJSONObject(i);
                    main = weatherObject.getString("main");
                    description = weatherObject.getString("description");
                }

                String temperatureInfo = jsonObject.getString("main");

                JSONObject temperatureArr = new JSONObject(temperatureInfo);

                temperatureCelsius = String.valueOf(round((Double.parseDouble(temperatureArr.getString("temp"))), 1));
                feels_like_Celsius = String.valueOf(round((Double.parseDouble(temperatureArr.getString("feels_like"))), 1));
                temperatureFahrenheit = String.valueOf(round((Double.parseDouble(temperatureCelsius)*1.8)+32, 1));
                feels_like_Fahrenheit = String.valueOf(round((Double.parseDouble(feels_like_Celsius)*1.8)+32, 1));

                weatherTextView.setText(main + ": " + description + "\n\n" + "Temperature: " + temperatureCelsius + "˚C" + " or " + temperatureFahrenheit + "˚F" + "\n\n" + "Feels like: " + feels_like_Celsius + "˚C" + " or " + feels_like_Fahrenheit + "˚F");
            } catch (Exception e) {
                e.printStackTrace();
                weatherTextView.setText("No weather data available.");
            }
        }
    }

    public void weatherButton(View view) throws UnsupportedEncodingException {

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        JSONDownload task = new JSONDownload();
        String result = null;
        String temp = cityText.getText().toString();
        weatherTextView.setText("");
        String restUrl = URLEncoder.encode(temp, "UTF-8");

        try {
            if (!(restUrl.equals(""))) {
                result = task.execute("https://openweathermap.org/data/2.5/weather?q=" + restUrl + "&appid=439d4b804bc8187953eb36d2a8c26a02").get();
            } else {
                Toast.makeText(this, "Please enter a city", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        cityText = (TextView) findViewById(R.id.cityPlainText);
        weatherTextView = (TextView) findViewById(R.id.weatherTextView);

        cityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weatherTextView.setText("");
            }
        });

    }
}