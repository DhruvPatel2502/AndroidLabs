package com.cst2335.pate;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {

    protected static final String URL_STRING = "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric";
    protected static final String URL_IMAGE = "http://openweathermap.org/img/w/";
    protected static final String UV_STRING = "http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389";
    protected static final String ACTIVITY_NAME = "WeatherForecast";
    private ProgressBar normProgBar;
    private ImageView weatherImageView;
    private TextView currentTextView, minTextView, maxTextView, targetLocation, uvTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        targetLocation = findViewById(R.id.targetLocation);
        weatherImageView = findViewById(R.id.currentWeatherImageView);
        currentTextView = findViewById(R.id.currentTempTextView);
        minTextView = findViewById(R.id.minTempTextView);
        maxTextView = findViewById(R.id.maxTempTextView);
        uvTextView = findViewById(R.id.uvRating);
        normProgBar = findViewById(R.id.progressBar);
        normProgBar.setVisibility(View.VISIBLE);
        normProgBar.setMax(100);

        new ForecastQuery().execute(null, null, null);

    }

    //Type1     Type2   Type3
    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        //private class MyHTTPRequest extends AsyncTask< String, Integer, String>

        private String currentTemp = null;
        private String minTemp = null;
        private String maxTemp = null;
        private String uv = null;
        private String iconFilename = null;
        private Bitmap weatherImage = null;
        private String currLocation = null;

        static private final String TAG = "ForecastQuery";

        //Type3                     Type1
        protected String doInBackground(String... args) {

            InputStream inputStream = null;

            try {

                //create a URL object of what server to contact:
                URL url = new URL(URL_STRING);
                //URL url = new URL(args[0]);

                //open the connection
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                inputStream = conn.getInputStream();



                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(inputStream, null);
                int eventType = parser.getEventType();
                boolean set = false;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.getName().equalsIgnoreCase("current")) {
                            set = true;
                        } else if (parser.getName().equalsIgnoreCase("city") && set) {
                            currLocation = parser.getAttributeValue(null, "name");
                        } else if (parser.getName().equalsIgnoreCase("temperature") && set) {
                            currentTemp = parser.getAttributeValue(null, "value");
                            publishProgress(25);
                            minTemp = parser.getAttributeValue(null, "min");
                            publishProgress(50);
                            maxTemp = parser.getAttributeValue(null, "max");
                            publishProgress(75);
                            //uv = parser.getAttributeValue(null,"max");
                        } else if (parser.getName().equalsIgnoreCase("weather") && set) {
                            iconFilename = parser.getAttributeValue(null, "icon") + ".png";
                            File file = getBaseContext().getFileStreamPath(iconFilename);
                            if (!file.exists()) {
                                saveImage(iconFilename);
                            } else {
                                Log.i(ACTIVITY_NAME, "Saved icon, " + iconFilename + " is displayed.");
                                try {
                                    FileInputStream in = new FileInputStream(file);
                                    weatherImage = BitmapFactory.decodeStream(in);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    Log.i(ACTIVITY_NAME, "Saved icon, " + iconFilename + " is not found.");
                                }
                            }
                            publishProgress(100);

                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (parser.getName().equalsIgnoreCase("current"))
                            set = false;
                    }
                    eventType = parser.next();
                }

            } catch (IOException e) {
                Log.i(ACTIVITY_NAME, "IOException: " + e.getMessage());
            } catch (XmlPullParserException e) {
                Log.i(ACTIVITY_NAME, "XmlPullParserException: " + e.getMessage());
            }try{

                //create a URL object of what server to contact:
                URL url = new URL(UV_STRING);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                //JSON reading:
                //Build the entire string response:
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString(); //result is the whole string


                // convert string to JSON: Look at slide 27:
                JSONObject uvReport = new JSONObject(result);

                //get the double associated with "value"
                uv = uvReport.getString("value");

                publishProgress(25);
                Thread.sleep(1000);
                publishProgress(50);
                Log.i(TAG, "Num of entries: " + uv) ;

            }catch (Exception e)
            {

            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.i(ACTIVITY_NAME, "IOException: " + e.getMessage());
                    }
                }
                return null;
            }

        }

        //Type 2
        @Override
        public void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            normProgBar.setProgress(values[0]);
            if (values[0] == 100) {

            }
        }

        //Type3
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            targetLocation.setText("Weather report for " + currLocation);
            currentTextView.setText("Current " + String.format("%.1f", Double.parseDouble(currentTemp)) + "\u00b0");
            minTextView.setText("Min " + String.format("%.1f", Double.parseDouble(minTemp)) + "\u00b0");
            maxTextView.setText("Max " + String.format("%.1f", Double.parseDouble(maxTemp)) + "\u00b0");
            uvTextView.setText("UV " + uv);
            weatherImageView.setImageBitmap(weatherImage);
            normProgBar.setVisibility(View.INVISIBLE);
        }

        private void saveImage(String fname) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(URL_IMAGE + fname);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    weatherImage = BitmapFactory.decodeStream(connection.getInputStream());
                    FileOutputStream outputStream = openFileOutput(fname, Context.MODE_PRIVATE);
                    weatherImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    Log.i(ACTIVITY_NAME, "Weather icon, " + fname + " is downloaded and displayed.");
                } else
                    Log.i(ACTIVITY_NAME, "Can't connect to the weather icon for downloading.");
            } catch (Exception e) {
                Log.i(ACTIVITY_NAME, "weather icon download error: " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }

            }
        }
    }
}