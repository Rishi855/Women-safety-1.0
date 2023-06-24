//package com.example.customnavigationbar;
//
//import static android.content.Context.MODE_PRIVATE;
//
//import static androidx.core.content.ContextCompat.startActivity;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Handler;
//import android.telephony.SmsManager;
//import android.widget.Toast;
//
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//
//public class SosFunctions
//{
//    boolean sosCall=false,sosMessage=false,shakeMessage=false;
//
//    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS=0;
//    private static final int MY_PERMISSIONS_REQUEST_AUTO_CALL=1;
//    private static final int MY_PERMISSIONS_REQUEST_LOCATION=2;
//    public String lat = "";
//    public String lon = "";
//    FusedLocationProviderClient mFusedLocationClient;
//    public String dial;
//
//    public  Context context;
//    public SosFunctions(Context context)
//    {
//        this.context = context;
//    }
//    public void ExecuteAllSosFunctions(Context context)
//    {
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
//        SharedPreferences sh = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
//        dial = sh.getString("emergencyContact", "");
//        sosCall = sh.getBoolean("sosCall",false);
//        sosMessage = sh.getBoolean("sosMessage",false);
//        shakeMessage = sh.getBoolean("shakeMessage",false);
//        if(sosCall)
//        {
//            int check_permission_call = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE);
//            if(check_permission_call == PackageManager.PERMISSION_GRANTED){
//                String dialCall = "tel:" + dial;
//                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dialCall)));
//                Toast.makeText(context, "Call sent", Toast.LENGTH_SHORT).show();
//            }
//            else{
//                FragmentManager fragmentManager = getParentFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.flFragment,new SettingFragment());
//                fragmentTransaction.commit();
//            }
//        }
//
//        if(sosMessage)
//        {
//            int check_permission_location = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
//            if(check_permission_location == PackageManager.PERMISSION_GRANTED){
//                getLastLocation();
//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        SmsManager smsManager = SmsManager.getDefault();
//                        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
//                        String tempLat = sh.getString("lat","");
//                        String tempLon = sh.getString("lon","");
//                        if(tempLat.length()!=0)
//                        {
//                            smsManager.sendTextMessage(dial, null, "https://maps.google.com/?q="+tempLat+","+tempLon, null, null);
//                            Toast.makeText(getActivity(), "Message sent : "+lon+" "+lat, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }, 5000);
//
//            }
//            else{FragmentManager fragmentManager = getParentFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.flFragment,new SettingFragment());
//                fragmentTransaction.commit();
//            }
//        }
//    }
//}
