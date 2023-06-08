package com.example.customnavigationbar;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;


public class SettingFragment extends Fragment {

    CheckBox sosCall,sosMessage,shakeMessage;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS=0;
    private static final int MY_PERMISSIONS_REQUEST_AUTO_CALL=1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION=2;
    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        sosCall = view.findViewById(R.id.sosCall);
        sosMessage = view.findViewById(R.id.sosMessage);
        shakeMessage = view.findViewById(R.id.shakeMessage);



        //SMS PERMISSION
        Button psms = view.findViewById(R.id.psms);
        psms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check_permission = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.SEND_SMS);

                if(check_permission == PackageManager.PERMISSION_GRANTED){

                }
                else{
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.SEND_SMS},MY_PERMISSIONS_REQUEST_SEND_SMS );
                }
            }

        });
        //CALL PERMISSION
        Button pcall = view.findViewById(R.id.pcall);
        pcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check_permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

                if(check_permission == PackageManager.PERMISSION_GRANTED){

                }
                else{
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CALL_PHONE},MY_PERMISSIONS_REQUEST_AUTO_CALL );
                }
            }

        });
        //LOCATION PERMISSION
        Button plocation = view.findViewById(R.id.plocation);
        plocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check_permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

                if(check_permission == PackageManager.PERMISSION_GRANTED){

                }
                else{
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION );
                }
            }

        });

        return view;
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
            case MY_PERMISSIONS_REQUEST_AUTO_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // Fetching the stored data from the SharedPreference
        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
//        String s1 = sh.getString("name", "");
//        int a = sh.getInt("age", 0);
        sosMessage.setChecked(sh.getBoolean("sosMessage", false));
        sosCall.setChecked(sh.getBoolean("sosCall",false));
        shakeMessage.setChecked(sh.getBoolean("shakeMessage",false));

        // Setting the fetched data in the EditTexts
//        name.setText(s1);
//        age.setText(String.valueOf(a));
    }
    @Override
    public void onPause() {
        super.onPause();
        // Creating a shared pref object with a file name "MySharedPref" in private mode
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putBoolean("sosMessage", sosMessage.isChecked());
        myEdit.putBoolean("sosCall", sosCall.isChecked());
        myEdit.putBoolean("shakeMessage", shakeMessage.isChecked());
        // write all the data entered by the user in SharedPreference and apply
//        myEdit.putString("name", name.getText().toString());
//        myEdit.putInt("age", Integer.parseInt(age.getText().toString()));
        myEdit.apply();
    }
}