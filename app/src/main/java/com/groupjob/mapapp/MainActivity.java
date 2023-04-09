package com.groupjob.mapapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import com.groupjob.mapapp.databinding.*;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
//    private static final double KILOMETRO = 1000d;
    private static ArrayList<Marker> destinationMarkers;
    private LatLng myCurrentLocation;
    private GoogleMap mMap;
    private ActivityMainBinding binding;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, SigninActivity.class));
        } else {
            Log.d("EMAIL", auth.getCurrentUser().getEmail());
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        destinationMarkers = new ArrayList<>();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(14.0f);
        mMap.setOnMapClickListener(latLng -> {
            selectDestination(latLng);
            Toast.makeText(MainActivity.this, "NOVO DESTINO SELECIONADO", Toast.LENGTH_LONG).show();
        });

        setCurrentLocation();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setCurrentLocation();
    }

    private void askUserPermission() {
        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

    }

    /**
     * Adiciona um novo marcador no mapa na coordenada passada.
     * Remove todos outros marcadores do mapa.
     *
     * @param latLng
     */
    private void selectDestination(@NonNull LatLng latLng) {
        for (Marker marker : destinationMarkers) {
            marker.remove();
        }
        LatLng destination = new LatLng(latLng.latitude, latLng.longitude);
        Marker newDestination = mMap.addMarker(new MarkerOptions().position(destination).title("DESTINO"));
        destinationMarkers.add(newDestination);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
        computeDistance(latLng);
    }

    private void computeDistance(LatLng destinationCoordinates) {
        if (this.myCurrentLocation == null) {
            showToast("Ligue a localização e reinicie o aplicativo");
            return;
        }
        LatLng myLocation = this.myCurrentLocation;

//        double distance = SphericalUtil.computeDistanceBetween(myLocation, destinationCoordinates) / KILOMETRO;
//        double roundedDistance = (double) Math.round(distance * TRES_CASAS_DECIMAIS) / TRES_CASAS_DECIMAIS;
//        binding.distancia.setText(Double.toString(roundedDistance) + " KM");


        final String[][] informationArray = {new String[2]};
//        new Thread(() -> {
            informationArray[0] = BingMapsAPI.getInformationArray(myLocation, destinationCoordinates);
            if (informationArray[0].length < 2) {
                showToast("Erro no servidor do Bing Maps");
            } else {
                binding.distancia.setText(roundNumber(Double.parseDouble(informationArray[0][0]), 1000d) + " Km");
                binding.tempoEstimado.setText(roundNumber(Double.valueOf(informationArray[0][1])/60, 100d) + " min");
            }
//        });

        //CURRENTLY DOESN'T WORK
//        new Thread(() -> {
//            String[] informationArray = RouteDistanceTime.getInformationArray(myLocation, destinationCoordinates);
//            if (informationArray.length < 2){
//                showToast("Erro no servidor do Google");
//                /// Na verdade não é um erro, para esta funcionalidades eles querem o credit card
//            }else{
//                binding.distancia.setText(informationArray[0]);
//                binding.tempoEstimado.setText(informationArray[1]);
//            }
//        }).start();
    }

    private void setCurrentLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            myCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLocation, 15));
                            mMap.setMyLocationEnabled(true);
                        } else {
                            showToast("Ligue a localização e reinicie o aplicativo");
                        }
                    });
        } else {
            askUserPermission();
            mMap.setMyLocationEnabled(true); // todo: This should be invoked only if the user grants the permission

        }
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private static double roundNumber(double number, double casasDecimais){
        return (double) Math.round(number * casasDecimais) / casasDecimais;
    }
}