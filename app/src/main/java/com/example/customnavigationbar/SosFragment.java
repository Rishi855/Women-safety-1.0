package com.example.customnavigationbar;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class SosFragment extends Fragment {

    boolean sosCall=false,sosMessage=false,shakeMessage=false;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS=0;
    private static final int MY_PERMISSIONS_REQUEST_AUTO_CALL=1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION=2;
    public String lat = "";
    public String lon = "";
    FusedLocationProviderClient mFusedLocationClient;
    public SosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sos, container, false);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
//        getLastLocation();
        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
//        String s1 = sh.getString("name", "");
//        int a = sh.getInt("age", 0);
        sosCall = sh.getBoolean("sosCall",false);
        sosMessage = sh.getBoolean("sosMessage",false);
        shakeMessage = sh.getBoolean("shakeMessage",false);
        if(sosCall)
        {
            int check_permission_call = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
            if(check_permission_call == PackageManager.PERMISSION_GRANTED){
                String dial = "tel:" + "9022739688";
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                Toast.makeText(getActivity(), "Call sent", Toast.LENGTH_SHORT).show();
            }
            else{FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flFragment,new SettingFragment());
                fragmentTransaction.commit();
            }
        }

        if(sosMessage)
        {
            int check_permission_location = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
            if(check_permission_location == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SmsManager smsManager = SmsManager.getDefault();
                        if(lat.length()!=0)
                        {
                            smsManager.sendTextMessage("9022739688", null, "https://maps.google.com/?q="+lat+","+lon, null, null);
                            Toast.makeText(getActivity(), "Message sent : "+lon+" "+lat, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 5000);

            }
            else{FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flFragment,new SettingFragment());
                fragmentTransaction.commit();
            }
        }
//        Toast.makeText(getActivity(), ""+sosCall, Toast.LENGTH_SHORT).show();
//        int check_permission_sms = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.SEND_SMS);
//        if(check_permission_sms == PackageManager.PERMISSION_GRANTED){
//            SmsManager smsManager = SmsManager.getDefault();
//            for(int i=0;i<5;i++)
//            {
//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
//
//                    }
//                }, 1000);
//            }
//            smsManager.sendTextMessage("9022739688", null, "https://maps.google.com/?q="+lat+","+lon, null, null);
//            Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
//        }
//        else{FragmentManager fragmentManager = getParentFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.flFragment,new SettingFragment());
//            fragmentTransaction.commit();
//        }
        return view;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {

            if (isLocationEnabled()) {

                if(mFusedLocationClient==null) return;
//                else Toast.makeText(getActivity(), "Not Empty location", Toast.LENGTH_SHORT).show();
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        lat = location.getLatitude()+"";
                        lon = location.getLongitude()+"";
//                        Toast.makeText(getActivity(), ""+lat+" "+lon, Toast.LENGTH_SHORT).show();

                    }
                });
            } else {
                Toast.makeText(getActivity(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//
//    }
}