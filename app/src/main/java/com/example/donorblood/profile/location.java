package com.example.donorblood.profile;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.donorblood.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;


import org.jetbrains.annotations.Nullable;

public class location extends Fragment implements OnMapReadyCallback {



        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
        private double latitude;
        private double longitude;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_location, container, false);

            Button btnPickLocation = view.findViewById(R.id.btnSaveLocation);
            btnPickLocation.setOnClickListener(v -> checkLocationPermissionAndOpenMap());

            return view;
        }

        private void checkLocationPermissionAndOpenMap() {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                openMapWithCurrentLocation();
            }
        }

        @SuppressLint("MissingPermission")
        private void openMapWithCurrentLocation() {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Uri mapUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Your+Location)");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openMapWithCurrentLocation();
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}


