package com.groupjob.mapapp;

import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BingMapsAPI {

    private static final String BASE_URL = "https://dev.virtualearth.net/REST/v1/Routes?";

    private static final String DISTANCE_UNIT = "km";
    private static final String OBJECT = "json";
    private static final String LANGUAGE = "en-GB";
    private static final String BING_MAPS_API = "AhMQZ7O9SDC6vb9-KhffcrSDJnaJeBBlERbMgq_SwyrKZYH0URAvk5vGDpc9AoFY";
    private static final String TIME_UNIT = "second";

    public static String[] getInformationArray(LatLng location, LatLng destination) {
        String request = buildRequestUrlString(location.toString(), destination.toString());
        Log.d("FULL REQUEST STRING: ", request);
        System.out.println("FULL REQUEST STRING: " + request);
        try {
            String jsonString = sendRequest(request);
            Log.d("JSON STRING RESPONSE", jsonString);
            JSONObject response = new JSONObject(jsonString);

            return new String[]{
                    String.valueOf(response.getJSONArray("resourceSets")
                            .getJSONObject(0)
                            .getJSONArray("resources")
                            .getJSONObject(0)
                            .getDouble("travelDistance")),

                    String.valueOf(response.getJSONArray("resourceSets")
                            .getJSONObject(0)
                            .getJSONArray("resources")
                            .getJSONObject(0)
                            .getDouble("travelDuration"))
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

    private static String buildRequestUrlString(String location, String destination) {
        StringBuilder fullUrl = new StringBuilder(BASE_URL);
        fullUrl.append("waypoint.1=" + cleanCoordinateString(location));
        fullUrl.append("&waypoint.2=" + cleanCoordinateString(destination));
        fullUrl.append("&distanceUnit=" + DISTANCE_UNIT);
        fullUrl.append("&o=" + OBJECT);
        fullUrl.append("&c=" + LANGUAGE);
        fullUrl.append("&key=" + BING_MAPS_API);
        fullUrl.append("&timeUnit=" + TIME_UNIT);

        Log.d("FULL URL: ", fullUrl.toString());
        return fullUrl.toString();
    }

    private static String sendRequest(String requestUrl) throws Exception {
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
//        return responseString;
    }

    private static String cleanCoordinateString(String coordinateString) {
        String coordinate = coordinateString.replace("lat/lng: (", "");
        coordinate = coordinate.replace(")", "");
        Log.d("CLEAN COORDINATES: ", coordinate);
        return coordinate;
    }

}
