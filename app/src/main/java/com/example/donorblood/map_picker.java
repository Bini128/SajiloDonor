package com.example.donorblood;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.HashMap;
import java.util.Map;

public class map_picker extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap mMap;
    private LatLng selectedLatLng;
    private Marker currentMarker;
    private FusedLocationProviderClient fusedLocationClient;

    // Kathmandu bounds
    private static final LatLngBounds KATHMANDU_BOUNDS = new LatLngBounds(
            new LatLng(27.65, 85.20), // Southwest
            new LatLng(27.80, 85.40)  // Northeast
    );

    private String userEmail;  // Assume you pass or get email from SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        userEmail = prefs.getString("email", "");

        Log.d("SessionEmail", "Loaded email: " + userEmail);

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User not logged in. Please login again.", Toast.LENGTH_SHORT).show();
            // Optionally redirect to login activity
            finish(); // close current activity
            return;
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Now you can use fusedLocationClient safely
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        // Use the location object
                        Log.d("Location", "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                        // Do something with location
                    } else {
                        Log.d("Location", "Location is null");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Location", "Failed to get location", e);
                });

        // Continue your logic here



    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

 Button btnSave = findViewById(R.id.btnSaveLocation);
        btnSave.setOnClickListener(v -> saveSelectedLocation());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Restrict camera to Kathmandu bounds
        mMap.setLatLngBoundsForCameraTarget(KATHMANDU_BOUNDS);
        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(18.0f);

        LatLng kathmanduCenter = new LatLng(27.7172, 85.3240);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kathmanduCenter, 13));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            zoomToUserLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        mMap.setOnMapClickListener(latLng -> {
            selectedLatLng = latLng;

            if (currentMarker != null) currentMarker.remove();

            currentMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Selected Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        });
    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions not granted
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                if (KATHMANDU_BOUNDS.contains(userLatLng)) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(27.7172, 85.3240), 13));
                }
            }
        });
    }

    private void saveSelectedLocation() {
        if (selectedLatLng != null) {
            double lat = selectedLatLng.latitude;
            double lng = selectedLatLng.longitude;

            // Show toast
            Toast.makeText(this, "Saved: " + lat + ", " + lng, Toast.LENGTH_SHORT).show();

            // Send lat,lng to server to update user location
            sendLocationToServer(lat, lng);

        } else {
            Toast.makeText(this, "Please select a location on the map.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendLocationToServer(double lat, double lng) {
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User email not found, cannot update location.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.18.17/Sajilodonor/update_location.php"; // <-- Change to your URL

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Location updated successfully on server.", Toast.LENGTH_SHORT).show();

                    // After successful update, you may return to dashboard
                    Intent intent = new Intent(map_picker.this, dashboard.class);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lon", lng);
                    startActivity(intent);
                    finish();

                },
                error -> {
                    Toast.makeText(this, "Failed to update location: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", userEmail);
                params.put("latitude", String.valueOf(lat));
                params.put("longitude", String.valueOf(lng));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                zoomToUserLocation();
            }
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
