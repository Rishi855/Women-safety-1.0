package com.safestree;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Objects;

public class MyService extends Service {
    private SensorManager mSensorManager;
    private int request_Code = 101;
    private float mAccel;
    private Button b;
    //    EditText number;
//    private String number;
    private float mAccelCurrent;
    public String lat = "";
    public String lon = "";
    public String dial = "";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION=2;
    FusedLocationProviderClient mFusedLocationClient;
    int addLevel;
    private float mAccelLast;
    private static final String TAG = "MyService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        return null;
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
//        startForeground(9999, onStartCommand())
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MyService.this);
    }


    protected void sendSMSMessage() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "NOOOOOOOO", Toast.LENGTH_SHORT).show();
        } else {
            getLastLocation();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SmsManager smsManager = SmsManager.getDefault();
                    SharedPreferences sh = MyService.this.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    String tempLat = sh.getString("lat","");
                    String tempLon = sh.getString("lon","");
                    Location current = new Location("LocationA");
                    current.setLatitude(Float.parseFloat(tempLat));
                    current.setLongitude(Float.parseFloat(tempLon));

                    float safeLatitude = sh.getFloat("safeLatitude",0);
                    float safeLongitude = sh.getFloat("safeLongitude",0);
                    float safeRadius = sh.getFloat("safeRadius",0);
                    Location locationSafe = new Location("LocationB");
                    locationSafe.setLatitude(safeLatitude);
                    locationSafe.setLongitude(safeLongitude);

                    if(safeLatitude!=0 && safeRadius!=0){
                        double distance = locationSafe.distanceTo(current);
                        if(distance<=safeRadius*1000)
                        {
                            Toast.makeText(getApplicationContext(), "Your are at safe location", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if(tempLat.length()!=0)
                    {
                        smsManager.sendTextMessage(dial, null, "Your contact has made an emergency alert and last location was: "+"https://maps.google.com/?q="+tempLat+","+tempLon, null, null);
                        Toast.makeText(MyService.this, "Message sent : "+lon+" "+lat, Toast.LENGTH_SHORT).show();
                    }
                }
            }, 6000);
        }
    }


    //    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        String phone_Number = "9022739688";
//        if (intent.getAction().equals("OnStopNotification")) {
//            stopForeground(true);
//            stopSelfResult(startId);
//        }

//        int level = intent.getIntExtra("level",0);
//        Toast.makeText(this, ""+level, Toast.LENGTH_SHORT).show();
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String level = sh.getString("level", "level-1");
        dial = sh.getString("emergencyContact","");
        if(dial.equals(""))
        {
            Toast.makeText(this, "Please select emergency number" , Toast.LENGTH_SHORT).show();
            return START_STICKY;
        }
//        Toast.makeText(this, "Service: " + level, Toast.LENGTH_SHORT).show();
        addLevel = 0;
        switch (level) {
            case "Level-1":
                addLevel = 0;
                break;
            case "Level-2":
                addLevel = 20;
                break;
            case "Level-3":
                addLevel = 40;
                break;
            case "Level-4":
                addLevel = 60;
                break;
            case "Level-5":
                addLevel = 80;
                break;
            default:
                addLevel = 0;
        }
        SensorEventListener mSensorListener;
//        Toast.makeText(this, "N", Toast.LENGTH_SHORT).show();

        mSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
//                Toast.makeText(MyService.this, "abs" , Toast.LENGTH_SHORT).show();
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                mAccelLast = mAccelCurrent;
                mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
                float delta = mAccelCurrent - mAccelLast;
                mAccel = mAccel * 0.9f + delta;
                Log.d(TAG, "onSensorChanged: " + mAccel);
//                Toast.makeText(MyService.this, "mAccel"+mAccel, Toast.LENGTH_SHORT).show();
                int temp = addLevel + 20;

                if (mAccel > temp) {
                    Toast.makeText(getApplicationContext(), "Shake event detected by services" + " and " + temp, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MyService.this, "Message sent by service" + mAccel, Toast.LENGTH_SHORT).show();
                    sendSMSMessage();

//                    makePhoneCall();
//                }

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
//        Toast.makeText(this, "e", Toast.LENGTH_SHORT).show();
        final String CHANNELID = "foreground service id";

        Intent stopSelf = new Intent(this, MainActivity.class);
//        stopSelf.putExtra("Stop",true);
//        stopSelf.setAction("OnStopNotification");
        stopSelf.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 1, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_MUTABLE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            CharSequence name = "Channel name";
//            String description = "Channel desription";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationChannel channel = new NotificationChannel(
                    CHANNELID,
                    CHANNELID,
                    NotificationManager.IMPORTANCE_LOW
            );
//            channel.setDescription(description);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                    .setContentText("Service is running")
                    .setContentTitle("Service enables")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setSound(uri)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_DEFAULT);
//                    .addAction(R.drawable.ic_launcher_foreground,"Stop",pendingIntent);
//            notification.notify(0,mb);
            startForeground(1001, notification.build());
//            Toast.makeText(this, "Notification could be create", Toast.LENGTH_SHORT).show();


//            NotificationCompat.Builder builder = new NotificationCompat.Builder()


//            stopSelf.setAction("Remove");
//            PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//            notification.addAction(R.drawable.ic_launcher_foreground, "Stop", pStopSelf);
//            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            manager.notify(0, notification.build());

        }
//        return super.onStartCommand(intent,flags,startId);

        return START_STICKY;
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
                        SharedPreferences sharedPreferences = MyService.this.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("lat",lat);
                        myEdit.putString("lon",lon);
                        myEdit.apply();
//                        Toast.makeText(getActivity(), ""+lat+" "+lon, Toast.LENGTH_SHORT).show();

                    }
                });
            } else {
                Toast.makeText(MyService.this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
//            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MyService.this);
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MyService.this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }



    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}