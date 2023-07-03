package com.example.customnavigationbar;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;


public class SettingFragment extends Fragment {

    CheckBox sosCall,sosMessage,shakeMessage;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS=0;
    private static final int MY_PERMISSIONS_REQUEST_AUTO_CALL=1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION=2;
    public String lat = "";
    public String lon = "";
//    public String[] PERMISSIONS = new String[]{
//            Manifest.permission.SEND_SMS,
//            Manifest.permission.CALL_PHONE,
//            Manifest.permission.ACCESS_FINE_LOCATION};
    FusedLocationProviderClient mFusedLocationClient;

//    SharedPreferences sharedPreferences ;
    private SensorManager mSensorManager;
    private int request_Code = 101;
    private float mAccel;
    private Button b;
    private float mAccelLast;
    Spinner spinner;
    public EditText editText;
    int level = 0;
    public Boolean now = false;
    String[] levels = { "Level-1","Level-2","Level-3"};

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        Button btnSosSetting = view.findViewById(R.id.btnSosSetting);

        btnSosSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_bottom_sheet_sos();
            }
        });

        Button btnShakeSetting = view.findViewById(R.id.btnShakeSetting);
        btnShakeSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    show_bottom_sheet_shake();
            }
        });

        //SMS PERMISSION
        ImageButton psms = view.findViewById(R.id.psms);
        psms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check_permission = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.SEND_SMS);

                if(check_permission == PackageManager.PERMISSION_GRANTED){
                    psms.setImageResource(R.drawable.baseline_done_24);
                    psms.setBackground(getActivity().getDrawable(R.drawable.bg_done));
                }
                else{
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.SEND_SMS},MY_PERMISSIONS_REQUEST_SEND_SMS );
                }
            }

        });
        //CALL PERMISSION
        ImageButton pcall = view.findViewById(R.id.pcall);
        pcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check_permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

                if(check_permission == PackageManager.PERMISSION_GRANTED){
                    pcall.setImageResource(R.drawable.baseline_done_24);
                    pcall.setBackground(getActivity().getDrawable(R.drawable.bg_done));
                }
                else{
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CALL_PHONE},MY_PERMISSIONS_REQUEST_AUTO_CALL );
                }
            }

        });
        //LOCATION PERMISSION
        ImageButton plocation = view.findViewById(R.id.plocation);
        plocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check_permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

                if(check_permission == PackageManager.PERMISSION_GRANTED){
                    plocation.setImageResource(R.drawable.baseline_done_24);
                    plocation.setBackground(getActivity().getDrawable(R.drawable.bg_done));
                }
                else{
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION );
                }
            }

        });

        int check_permission1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if(check_permission1 == PackageManager.PERMISSION_GRANTED){
            plocation.setImageResource(R.drawable.baseline_done_24);
            plocation.setBackground(getActivity().getDrawable(R.drawable.bg_done));
        }
        int check_permission2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

        if(check_permission2 == PackageManager.PERMISSION_GRANTED){
            pcall.setImageResource(R.drawable.baseline_done_24);
            pcall.setBackground(getActivity().getDrawable(R.drawable.bg_done));
        }
        int check_permission3 = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.SEND_SMS);

        if(check_permission3 == PackageManager.PERMISSION_GRANTED){
            psms.setImageResource(R.drawable.baseline_done_24);
            psms.setBackground(getActivity().getDrawable(R.drawable.bg_done));
        }
        return view;
    }

    private void show_bottom_sheet_sos() {
        final BottomSheetDialog bottomSheet = new BottomSheetDialog(getActivity());
        bottomSheet.setContentView(R.layout.sos_settings);
        CheckBox sosCall = bottomSheet.findViewById(R.id.pcall);
        CheckBox sosMessage = bottomSheet.findViewById(R.id.psms);
        bottomSheet.show();
        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        sosMessage.setChecked(sh.getBoolean("sosMessage", false));
        sosCall.setChecked(sh.getBoolean("sosCall", false));
        Button done = bottomSheet.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putBoolean("sosMessage", sosMessage.isChecked());
                myEdit.putBoolean("sosCall", sosCall.isChecked());
                myEdit.apply();
                bottomSheet.dismiss();
            }
        });
    }
    private void show_bottom_sheet_shake() {
        final BottomSheetDialog bottomSheet = new BottomSheetDialog(getActivity());
        bottomSheet.setContentView(R.layout.shake_setting);
        CheckBox shakeMessage = bottomSheet.findViewById(R.id.psms);
        List<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Level-1");
        spinnerArray.add("Level-2");
        spinnerArray.add("Level-3");
        spinnerArray.add("Level-4");
        spinnerArray.add("Level-5");

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchActive = bottomSheet.findViewById(R.id.switchActive);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) bottomSheet.findViewById(R.id.levelSpinner);
        spinner.setAdapter(adapter);
        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String temp = sh.getString("level","Level-1");
        if(temp.equals("Level-1")) spinner.setSelection(0);
        else if(temp.equals("Level-2")) spinner.setSelection(1);
        else if(temp.equals("Level-3")) spinner.setSelection(2);
        else if(temp.equals("Level-4")) spinner.setSelection(3);
        else if(temp.equals("Level-5")) spinner.setSelection(4);
        shakeMessage.setChecked(sh.getBoolean("shakeMessage",false));
        switchActive.setChecked(sh.getBoolean("switchActive",false));
        bottomSheet.show();
        Button done = bottomSheet.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putBoolean("shakeMessage",shakeMessage.isChecked());
                myEdit.putString("level",spinner.getSelectedItem().toString());
                myEdit.putBoolean("switchActive",switchActive.isChecked());
                myEdit.apply();
                if(switchActive.isChecked()) ActiveService();
                else DeactivateService();
                bottomSheet.dismiss();
//                done.setBackground(getResources().getDrawable(R.drawable.done_button));
            }
        });

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
    public boolean foregroundServiceRunning()
    {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (MyService.class.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }
    public void ActiveService()
    {
        now = true;
        if(!foregroundServiceRunning())
        {
//            Toast.makeText(getActivity(), "1: "+now, Toast.LENGTH_SHORT).show();
            getActivity().startService(new Intent(getActivity(),MyService.class));
        }
        else {
//            Toast.makeText(getActivity(), "1: "+now, Toast.LENGTH_SHORT).show();
            getActivity().stopService(new Intent(getActivity(), MyService.class));
            getActivity().startService(new Intent(getActivity(),MyService.class));
        }
    }
    public void DeactivateService()
    {
        now = false;
        if(foregroundServiceRunning())
        {
            getActivity().stopService(new Intent(getActivity(), MyService.class));
        }
    }



    @Override
    public void onResume() {
        super.onResume();
//        for(String permission : PERMISSIONS)
//        {
//            if(ActivityCompat.checkSelfPermission(getActivity(),permission)==PackageManager.PERMISSION_GRANTED)
//            {
//                return;
//            }
//        }
    }
    @Override
    public void onPause() {
        super.onPause();
//        if(now)
//        {
//            if(!foregroundServiceRunning())
//            {
//                Toast.makeText(getActivity(), "1: "+now, Toast.LENGTH_SHORT).show();
//                getActivity().startService(new Intent(getActivity(),MyService.class));
//            }
//            else {
//                Toast.makeText(getActivity(), "1: "+now, Toast.LENGTH_SHORT).show();
//                getActivity().stopService(new Intent(getActivity(), MyService.class));
//                getActivity().startService(new Intent(getActivity(),MyService.class));
//            }
//        }

    }
}


//        List<String> spinnerArray = new ArrayList<String>();
//        spinnerArray.add("Level-1");
//        spinnerArray.add("Level-2");
//        spinnerArray.add("Level-3");
//        spinnerArray.add("Level-4");
//        spinnerArray.add("Level-5");

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        Spinner spinner = (Spinner) view.findViewById(R.id.levelSpinner);
//        spinner.setAdapter(adapter);
//
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view,
//                                       int position, long id) {
//                Object item = adapterView.getItemAtPosition(position);
//                if (item != null) {
//                    Toast.makeText(getActivity(), item.toString(),
//                            Toast.LENGTH_SHORT).show();
//                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
//                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
//                    // write all the data entered by the user in SharedPreference and apply
//                    myEdit.putString("level", item.toString());
//                    myEdit.apply();
//                    if(now)
//                    {
//                        if(!foregroundServiceRunning())
//                        {
//                            getActivity().startService(new Intent(getActivity(),MyService.class));
//                        }
//                        else {
//                            getActivity().stopService(new Intent(getActivity(), MyService.class));
//                            getActivity().startService(new Intent(getActivity(),MyService.class));
//
//                        }
//                    }
//
////                    Toast.makeText(MainActivity.this, item.toString()+"selected", Toast.LENGTH_SHORT).show();
//                }
//                Toast.makeText(getActivity(), "Selected",
//                        Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                // TODO Auto-generated method stub
//
//            }
//        });
//        shakeMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked)
//                {
//                    now = true;
//                    if(!foregroundServiceRunning())
//                    {
//                        getActivity().startService(new Intent(getActivity(),MyService.class));
//                    }
//                    else {
//                        getActivity().stopService(new Intent(getActivity(), MyService.class));
//                        getActivity().startService(new Intent(getActivity(),MyService.class));
//
//                    }
//                    Toast.makeText(getActivity(), "1: "+now, Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    now=false;
//                    if(foregroundServiceRunning())
//                        getActivity().stopService(new Intent(getActivity(), MyService.class));
//                }
//            }
//        });