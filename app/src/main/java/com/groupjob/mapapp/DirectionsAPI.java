package com.groupjob.mapapp;

import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class DirectionsAPI {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String API_KEY = "AIzaSyB782Og2LrmyAij_bZJ9yOM5BvxC_1IBAU";


    public static String[] getInformationArray(LatLng location, LatLng destination) {
        String request = buildFullUrl(destination, location);
        Log.d("FULL REQUEST STRING: ", request);
        System.out.println("FULL REQUEST STRING: " + request);
        try {
            String jsonString = sendRequest(request);
            Log.d("JSON STRING RESPONSE", jsonString);
            JSONObject response = new JSONObject(jsonString);

            return new String[]{
                    getDistance(response.toString()),
                    getTime(response.toString()),
                    getCity(response.toString()),
                    getPolyline(response.toString())
            };
        } catch (JSONException | IOException e) {
            Log.d("DEBUGGING", e.getMessage());
            e.printStackTrace();
            return new String[]{};
        } catch (Exception e) {
            Log.d("DEBUGGING", ":"+e.getMessage());
            e.printStackTrace();
            return new String[]{};
        }
    }

    private static String sendRequest(String requestUrl) throws IOException {
        StringBuilder response = new StringBuilder();

        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (connection.getResponseCode() != 200) {
            System.out.println("Response Code: " + connection.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String responseStream;
        while ((responseStream = br.readLine()) != null) {
            response.append(responseStream);
        }
        connection.disconnect();

        String responseString = response.toString().replace("\"", "\\\"");        System.out.println(responseString);
        Log.d("Response String: ", responseString);
        return response.toString();
    }

    private static String buildFullUrl(LatLng destination, LatLng origin){
        StringBuilder fullUrl = new StringBuilder(BASE_URL);
        fullUrl.append("destination="+cleanCoordinateString(destination));
        fullUrl.append("&origin="+cleanCoordinateString(origin));
        fullUrl.append("&key="+API_KEY);


        return fullUrl.toString();
    }

    private static String getDistance(String jsonString){
        JSONObject obj = null;
        String distanceText;
        try {
            obj = new JSONObject(jsonString);
            JSONArray routes = obj.getJSONArray("routes");
            JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
            distanceText = legs.getJSONObject(0).getJSONObject("distance").getString("text");
            return distanceText;
        } catch (JSONException e) {
            Log.d("DEBUGGING", "Error parsing json" + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    private static String getTime(String jsonString){
        JSONObject obj = null;
        String timeText;
        try {
            obj = new JSONObject(jsonString);
            JSONArray routes = obj.getJSONArray("routes");
            JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
            timeText = legs.getJSONObject(0).getJSONObject("duration").getString("text");
            return timeText;
        } catch (JSONException e) {
            Log.d("DEBUGGING", "Error parsing json" + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    private static String cleanCoordinateString(LatLng coordinate) {
        String coordinateString = coordinate.toString().replace("lat/lng: (", "");
        coordinateString = coordinateString.replace(")", "");
        Log.d("CLEAN COORDINATES: ", coordinateString);
        return coordinateString;
    }

    private static String getCity(String jsonString){
        JSONObject obj = null;
        try {
            obj = new JSONObject(jsonString);
            JSONArray routes = obj.getJSONArray("routes");
            JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
            String endAddress = legs.getJSONObject(0).getString("end_address");
            return endAddress;
        } catch (JSONException e) {
            Log.d("DEBUGGING", "Error parsing json" + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    private static String getPolyline(String jsonString){
        JSONObject obj = null;
        try {
            obj = new JSONObject(jsonString);
            JSONArray routes = obj.getJSONArray("routes");
            JSONArray legs = routes.getJSONObject(0).getJSONArray("overview_polyline");
            String points = legs.getJSONObject(0).getString("points");
            return points;
        } catch (JSONException e) {
            Log.d("DEBUGGING", "Error parsing json" + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
}
