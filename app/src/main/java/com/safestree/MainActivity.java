package com.safestree;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.safestree.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    boolean sosCall=false,sosMessage=false,shakeMessage=false;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS=0;
    private static final int MY_PERMISSIONS_REQUEST_AUTO_CALL=1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION=2;
    public String lat = "";
    public String lon = "";
    FusedLocationProviderClient mFusedLocationClient;
    public String dial;

    ActivityMainBinding binding;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
        try {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            // ... rest of body of onCreateView() ...
        } catch (Exception e) {
            Log.e("TAGG", "HERE YOU HAVE TO CHANGE"+e, e);
            throw e;
        }
        setContentView(binding.getRoot());

        replaceFragment(new SettingFragment());
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setSelectedItemId(R.id.setting);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId()== R.id.location)
            {
                replaceFragment(new LocationFragment());
            }
            else if(item.getItemId()== R.id.emergencyContacts)
            {
                replaceFragment(new EmergencyContantFragment());
            }
            else if(item.getItemId()== R.id.sosAlertFab)
            {
//                replaceFragment(new SosFragment());
            }
            else if(item.getItemId()== R.id.setting)
            {
                replaceFragment(new SettingFragment());
            }
            else if(item.getItemId()== R.id.help)
            {
                replaceFragment(new HelpFragment());
            }
            return true;
        });
        FloatingActionButton floatingActionButton=findViewById(R.id.sosAlertAction);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                SharedPreferences sh = MainActivity.this.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
                dial = sh.getString("emergencyContact", "");                sosCall = sh.getBoolean("sosCall",false);
                sosMessage = sh.getBoolean("sosMessage",false);
                shakeMessage = sh.getBoolean("shakeMessage",false);
                if(sosCall && !dial.equals(""))
                {
                    int check_permission_call = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE);
                    if(check_permission_call == PackageManager.PERMISSION_GRANTED){
                        String dialCall = "tel:" + dial;
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dialCall)));
                        Toast.makeText(MainActivity.this, "Call sent", Toast.LENGTH_SHORT).show();
                    }
                }

                if(sosMessage && !dial.equals(""))
                {
                    int check_permission_location = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                    if(check_permission_location == PackageManager.PERMISSION_GRANTED){
                        getLastLocation();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SmsManager smsManager = SmsManager.getDefault();
                                SharedPreferences sh = MainActivity.this.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                                String tempLat = sh.getString("lat","");
                                String tempLon = sh.getString("lon","");
                                if(tempLat.length()!=0)
                                {
                                    smsManager.sendTextMessage(dial, null, "Your contact has made an emergency alert and last location was: "+"https://maps.google.com/?q="+tempLat+","+tempLon, null, null);
                                    Toast.makeText(MainActivity.this, "Message sent : "+lon+" "+lat, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, 5000);

                    }
                }
            }
        });
    }
    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment,fragment);
        fragmentTransaction.commit();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {

            if (isLocationEnabled()) {

                if(mFusedLocationClient==null) return;
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        lat = location.getLatitude()+"";
                        lon = location.getLongitude()+"";
                        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("lat",lat);
                        myEdit.putString("lon",lon);
                        myEdit.apply();
                    }
                });
            } else {
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager)MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}