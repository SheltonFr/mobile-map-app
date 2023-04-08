package com.groupjob.mapapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import com.groupjob.mapapp.databinding.*;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final double KILOMETRO = 1000d;
    private static final double TRES_CASAS_DECIMAIS = 1000d;
    private LatLng myCurrentLocation;
    private GoogleMap mMap;
    private ActivityMainBinding binding;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static ArrayList<Marker> destinationMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        destinationMarkers = new ArrayList<>();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

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

    private void askUserPermission(){
        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    /**
     * Adiciona um novo marcador no mapa na coordenada passada.
     * Remove todos outros marcadores do mapa.
     * @param latLng
     */
    private void selectDestination(@NonNull LatLng latLng){
        for (Marker marker: destinationMarkers) {
            marker.remove();
        }
        LatLng destination = new LatLng(latLng.latitude, latLng.longitude);
        Marker newDestination = mMap.addMarker(new MarkerOptions().position(destination).title("DESTINO"));
        destinationMarkers.add(newDestination);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
        computeDistance(latLng);
    }

    private void computeDistance(LatLng latLng){
        if(this.myCurrentLocation == null){
            showToast("Ligue a localização e reinicie o aplicativo");
            return;
        }

        LatLng myLocation = this.myCurrentLocation;
//        LatLng destination = destinationMarkers.get(0).getPosition();
        LatLng destination = latLng;
        double distance = SphericalUtil.computeDistanceBetween(myLocation, destination) / KILOMETRO;

        double roundedDistance = (double) Math.round(distance * TRES_CASAS_DECIMAIS) / TRES_CASAS_DECIMAIS;

        binding.distancia.setText(Double.toString(roundedDistance) + " KM");

    }

    private void setCurrentLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            myCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.setMyLocationEnabled(true);
                        }else{
                            showToast("Ligue a localização e reinicie o aplicativo");
                        }
                    });
        }else{
            askUserPermission();
            mMap.setMyLocationEnabled(true); // todo: This should be invoked only if the user grants the permission

        }
    }

    private void showToast(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }
}