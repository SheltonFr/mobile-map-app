package com.groupjob.mapapp;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  FONTE PRIMARIA: https://developers.google.com/maps/documentation/routes/compute_route_directions
 *  BREVE DESCRICAO: Esta classe usa Google Routes API para encontrar a distancia e tempo de viagem de carro entre duas LatLong
 */
public class RouteDistanceTime {

    private static final String API_KEY = "AIzaSyDQDEPMcXgETn-yN__zU2gSYHfTGfk9pr4";
    private static final String API_URL = "https://routes.googleapis.com/directions/v2:computeRoutes";

    /*
    public static void main(String[] args) throws Exception {
        URL url = new URL("https://routes.googleapis.com/directions/v2:computeRoutes");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        // Set request headers
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("X-Goog-Api-Key", "AIzaSyDQDEPMcXgETn-yN__zU2gSYHfTGfk9pr4");
        con.setRequestProperty("X-Goog-FieldMask", "routes.duration,routes.distanceMeters,routes.polyline.encodedPolyline");

        // Construct request body as a JSON object
        JSONObject requestBody = new JSONObject();
        requestBody.put("origin", new JSONObject()
                .put("location", new JSONObject()
                        .put("latLng", new JSONObject()
                                .put("latitude", 37.419734)
                                .put("longitude", -122.0827784)
                        )
                )
        );
        requestBody.put("destination", new JSONObject()
                .put("location", new JSONObject()
                        .put("latLng", new JSONObject()
                                .put("latitude", 37.417670)
                                .put("longitude", -122.079595)
                        )
                )
        );
        requestBody.put("travelMode", "DRIVE");
        requestBody.put("routingPreference", "TRAFFIC_AWARE");
        requestBody.put("departureTime", "2023-04-08T15:30:45.1234560Z");
//        requestBody.put("departureTime", "2023-10-15T15:01:23.045123456Z"); // Original
        requestBody.put("computeAlternativeRoutes", false);
        requestBody.put("routeModifiers", new JSONObject()
                .put("avoidTolls", false)
                .put("avoidHighways", false)
                .put("avoidFerries", false)
        );
        requestBody.put("languageCode", "en-US");
        requestBody.put("units", "IMPERIAL");

        // Send the request
        con.setDoOutput(true);
        byte[] requestBodyBytes = requestBody.toString().getBytes("utf-8");
        con.setRequestProperty("Content-Length", Integer.toString(requestBodyBytes.length));
        con.getOutputStream().write(requestBodyBytes);

        // Read the response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Print the response as a string
        System.out.println(response.toString());

        // Parse the response as a JSON object
        JSONObject responseBody = new JSONObject(response.toString());
        // Do something with the response body
    }
*/

    public static String getDistance(LatLng location, LatLng destination) throws JSONException {
        JSONObject responseBody = new JSONObject();
        try {
            HttpURLConnection connection = buildConnection();
            JSONObject requestBody = buildRequestBody(location, destination);
            responseBody = sendRequest(connection, requestBody);
            return String.valueOf(responseBody.getJSONArray("routes")
                    .getJSONObject(0)
                    .getInt("distanceMeters"));
        } catch (IOException e) {
            Log.d("EXCEPTION-LOG", "Conexao mal formada!");
        } catch (JSONException e) {
            Log.d("EXCEPTION-LOG", "RequestBody mal formado!");
        }
        return String.valueOf(
                responseBody.getJSONArray("routes")
                        .getJSONObject(0)
                        .getInt("distanceMeters"));
    }

    @NonNull
    public static JSONObject buildRequestBody(LatLng currentLocation, LatLng destination) throws JSONException {
        // Construct request body as a JSON object
        JSONObject requestBody = new JSONObject();
        requestBody.put("origin", new JSONObject()
                .put("location", new JSONObject()
                        .put("latLng", new JSONObject()
                                .put("latitude", currentLocation.latitude)
                                .put("longitude", currentLocation.longitude)
                        )
                )
        );
        requestBody.put("destination", new JSONObject()
                .put("location", new JSONObject()
                        .put("latLng", new JSONObject()
                                .put("latitude", destination.latitude)
                                .put("longitude", destination.longitude)
                        )
                )
        );
        requestBody.put("travelMode", "DRIVE");
        requestBody.put("routingPreference", "TRAFFIC_AWARE");
        requestBody.put("departureTime", getCurrentTimeStamp());
//        requestBody.put("departureTime", "2023-10-15T15:01:23.045123456Z"); // Original
        requestBody.put("computeAlternativeRoutes", false);
        requestBody.put("routeModifiers", new JSONObject()
                .put("avoidTolls", false)
                .put("avoidHighways", false)
                .put("avoidFerries", false)
        );
        requestBody.put("languageCode", "en-US");
        requestBody.put("units", "IMPERIAL");
        return requestBody;
    }

    @NonNull
    public static HttpURLConnection buildConnection() throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        // Set request headers
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("X-Goog-Api-Key", API_KEY);
        con.setRequestProperty("X-Goog-FieldMask", "routes.duration,routes.distanceMeters,routes.polyline.encodedPolyline");
        return con;
    }

    private static JSONObject sendRequest(@NonNull HttpURLConnection con, @NonNull JSONObject requestBody) throws IOException, JSONException {
        con.setDoOutput(true);
        byte[] requestBodyBytes = requestBody.toString().getBytes("utf-8");
        con.setRequestProperty("Content-Length", Integer.toString(requestBodyBytes.length));
        con.getOutputStream().write(requestBodyBytes);

        // Read the response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Print the response as a string
//        System.out.println(response.toString());

        return  new JSONObject(response.toString());
    }

    public static String getCurrentTimeStamp(){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'");
        // Exemplo de output esperado: 2023-04-08T15:30:45.1234560Z
        return localDateTime.format(formatter);
    }
}
