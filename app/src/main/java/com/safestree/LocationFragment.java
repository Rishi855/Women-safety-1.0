package com.safestree;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.core.content.ContextCompat;

public class LocationFragment extends Fragment {

//    private MapView mMapView;
//    private GoogleMap googleMap;
//    private FusedLocationProviderClient fusedLocationClient;
//    private Marker selectedMarker;
//    boolean isLocationEnabled;

    private WebView webView;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        webView = view.findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Enable JavaScript to interact with Android
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        // Load the HTML file from the assets folder
        webView.loadUrl("file:///android_asset/map.html");

        // Enable location permissions for WebView
        webView.setWebChromeClient(new WebChromeClient());

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Check and request location permission
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            updateLocation();
        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location updates
                updateLocation();
            }
        }
    }

    // Define a JavaScript interface for communication with Android
    public class WebAppInterface {
        @JavascriptInterface
        public void getLocation() {
            // Implement your logic to retrieve and pass location to JavaScript
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateLocation();
                }
            });
        }
    }

    private void updateLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                // Pass location to JavaScript
                                webView.evaluateJavascript(
                                        "updateLocation(" + latitude + "," + longitude + ");",
                                        null
                                );
                            }
                        }
                    });
        }
    }

}


//package com.example.customnavigationbar;
//
//        import android.app.AlertDialog;
//        import android.content.Context;
//        import android.content.DialogInterface;
//        import android.content.pm.PackageManager;
//        import android.location.Location;
//        import android.os.Bundle;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.Manifest;
//
//        import androidx.annotation.NonNull;
//        import androidx.core.app.ActivityCompat;
//        import androidx.fragment.app.Fragment;
//
//        import com.google.android.gms.location.FusedLocationProviderClient;
//        import com.google.android.gms.location.LocationCallback;
//        import com.google.android.gms.location.LocationRequest;
//        import com.google.android.gms.location.LocationResult;
//        import com.google.android.gms.location.LocationServices;
//        import com.google.android.gms.maps.CameraUpdateFactory;
//        import com.google.android.gms.maps.GoogleMap;
//        import com.google.android.gms.maps.MapView;
//        import com.google.android.gms.maps.MapsInitializer;
//        import com.google.android.gms.maps.OnMapReadyCallback;
//        import com.google.android.gms.maps.model.LatLng;
//        import com.google.android.gms.maps.model.Marker;
//        import com.google.android.gms.maps.model.MarkerOptions;
//        import com.google.android.gms.tasks.OnSuccessListener;
//        import android.Manifest;
//        import android.content.pm.PackageManager;
//        import android.location.Location;
//        import android.media.Image;
//        import android.os.Bundle;
//        import android.view.View;
//        import android.webkit.JavascriptInterface;
//        import android.webkit.WebChromeClient;
//        import android.webkit.WebSettings;
//        import android.webkit.WebView;
//        import android.widget.Button;
//        import android.widget.ImageButton;
//
//        import androidx.annotation.NonNull;
//        import androidx.appcompat.app.AppCompatActivity;
//        import androidx.core.app.ActivityCompat;
//        import androidx.core.content.ContextCompat;
//        import com.google.android.gms.location.FusedLocationProviderClient;
//        import com.google.android.gms.location.LocationServices;
//        import com.google.android.gms.tasks.OnSuccessListener;
//
//        import android.content.Intent;
//        import android.location.LocationManager;
//        import android.provider.Settings;
//
//public class LocationFragment extends Fragment implements OnMapReadyCallback {
//
////    private MapView mMapView;
////    private GoogleMap googleMap;
////    private FusedLocationProviderClient fusedLocationClient;
////    private Marker selectedMarker;
////    boolean isLocationEnabled;
//
//    private WebView webView;
//    private FusedLocationProviderClient fusedLocationClient;
//
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_location, container, false);
//
//        mMapView = rootView.findViewById(R.id.mapView);
//        mMapView.onCreate(savedInstanceState);
//
//        mMapView.onResume(); // needed to get the map to display immediately
//
//        try {
//            MapsInitializer.initialize(getActivity().getApplicationContext());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
//
//        mMapView.getMapAsync(this);
//
//        return rootView;
//    }
//
//    @Override
//    public void onMapReady(GoogleMap mMap) {
//        googleMap = mMap;
//        googleMap.clear();
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        googleMap.setMyLocationEnabled(true);
//
//        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
//        isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//        if (!isLocationEnabled) {
//            // Prompt user to enable location
//            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//            builder.setTitle("Location Services Disabled")
//                    .setMessage("Please enable location services to use this feature.")
//                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // Redirect to device settings screen
//                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            startActivity(intent);
//                        }
//                    })
//                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // Handle cancellation
//                        }
//                    })
//                    .setCancelable(false)
//                    .show();
//        } else {
//            // Location is enabled, continue with your existing code
//            googleMap.setMyLocationEnabled(true);
//
//            // Rest of your code...
//        }
//        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                placeMarker(latLng);
//            }
//        });
//
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            fusedLocationClient.getLastLocation()
//                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            if (location != null) {
//                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                                placeMarker(currentLocation); // Place marker at the current location by default
//                                moveCameraToLocation(currentLocation);
//                            }
//                        }
//
//                    });
//        } else {
//            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
//    }
//
//    private void checkOnOf()
//    {
//
//    }
//
//    private void placeMarker(LatLng latLng) {
//        if (selectedMarker != null) {
//            selectedMarker.remove();
//        }
//        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Selected Location");
//        selectedMarker = googleMap.addMarker(markerOptions);
//    }
//
//    private void moveCameraToLocation(LatLng latLng) {
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
////        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
////        isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        mMapView.onResume();
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            fusedLocationClient.getLastLocation()
//                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            if (location != null) {
//                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                                placeMarker(currentLocation); // Place marker at the current location by default
//                                moveCameraToLocation(currentLocation);
//                                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                    // TODO: Consider calling
//                                    //    ActivityCompat#requestPermissions
//                                    // here to request the missing permissions, and then overriding
//                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                    //                                          int[] grantResults)
//                                    // to handle the case where the user grants the permission. See the documentation
//                                    // for ActivityCompat#requestPermissions for more details.
//                                    return;
//                                }
//                                googleMap.setMyLocationEnabled(true);
//                            } else {
//                                // If location is null, request location updates
//                                fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, null);
//                            }
//                        }
//
//                    });
//        } else {
//            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
//    }
//
//    private LocationRequest createLocationRequest() {
//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(10000);
//        return locationRequest;
//    }
//
//    private LocationCallback locationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            if (locationResult != null) {
//                for (Location location : locationResult.getLocations()) {
//                    if (location != null) {
//                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                        placeMarker(currentLocation); // Place marker at the current location by default
//                        moveCameraToLocation(currentLocation);
//
//                        // Stop location updates
//                        fusedLocationClient.removeLocationUpdates(locationCallback);
//                    }
//                }
//            }
//        }
//    };
//
//
//
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mMapView.onPause();
////        isLocationEnabled=false;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mMapView.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mMapView.onLowMemory();
//    }
//
//}
