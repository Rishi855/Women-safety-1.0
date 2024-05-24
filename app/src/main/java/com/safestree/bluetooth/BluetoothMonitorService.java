package com.safestree.bluetooth;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class BluetoothMonitorService extends Service {
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver mReceiver;
    private static final String NOTIFICATION_CHANNEL_ID = "MyBluetoothChannel";

    // Notification ID (can be any unique number)
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Register the Bluetooth broadcast receiver
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String connectedDeviceAddress = device.getAddress();
                    Toast.makeText(context, "Connected to device: " + connectedDeviceAddress, Toast.LENGTH_SHORT).show();
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    String dial = "9022739688"; // Replace with the recipient's actual phone number
                    String message = "Hello";
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(dial, null, message, null, null);
                    Toast.makeText(context, "Bluetooth device disconnected", Toast.LENGTH_SHORT).show();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start the service in the foreground with a notification
        startForeground(1, createNotification());

        return START_STICKY;
    }

    private Notification createNotification() {
        // Create a notification channel for Android Oreo and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Bluetooth Monitor",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            // Customize the notification channel (e.g., description, importance, etc.)
            // ...

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create a notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_notification_icon) // Replace with your notification icon
                .setContentTitle("Bluetooth Monitor")
                .setContentText("Monitoring Bluetooth connections...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true); // Make the notification ongoing (cannot be dismissed by the user)

        // You can customize other notification properties as needed
        // ...

        // Build and return the notification
        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister the Bluetooth broadcast receiver
        unregisterReceiver(mReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
