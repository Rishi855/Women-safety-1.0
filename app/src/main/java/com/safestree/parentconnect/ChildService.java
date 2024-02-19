package com.safestree.parentconnect;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.safestree.MainActivity;
import com.safestree.R;

public class ChildService extends Service {

    private DatabaseReference mDatabase;
    FirebaseAuth auth;
    FirebaseUser authUser;
    String childGmail="";
    static int trackCount=0;
    String lat = "";
    String lon = "";
    Handler handler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        childGmail=intent.getStringExtra("childGmail");
//        Toast.makeText(this, "Reached", Toast.LENGTH_SHORT).show();
        return null;
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        final String CHANNELID = "foreground service id";
        Intent stopSelf = new Intent(this, MainActivity.class);
        stopSelf.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ChildService.this, 1, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_MUTABLE);

        FirebaseMessaging.getInstance().subscribeToTopic("notification");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        authUser = auth.getCurrentUser();

        childGmail = intent.getStringExtra("childGmail");
//        Toast.makeText(this, "haha"+childGmail, Toast.LENGTH_SHORT).show();
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
        }
        getLastUpdate();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                getLastUpdate();
                handler.postDelayed(this, 15000);
            }
        }, 15000);
        return START_STICKY;
    }

    @SuppressLint("MissingPermission")
    private void getLastUpdate() {
//        Log.d("tagg", "Error getting data");
        mDatabase.child("users").child(childGmail).child("locationDetails").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("tagg", "Error getting data", task.getException());
                }
                else {
//                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    LocationTrack updatedData = task.getResult().getValue(LocationTrack.class);
                    if(updatedData==null)
                    {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("notification");
                        handler.removeCallbacksAndMessages(null);
                        stopForeground(true);
                        Toast.makeText(ChildService.this, "Enter correct token", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(trackCount==updatedData.getFireStatusSize())
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
                            NotificationManager notificationManager = getSystemService(NotificationManager.class);
                            notificationManager.createNotificationChannel(channel);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "channel_id")
                                    .setSmallIcon(R.drawable.ic_child_parent_icon_foreground)
                                    .setContentTitle("Alert")
                                    .setContentText("Your child lost the internet connection.")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManagerCompat notificationManager1 = NotificationManagerCompat.from(getApplicationContext());
                            notificationManager1.notify(1, builder.build());
                        }

                        Toast.makeText(ChildService.this, "Your child connection is lost", Toast.LENGTH_SHORT).show();
                    }
                    trackCount=updatedData.getFireStatusSize();
                    Toast.makeText(ChildService.this, "Here: "+updatedData.getFireStatusSize(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up resources, unsubscribe from Firebase topics, etc.
        FirebaseMessaging.getInstance().unsubscribeFromTopic("notification");

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        stopForeground(true);
    }


}
