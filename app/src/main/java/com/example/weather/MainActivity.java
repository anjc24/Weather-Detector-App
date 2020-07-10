package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
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

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);

    }
    public void getWeather(View view){

        DownloadTask task = new DownloadTask();
        task.execute("http://api.openweathermap.org/data/2.5/weather?q="+editText.getText().toString()+"&units=metric&APPID=6ea4e2c62b8128f7afd4b8412176e488");

        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
          String result = "";
          URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }catch(Exception e){
                e.printStackTrace();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(), "Couldn't find weather", Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                String temperatureInfo = jsonObject.getString("main");

                Log.i("Weather", weatherInfo);
                Log.i("temperature", temperatureInfo);
                JSONArray arr = new JSONArray(weatherInfo);
                JSONObject obj = new JSONObject(temperatureInfo);
                String message = "";
                for(int i=0; i<arr.length();i++){
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if(!main.equals("") && !description.equals("")){
                        message += main + " : " + description + "\r\n";
                    }
                }
                String temp = obj.getString("temp");
                String feels = obj.getString("feels_like");
                String tempmin = obj.getString("temp_min");
                String tempmax = obj.getString("temp_max");
                String pressure = obj.getString("pressure");
                String humidity = obj.getString("humidity");
                Log.i("temperatureifo",temp);
                message+="Temperature : " + temp + " °C" + "\r\n";
                message+="Feels Like : " + feels + " °C" + "\r\n";
                message+="Min Temperature : " + tempmin + " °C" + "\r\n";
                message+="Max Temperature : " + tempmax + " °C" + "\r\n";
                message+="Humidity : " + humidity + " %" + "\r\n";
                message+="Pressure : " + pressure + " mbar" + "\r\n";

                if(!message.equals("")){
                    resultTextView.setText(message);
                }else{
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e)
            {    e.printStackTrace();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(), "Couldn't find weather", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}