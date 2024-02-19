package com.safestree.parentconnect;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.safestree.MainActivity;
import com.safestree.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateCountService extends Service {
    private SensorManager mSensorManager;
    private int request_Code = 101;
    private float mAccel;
    private Button b;
    //    EditText number;
//    private String number;
    private float mAccelCurrent;
//    public String lat = "";
//    public String lon = "";
    public String dial = "";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION=2;
    FusedLocationProviderClient mFusedLocationClient;
    int addLevel;
    private float mAccelLast;
    private static final String TAG = "MyService";

//    private static final int HANDLER_DELAY = 1000 * 60 * 5; // 5 minutes

    private static final int HANDLER_DELAY = 5000; // 5 seconds

    private DatabaseReference mDatabase;
    FirebaseAuth auth;
    FirebaseUser authUser;
    Handler handler = new Handler();

    static int count = 0;
    public LocationTrack locationTrack;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        return null;
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
//        startForeground(9999, onStartCommand())
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(UpdateCountService.this);

    }
    //    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String CHANNELID = "foreground service id";
        Intent stopSelf = new Intent(this, MainActivity.class);
        stopSelf.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(UpdateCountService.this, 1, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_MUTABLE);

        FirebaseMessaging.getInstance().subscribeToTopic("notification");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        authUser = auth.getCurrentUser();
        locationTrack = new LocationTrack();

//        Handler handler1 = new Handler();
//
//        handler1.postDelayed(new Runnable() {
//            public void run() {
//                // Stop the service gracefully
//                stopForeground(true);
//                stopSelf();
//                Toast.makeText(MyService.this, "Service stopped", Toast.LENGTH_SHORT).show();
//
//            }
//        }, HANDLER_DELAY+5000);


        handler.postDelayed(new Runnable() {
            public void run() {
                getLastLocation();
                handler.postDelayed(this, HANDLER_DELAY);
            }
        }, HANDLER_DELAY);

        if (isLocationEnabled()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationChannel channel = new NotificationChannel(
                        CHANNELID,
                        CHANNELID,
                        NotificationManager.IMPORTANCE_DEFAULT
                );

                getSystemService(NotificationManager.class).createNotificationChannel(channel);

                Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                        .setContentText("Service is running")
                        .setContentTitle("Service enables")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setSound(uri)
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .setPriority(Notification.PRIORITY_DEFAULT);

                startForeground(1001, notification.build());

                getLastLocation(); // Get the location if it's enabled
            } else {
                // Handle the case when location is enabled for older Android versions
                getLastLocation(); // Get the location if it's enabled
            }
        } else {
            // Handle the case when location is disabled
            stopSelf(); // Stop the service if location is disabled
            Toast.makeText(UpdateCountService.this, "Please turn on your location...", Toast.LENGTH_LONG).show();
            Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(locationIntent);
        }

        return START_STICKY;
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
//                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
//                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
//                                        String currentTime = sdf.format(new Date());
                        locationTrack.addStatus(count++);
                        mDatabase.child("users").child(authUser.getUid()).child("locationDetails").setValue(locationTrack);
                        Toast.makeText(UpdateCountService.this, "One more added", Toast.LENGTH_SHORT).show();
                    }
                });
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//                if (mFusedLocationClient == null) return;
//                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
//                    Location location = task.getResult();
//                    if (location == null) {
//                        requestNewLocationData();
//                    } else {
//                        double lat = location.getLatitude();
//                        double lon = location.getLongitude();
//                        Log.d("tagg", "Lat: " + lat + ", Lon: " + lon);
//
//                        FirebaseMessaging.getInstance().getToken()
//                                .addOnCompleteListener(new OnCompleteListener<String>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<String> task) {
//                                        if (!task.isSuccessful()) {
////                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                                            return;
//                                        }
//                                        locationTrack.addLocation(lat,lon);
//                                        mDatabase.child("users").child(authUser.getUid()).child("locationDetails").setValue(locationTrack);
//                                        Toast.makeText(MyService.this, "One more added", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    }
//                });
//            } else {
//                Toast.makeText(MyService.this, "Please turn on your location...", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = LocationRequest.create()
                .setInterval(100)
                .setFastestInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(100);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                Log.d("tagg", "Lat: " + lat + ", Lon: " + lon);
                // You can do something with the location here
            }
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(UpdateCountService.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UpdateCountService.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }



    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up resources, unsubscribe from Firebase topics, etc.
        FirebaseMessaging.getInstance().unsubscribeFromTopic("notification");
        handler.removeCallbacksAndMessages(null);
        stopForeground(true);
    }

}



//import android.annotation.SuppressLint;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Looper;
//import android.provider.Settings;
//import android.telephony.SmsManager;
//import android.util.Log;
//import android.widget.Toast;
//import android.Manifest;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.messaging.FirebaseMessaging;
//
//public class MyService extends Service {
//
//    private static final int HANDLER_DELAY = 5000; // 5 seconds
//    private static final String TAG = "MyService";
//
//    private FusedLocationProviderClient mFusedLocationClient;
//    private DatabaseReference mDatabase;
//    private FirebaseAuth auth;
//    private FirebaseUser authUser;
//    private LocationTrack locationTrack;
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        auth = FirebaseAuth.getInstance();
//        authUser = auth.getCurrentUser();
//        locationTrack = new LocationTrack();
//
//
//        Handler handler=new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                getLastLocation();
//                handler.postDelayed(this, HANDLER_DELAY);
////                stopService();
//
//            }
//        }, HANDLER_DELAY);
//
//
//        Handler stopHandler = new Handler();
//        stopHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                stopService();
//                handler.removeCallbacksAndMessages(null);
//                Toast.makeText(MyService.this, "Done", Toast.LENGTH_SHORT).show();
//
//            }
//        }, 10000);
//        return START_STICKY;
//    }
//
//    private void stopService() {
//        stopForeground(true);
//        stopSelf();
//        Intent intent = new Intent(MyService.this, MyService.class);
//        stopService(intent);
//    }
//
//    private void getLastLocation() {
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//                if (mFusedLocationClient == null) return;
//                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
//                    Location location = task.getResult();
//                    if (location == null) {
//                        requestNewLocationData();
//                    } else {
//                        double lat = location.getLatitude();
//                        double lon = location.getLongitude();
//                        Log.d("tagg", "Lat: " + lat + ", Lon: " + lon);
//
//                        FirebaseMessaging.getInstance().getToken()
//                                .addOnCompleteListener(new OnCompleteListener<String>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<String> task) {
//                                        if (!task.isSuccessful()) {
//                                            return;
//                                        }
//                                        locationTrack.addLocation(lat, lon);
//                                        mDatabase.child("users").child(authUser.getUid()).child("locationDetails").setValue(locationTrack);
//                                        Toast.makeText(MyService.this, "One more added", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    }
//                });
//            } else {
//                Toast.makeText(MyService.this, "Please turn on your location...", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private void requestNewLocationData() {
//        LocationRequest mLocationRequest = LocationRequest.create()
//                .setInterval(100)
//                .setFastestInterval(3000)
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setMaxWaitTime(100);
//        mLocationRequest.setNumUpdates(1);
//
//        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//    }
//
//    private LocationCallback mLocationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            super.onLocationResult(locationResult);
//            Location location = locationResult.getLastLocation();
//            if (location != null) {
//                double lat = location.getLatitude();
//                double lon = location.getLongitude();
//                Log.d("tagg", "Lat: " + lat + ", Lon: " + lon);
//            }
//        }
//    };
//
//    private boolean checkPermissions() {
//        return ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private boolean isLocationEnabled() {
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "Service is being stopped.");
//    }
//}


//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.AlarmManager;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.location.Location;
//import android.location.LocationManager;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Looper;
//import android.os.Message;
//import android.os.SystemClock;
//import android.provider.Settings;
//import android.telephony.SmsManager;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.messaging.FirebaseMessaging;
//
//import java.util.Objects;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class MyService extends Service {
//    private SensorManager mSensorManager;
//    private int request_Code = 101;
//    private float mAccel;
//    private Button b;
//    //    EditText number;
////    private String number;
//    private float mAccelCurrent;
//    public String lat = "";
//    public String lon = "";
//    public String dial = "";
//    private static final int MY_PERMISSIONS_REQUEST_LOCATION=2;
//    FusedLocationProviderClient mFusedLocationClient;
//    int addLevel;
//    private float mAccelLast;
//    private static final String TAG = "MyService";
//
////    private static final int HANDLER_DELAY = 1000 * 60 * 5; // 5 minutes
//
//    private static final int HANDLER_DELAY = 5000; // 5 seconds
//
//    private DatabaseReference mDatabase;
//    FirebaseAuth auth;
//    FirebaseUser authUser;
//    Handler handler = new Handler();
//
//    public LocationTrack locationTrack;
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
////        return null;
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    @Override
//    public void onCreate() {
////        startForeground(9999, onStartCommand())
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MyService.this);
//
//    }
//
//
//    //    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        final String CHANNELID = "foreground service id";
//        Intent stopSelf = new Intent(this, MainActivity.class);
//        stopSelf.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 1, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_MUTABLE);
//
//        FirebaseMessaging.getInstance().subscribeToTopic("notification");
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        auth = FirebaseAuth.getInstance();
//        authUser = auth.getCurrentUser();
//        locationTrack = new LocationTrack();
//
//        Handler handler1 = new Handler();
//
//        handler1.postDelayed(new Runnable() {
//            public void run() {
//                // Stop the service gracefully
//                stopForeground(true);
//                stopSelf();
//                // Delay for a moment before restarting the service
////                handler.postDelayed(new Runnable() {
////                    public void run() {
////                        // Create an intent to restart the service
////                        Intent restartServiceIntent = new Intent(getApplicationContext(), MyService.class);
////                        restartServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////
////                        // Start the service
////                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
////                            startForegroundService(restartServiceIntent);
////                        } else {
////                            startService(restartServiceIntent);
////                        }
////                    }
////                }, 1000); // Delay for 1 second before restarting
//                Toast.makeText(MyService.this, "Service stopped", Toast.LENGTH_SHORT).show();
//
//            }
//        }, HANDLER_DELAY+5000);
//
//
//        handler.postDelayed(new Runnable() {
//            public void run() {
////                requestNewLocationData();
//                getLastLocation();
//                handler.postDelayed(this, HANDLER_DELAY);
//            }
//        }, HANDLER_DELAY);
//
//        if (isLocationEnabled()) {
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                NotificationChannel channel = new NotificationChannel(
//                        CHANNELID,
//                        CHANNELID,
//                        NotificationManager.IMPORTANCE_DEFAULT
//                );
//
//                getSystemService(NotificationManager.class).createNotificationChannel(channel);
//
//                Notification.Builder notification = new Notification.Builder(this, CHANNELID)
//                        .setContentText("Service is running")
//                        .setContentTitle("Service enables")
//                        .setSmallIcon(R.drawable.ic_launcher_background)
//                        .setSound(uri)
//                        .setContentIntent(pendingIntent)
//                        .setOngoing(true)
//                        .setPriority(Notification.PRIORITY_DEFAULT);
//
//                startForeground(1001, notification.build());
//
//                getLastLocation(); // Get the location if it's enabled
//            } else {
//                // Handle the case when location is enabled for older Android versions
//                getLastLocation(); // Get the location if it's enabled
//            }
//        } else {
//            // Handle the case when location is disabled
//            stopSelf(); // Stop the service if location is disabled
//            Toast.makeText(MyService.this, "Please turn on your location...", Toast.LENGTH_LONG).show();
//            Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(locationIntent);
//        }
//
//        return START_STICKY;
//    }
//
//
//    @SuppressLint("MissingPermission")
//    private void getLastLocation() {
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//                if (mFusedLocationClient == null) return;
//                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
//                    Location location = task.getResult();
//                    if (location == null) {
//                        requestNewLocationData();
//                    } else {
//                        double lat = location.getLatitude();
//                        double lon = location.getLongitude();
//                        Log.d("tagg", "Lat: " + lat + ", Lon: " + lon);
//
//                        FirebaseMessaging.getInstance().getToken()
//                                .addOnCompleteListener(new OnCompleteListener<String>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<String> task) {
//                                        if (!task.isSuccessful()) {
////                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                                            return;
//                                        }
//                                        locationTrack.addLocation(lat,lon);
//                                        mDatabase.child("users").child(authUser.getUid()).child("locationDetails").setValue(locationTrack);
//                                        Toast.makeText(MyService.this, "One more added", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    }
//                });
//            } else {
//                Toast.makeText(MyService.this, "Please turn on your location...", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private void requestNewLocationData() {
//        LocationRequest mLocationRequest = LocationRequest.create()
//                .setInterval(100)
//                .setFastestInterval(3000)
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setMaxWaitTime(100);
//        mLocationRequest.setNumUpdates(1);
//
//        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//    }
//
//    private LocationCallback mLocationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            super.onLocationResult(locationResult);
//            Location location = locationResult.getLastLocation();
//            if (location != null) {
//                double lat = location.getLatitude();
//                double lon = location.getLongitude();
//                Log.d("tagg", "Lat: " + lat + ", Lon: " + lon);
//                // You can do something with the location here
//            }
//        }
//    };
//
//    private boolean checkPermissions() {
//        return ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//    }
//
//
//
//    private boolean isLocationEnabled() {
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "Service is being stopped.");
//        // You can also create a notification or take other actions here.
//    }
//
//
//}
