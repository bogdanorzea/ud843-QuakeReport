package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Earthquake> extractEarthquakes(String query_url) {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        String response = null;

        try {
            response = makeUSGSRequest(query_url);
        } catch (IOException e) {
            Log.e("QueryUtils", "Problem freeing up resources", e);
        }

        if(TextUtils.isEmpty(response)){
            return null;
        }

        try {
            JSONObject rootJson = new JSONObject(response);
            JSONArray earthquakeJsonArray = rootJson.getJSONArray("features");
            int len = earthquakeJsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject currentEarthquake = earthquakeJsonArray.getJSONObject(i);
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                double magnitude = properties.getDouble("mag");
                String location = properties.getString("place");
                long time = properties.getLong("time");
                String url = properties.getString("url");
                Earthquake tempEarthquake = new Earthquake(magnitude, location, time, url);

                earthquakes.add(tempEarthquake);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

    private static String makeUSGSRequest(String requestUrl) throws IOException {
        String result = null;

        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error while creating URL", e);
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream in = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                in = urlConnection.getInputStream();
                result = readStream(in);
            } else {
                Log.e(LOG_TAG, "HTTP response code was: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Cannot open the connection to the URL.");
        } finally {
            // Clean-up in case of exception
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (in != null) {
                // Function signature must include the IOException because of this call to close
                in.close();
            }
        }

        return result;
    }

    private static String readStream(InputStream in) {
        StringBuilder output = new StringBuilder();

        if (in != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(in, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = null;
            try {
                line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error reading the response stream.");
            }
        }

        return output.toString();
    }
}