package com.safestree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.safestree.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    FirebaseAuth auth;
    FirebaseUser authUser;

    boolean sosCall=false,sosMessage=false,shakeMessage=false;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS=0;
    private static final int MY_PERMISSIONS_REQUEST_AUTO_CALL=1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION=2;

    private String connectedDeviceAdd = "";
    private BluetoothDevice connectedDevice;

    private BluetoothAdapter bluetoothAdapter;
    private TextView connectionStatusTextView;
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

        FirebaseMessaging.getInstance().subscribeToTopic("notification");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        authUser = auth.getCurrentUser();

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
                dial = sh.getString("emergencyContact", "");
                sosCall = sh.getBoolean("sosCall",false);
                sosMessage = sh.getBoolean("sosMessage",false);
                shakeMessage = sh.getBoolean("shakeMessage",false);
                float safeLatitude = sh.getFloat("safeLatitude",0);
                float safeLongitude = sh.getFloat("safeLongitude",0);
                float safeRadius = sh.getFloat("safeRadius",0);

                Location locationSafe = new Location("LocationB");
                locationSafe.setLatitude(safeLatitude);
                locationSafe.setLongitude(safeLongitude);

                String tempLat = "",tempLon="";
                Location current = new Location("LocationA");

                int check_permission_location = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                if(check_permission_location == PackageManager.PERMISSION_GRANTED){
                    getLastLocation();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("temp", "run: done");
                        }
                    }, 5000);
                    tempLat = sh.getString("lat","");
                    tempLon = sh.getString("lon","");
                    if(!tempLat.equals(""))
                    {
                        current.setLatitude(Float.parseFloat(tempLat));
                        current.setLongitude(Float.parseFloat(tempLon));
                    }
                    else {
                        return;
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Please give location permission and turn on location", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(safeLatitude!=0 && safeRadius!=0){
                    double distance = locationSafe.distanceTo(current);
                    if(distance<=safeRadius*1000)
                    {
                        Toast.makeText(MainActivity.this, "Your are at safe location", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

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
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(dial, null, "Your contact has made an emergency alert and last location was: "+"https://maps.google.com/?q="+tempLat+","+tempLon, null, null);
                    try {
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String strDate = sdf.format(c.getTime());
                        Toast.makeText(getApplicationContext(), ""+strDate, Toast.LENGTH_SHORT).show();
                        mDatabase.child("users").child(authUser.getUid()).child("SosTriggered").child(strDate).setValue("lat:"+tempLat+" lon:"+tempLon);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Not updated", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(MainActivity.this, "Message sent : "+lon+" "+lat, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.setCustomAnimations(
//                R.anim.slide_in_from_right,  // enter animation
//                R.anim.slide_out_to_left,    // exit animation
//                R.anim.slide_in_from_left,   // pop enter animation
//                R.anim.slide_out_to_right    // pop exit animation
//        );
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
//    private boolean checkConnectedDevices(String connectedDeviceAdd) {
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            // Request the necessary permission if it's not granted
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 1);
//        } else {
//            // Replace "YOUR_DEVICE_ADDRESS_HERE" with the actual Bluetooth device address
////            String deviceAddress = "00:11:22:33:44:55"; // Replace with the actual address
//
//            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(connectedDeviceAdd);
//            Toast.makeText(this, ""+device+"LOL", Toast.LENGTH_SHORT).show();
//            if (device != null) {
////                bluetoothGatt = device.connectGatt(this, false, gattCallback);
//                return false;
//            }
//            else
//                return true;
//        }
//        return false;
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            checkConnectedDevices();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now send SMS
            } else {
                // Permission denied, handle this gracefully (e.g., show a message or disable SMS-related features)
            }
        }
    }
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//
//            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
//                // Get the BluetoothDevice object from the Intent
//                BluetoothDevice device = intent
//                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                connectedDeviceAdd = device.getAddress();
//                Toast.makeText(context, connectedDeviceAdd+"", Toast.LENGTH_SHORT).show();
//                connectedDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(connectedDeviceAdd);
//                checkConnectedDevices(connectedDeviceAdd);
//                Toast.makeText(context, "Device connected", Toast.LENGTH_SHORT).show();
//                connectionStatusTextView.setText("Connected");
//
//
//            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
//                Log.e("TAG", "Device Disconnected");
//                Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
//                connectionStatusTextView.setText("Disconnected");
//
//                String dial = "9022739688"; // Replace with the recipient's actual phone number
//                String message = "Hello";
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage(dial, null, message, null, null);
//
//            }
//        }
//    };


}