package com.safestree;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.safestree.authentication.login;
import com.safestree.parentconnect.ChildService;
import com.safestree.parentconnect.UpdateCountService;

import java.util.ArrayList;
import java.util.List;


public class SettingFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser authUser;
    private DatabaseReference mDatabase;
    private static final int REQUEST_FOREGROUND_SERVICE_PERMISSION = 123;

    CheckBox sosCall, sosMessage, shakeMessage;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_REQUEST_AUTO_CALL = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 2;
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
    Button childParent;
    public Boolean now = false;
    String[] levels = {"Level-1", "Level-2", "Level-3"};

    String SMSTEXT="";
    String LOCAITONTEXT="";
    String CALLTEXT="";
    public static ActivityResultLauncher<String> requestPermissionLauncher;

// Here 7   @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Initialize the ActivityResultLauncher for SMS permission
//        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
//                new ActivityResultCallback<Boolean>() {
//                    @Override
//                    public void onActivityResult(Boolean isGranted) {
//                        boolean isPermissionsGranted = true;
//                        if (isGranted) {
//                            // SMS permission granted, you can now send SMS
//                            showToast("Permission Granted");
//
//                        } else {
//                            // Permission denied, handle it accordingly (e.g., show a message)
//                            isPermissionsGranted=false;
//                            showToast("Permission Denied");
//                        }
//                        if(!isPermissionsGranted)
//                        {
//                            askFromSettings();
//                        }
//                    }
//                });
//    }
//    public void askFromSettings()
//    {
//        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//                    alertDialogBuilder.setTitle("Permissions Required")
//                            .setMessage("You have forcefully denied some of the required permissions " +
//                                    "for this action. Please open settings, go to permissions, and allow them.")
//                            .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                                            Uri.fromParts("package", getActivity().getPackageName(), null));
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
//                                }
//                            })
//                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                }
//                            })
//                            .setCancelable(false)
//                            .create()
//                            .show();
//    }
    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        authUser = auth.getCurrentUser();
        if (authUser==null)
        {
            Intent intent = new Intent(getActivity(),login.class);
            startActivity(intent);
        }

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

        childParent = view.findViewById(R.id.btnChildParent);
        childParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_bottom_sheet_connect();
//                Intent connectFragment = new Intent(getActivity(), parentConnection.class);
//                startActivity(connectFragment);
//                replaceFragment(new parentConnection());
            }
        });

        //SMS PERMISSION
        ImageButton psms = view.findViewById(R.id.psms);
        psms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check_permission = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.SEND_SMS);

                if (check_permission == PackageManager.PERMISSION_GRANTED) {
                    psms.setImageResource(R.drawable.baseline_done_24);
                    psms.setBackground(getActivity().getDrawable(R.drawable.bg_done));
                } else {
                    requestPermissions();
//                       SmsPermissionDisclosureDialog dialog = new SmsPermissionDisclosureDialog();
//                       dialog.show(getActivity().getSupportFragmentManager(), "SmsPermissionDisclosureDialog");
                }
            }

         });
        //CALL PERMISSION
        ImageButton pcall = view.findViewById(R.id.pcall);
        pcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check_permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

                if (check_permission == PackageManager.PERMISSION_GRANTED) {
                    pcall.setImageResource(R.drawable.baseline_done_24);
                    pcall.setBackground(getActivity().getDrawable(R.drawable.bg_done));
                } else {
                    requestPermissions();
//                          CallPermissionDisclosureDialog dialog = new CallPermissionDisclosureDialog();
//                          dialog.show(getActivity().getSupportFragmentManager(), "CallPermissionDisclosureDialog");
                }
            }

        });
        //LOCATION PERMISSION
        ImageButton plocation = view.findViewById(R.id.plocation);
        plocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check_permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

                if (check_permission == PackageManager.PERMISSION_GRANTED) {
                    plocation.setImageResource(R.drawable.baseline_done_24);
                    plocation.setBackground(getActivity().getDrawable(R.drawable.bg_done));
                } else {
                    requestPermissions();
//                        LocationPermissionDisclosureDialog dialog = new LocationPermissionDisclosureDialog();
//                        dialog.show(getActivity().getSupportFragmentManager(), "LocationPermissionDisclosureDialog");
                }
            }

        });

        int check_permission1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (check_permission1 == PackageManager.PERMISSION_GRANTED) {
            plocation.setImageResource(R.drawable.baseline_done_24);
            plocation.setBackground(getActivity().getDrawable(R.drawable.bg_done));
        }
        int check_permission2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

        if (check_permission2 == PackageManager.PERMISSION_GRANTED) {
            pcall.setImageResource(R.drawable.baseline_done_24);
            pcall.setBackground(getActivity().getDrawable(R.drawable.bg_done));
        }
        int check_permission3 = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.SEND_SMS);

        if (check_permission3 == PackageManager.PERMISSION_GRANTED) {
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
        String temp = sh.getString("level", "Level-1");
        if (temp.equals("Level-1")) spinner.setSelection(0);
        else if (temp.equals("Level-2")) spinner.setSelection(1);
        else if (temp.equals("Level-3")) spinner.setSelection(2);
        else if (temp.equals("Level-4")) spinner.setSelection(3);
        else if (temp.equals("Level-5")) spinner.setSelection(4);
        shakeMessage.setChecked(sh.getBoolean("shakeMessage", false));
        switchActive.setChecked(sh.getBoolean("switchActive", false));
        bottomSheet.show();
        Button done = bottomSheet.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putBoolean("shakeMessage", shakeMessage.isChecked());
                myEdit.putString("level", spinner.getSelectedItem().toString());
                myEdit.putBoolean("switchActive", switchActive.isChecked());
                myEdit.apply();
                if (switchActive.isChecked()) ActiveServiceForAll(MyService.class);
                else DeactivateServiceForAll(MyService.class);
                bottomSheet.dismiss();
                Toast.makeText(getActivity(), "It might take 5 to 10 seconds to change to the setting.", Toast.LENGTH_SHORT).show();
//                done.setBackground(getResources().getDrawable(R.drawable.done_button));
            }
        });

    }

    private void show_bottom_sheet_connect()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEditConnect = sharedPreferences.edit();
        final BottomSheetDialog bottomSheet = new BottomSheetDialog(getActivity());
        bottomSheet.setContentView(R.layout.connect_setting);
        EditText token = bottomSheet.findViewById(R.id.et_token);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchActive = bottomSheet.findViewById(R.id.switchActive);
        Button track = bottomSheet.findViewById(R.id.btnTrack);
        Button done = bottomSheet.findViewById(R.id.btnDone);
        Button share = bottomSheet.findViewById(R.id.btnShare);
        Button stop = bottomSheet.findViewById(R.id.btnStopService);
        assert switchActive != null;
        switchActive.setChecked(sharedPreferences.getBoolean("childService",false));
        token.setText(sharedPreferences.getString("childGmail",""));
        assert done != null;
        done.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if (switchActive.isChecked()) {
                    ActiveServiceForAll(UpdateCountService.class);
                }

                else {
                    DeactivateServiceForAll(UpdateCountService.class);
                }
                myEditConnect.putBoolean("childService",switchActive.isChecked());
//                Toast.makeText(getActivity(), ""+switchActive.isChecked(), Toast.LENGTH_SHORT).show();
                myEditConnect.apply();
                bottomSheet.dismiss();
//                done.setBackground(getResources().getDrawable(R.drawable.done_button));
            }
        });
        assert track != null;
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                if(token.getText().toString().trim().isEmpty()){
                    Toast.makeText(getActivity(), "Enter your child token", Toast.LENGTH_SHORT).show();
                    return;
                }
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.FOREGROUND_SERVICE}, REQUEST_FOREGROUND_SERVICE_PERMISSION);
                            Toast.makeText(getActivity(), "Please allow permission", Toast.LENGTH_SHORT).show();
                            return ;
                        } else {
                            if(!foregroundServiceRunningForAll(ChildService.class.getName()))
                            {
                                Intent serviceIntent = new Intent(getActivity(), ChildService.class);
                                serviceIntent.putExtra("childGmail",token.getText().toString().trim());
                                getActivity().startService(serviceIntent);
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                mDatabase.child("users").child(authUser.getUid()).child("locationDetails").addValueEventListener(postListener);
                myEdit.putString("childGmail",token.getText().toString().trim());
                myEdit.apply();
                Toast.makeText(getActivity(), "It might take 5 to 10 seconds to change to the setting.", Toast.LENGTH_SHORT).show();
                bottomSheet.dismiss();
            }
        });
        assert share != null;
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain"); // You can change the type to share other content types
                String shareText = authUser.getUid()+""; // Replace with your content
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "App Recommendation");
                Intent chooser = Intent.createChooser(shareIntent, "Share via");
                if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(chooser);
                } else {
                    Toast.makeText(getActivity(), "No apps to share", Toast.LENGTH_SHORT).show();
                }
                bottomSheet.dismiss();
            }
        });

        assert stop != null;
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeactivateServiceForAll(ChildService.class);
                DeactivateServiceForAll(UpdateCountService.class);
                switchActive.setChecked(false);
                myEditConnect.putBoolean("childService",false);
//                Toast.makeText(getActivity(), ""+sharedPreferences.getString("childService",""), Toast.LENGTH_SHORT).show();
                bottomSheet.dismiss();
            }
        });
        bottomSheet.show();
    }

    public boolean foregroundServiceRunningForAll(String currentService) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (currentService.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void ActiveServiceForAll(Class<?> serviceClass)
    {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission if it's not granted
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.FOREGROUND_SERVICE}, REQUEST_FOREGROUND_SERVICE_PERMISSION);
        } else {
            // Start your service here
            if (!foregroundServiceRunningForAll(serviceClass.getName())) {
//            Toast.makeText(getActivity(), "1: "+now, Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(getActivity(), serviceClass);
                getActivity().startService(serviceIntent);
            } else {
//            Toast.makeText(getActivity(), "1: "+now, Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(getActivity(), serviceClass);
                getActivity().stopService(new Intent(getActivity(),serviceClass));
                getActivity().startService(serviceIntent);
            }

        }
    }

    public void DeactivateServiceForAll(Class<?> serviceClass) {
        now = false;
        if (foregroundServiceRunningForAll(serviceClass.getName())) {
            Intent serviceIntent = new Intent(getActivity(), serviceClass);
//            serviceIntent.putExtra("status",false);
            getActivity().stopService(serviceIntent);
            Toast.makeText(getActivity(), "destroyed", Toast.LENGTH_SHORT).show();
        }
    }

//    public boolean foregroundServiceRunningFirebase() {
//        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (UpdateCountService.class.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }

//    public void ActiveFirebaseService()
//    {
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
//            // Request the permission if it's not granted
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.FOREGROUND_SERVICE}, REQUEST_FOREGROUND_SERVICE_PERMISSION);
//        } else {
//            // Start your service here
//            if (!foregroundServiceRunningForAll(UpdateCountService.class.getName())) {
////            Toast.makeText(getActivity(), "1: "+now, Toast.LENGTH_SHORT).show();
//                Intent serviceIntent = new Intent(getActivity(), UpdateCountService.class);
//                getActivity().startService(serviceIntent);
//            } else {
////            Toast.makeText(getActivity(), "1: "+now, Toast.LENGTH_SHORT).show();
//                Intent serviceIntent = new Intent(getActivity(), UpdateCountService.class);
//                getActivity().stopService(new Intent(getActivity(), UpdateCountService.class));
//                getActivity().startService(serviceIntent);
//            }
//
//        }
//    }

//    public void DeactivateServiceFirebase() {
//        now = false;
//        if (foregroundServiceRunningForAll(UpdateCountService.class.getName())) {
//            Intent serviceIntent = new Intent(getActivity(), UpdateCountService.class);
////            serviceIntent.putExtra("status",false);
//            getActivity().stopService(serviceIntent);
//            Toast.makeText(getActivity(), "destroyed", Toast.LENGTH_SHORT).show();
//        }
//    }

//    public boolean foregroundServiceRunningFirebaseParent() {
//        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (ChildService.class.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }

//    public void ActiveFirebaseServiceParent()
//    {
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.FOREGROUND_SERVICE}, REQUEST_FOREGROUND_SERVICE_PERMISSION);
//        } else {
//            if (!foregroundServiceRunningForAll(ChildService.class.getName())) {
//                Intent serviceIntent = new Intent(getActivity(), ChildService.class);
//                getActivity().startService(serviceIntent);
//            } else {
//                Intent serviceIntent = new Intent(getActivity(), ChildService.class);
//                getActivity().stopService(new Intent(getActivity(), ChildService.class));
//                getActivity().startService(serviceIntent);
//            }
//
//        }
//    }
//    public void DeactivateServiceFirebaseParent() {
//        now = false;
//        if (foregroundServiceRunningForAll(ChildService.class.getName())) {
//            getActivity().stopService(new Intent(getActivity(), ChildService.class));
//        }
//    }

//    public boolean foregroundServiceRunning() {
//        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (MyService.class.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }

//    public void ActiveService() {
//        now = true;
//        if (!foregroundServiceRunningForAll(MyService.class.getName())) {
////            Toast.makeText(getActivity(), "1: "+now, Toast.LENGTH_SHORT).show();
//            getActivity().startService(new Intent(getActivity(), MyService.class));
//        } else {
////            Toast.makeText(getActivity(), "1: "+now, Toast.LENGTH_SHORT).show();
//            getActivity().stopService(new Intent(getActivity(), MyService.class));
//            getActivity().startService(new Intent(getActivity(), MyService.class));
//        }
//    }

//    public void DeactivateService() {
//        now = false;
//        if (foregroundServiceRunningForAll(MyService.class.getName())) {
//            getActivity().stopService(new Intent(getActivity(), MyService.class));
//        }
//    }

    public void requestPermissions() {
        Dexter.withContext(getActivity())
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.CALL_PHONE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            // All permissions are granted.
                            Toast.makeText(getActivity(), "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            // Check if any permission is permanently denied.
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        // Handle permission rationale here.
                        // This method is called when user grants some permission and denies some.
                        // You can show an explanation to the user here.
                        for (PermissionRequest permission : permissions) {
                            if (token != null) {
                                // Check if "Never Ask Again" is selected.
                                if (shouldShowRequestPermissionRationale(permission.getName())) {
                                    // You can show an explanation dialog or message to the user here.
                                    // For example, display a dialog explaining why the permission is needed.
                                    new AlertDialog.Builder(getActivity())
                                            .setMessage("We need this permission for XYZ functionality.")
                                            .setPositiveButton("OK", (dialog, which) -> token.continuePermissionRequest())
                                            .setNegativeButton("Cancel", (dialog, which) -> token.cancelPermissionRequest())
                                            .show();
                                } else {
                                    // Permission is permanently denied.
                                    // You can show a message or take appropriate action.
                                    showSettingsDialog();
                                }
                            }
                        }
                    }
                })
                .withErrorListener(error -> {
                    // Handle error here.
                    Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                })
                .onSameThread()
                .check();
    }

    // leatest commit   public void requestPermissions() {
//        // below line is use to request permission in the current activity.
//        // this method is use to handle error in runtime permissions
//        Dexter.withContext(getActivity())
//                // below line is use to request the number of permissions which are required in our app.
//                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
//                        // below is the list of permissions
//                        Manifest.permission.SEND_SMS,
//                        Manifest.permission.CALL_PHONE)
//                // after adding permissions we are calling an with listener method.
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                        // this method is called when all permissions are granted
//                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
//                            // do you work now
//                            Toast.makeText(getActivity(), "All the permissions are granted..", Toast.LENGTH_SHORT).show();
//                        }
//                        // check for permanent denial of any permission
//                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
//                            // permission is denied permanently, we will show user a dialog message.
//                            showSettingsDialog();
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                        // this method is called when user grants some permission and denies some of them.
//                        permissionToken.continuePermissionRequest();
//                    }
//                }).withErrorListener(error -> {
//                    // we are displaying a toast message for error message.
//                    Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
//                })
//                // below line is use to run the permissions on same thread and to check the permissions
//                .onSameThread().check();
//    }
    public void showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

        // below line is the title for our alert dialog.
        builder.setTitle("Need Permissions");

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            // this method is called on click on positive button and on clicking shit button
            // we are redirecting our user from our app to the settings page of our app.
            dialog.cancel();
            // below is the intent from which we are redirecting our user.
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // this method is called when user click on negative button.
            dialog.cancel();
        });
        // below line is used to display our dialog
        builder.show();
    }


    // below is the shoe setting dialog method which is use to display a dialogue message.



// here start 1   public final ActivityResultLauncher<String> requestAutoCallPermission =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//                if (isGranted) {
//                    // Permission granted, handle it here
//                    Toast.makeText(getActivity(), "Call Granted", Toast.LENGTH_SHORT).show();
//                } else {
//                    // Permission denied, handle it here or show a message to the user
//                }
//            });
//
//    public final ActivityResultLauncher<String> requestLocationPermission =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//                if (isGranted) {
//                    // Permission granted, handle it here
//                    Toast.makeText(getActivity(), "Location Granted", Toast.LENGTH_SHORT).show();
//                } else {
//                    // Permission denied, handle it here or show a message to the user
//                }
//            });
//
//
// Here another 1

// Here main all first of all good    public static class SmsPermissionDisclosureDialog extends DialogFragment {
//
//        public void showAppSettings() {
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//            alertDialogBuilder.setTitle("Permissions Required")
//                    .setMessage("You have forcefully denied some of the required permissions " +
//                            "for this action. Please open settings, go to permissions, and allow them.")
//                    .setPositiveButton("Settings", (dialog, which) -> {
//                        // Open app settings
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                                Uri.fromParts("package", getActivity().getPackageName(), null));
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    })
//                    .setNegativeButton("Cancel", (dialog, which) -> {
//                        // Handle cancellation if needed
//                    })
//                    .setCancelable(false)
//                    .create()
//                    .show();
//        }
//
//        @Override
//        public AlertDialog onCreateDialog(Bundle savedInstanceState) {
//            return new AlertDialog.Builder(getActivity())
//                    .setTitle("SMS Permission Disclaimer")
//                    .setMessage("This app requires access to your SMS to enable sending location information to your default number in an emergency situation. even when your app is closed and not in use.")
//                    .setPositiveButton("Accept", (dialog, which) -> {
//                        // User acknowledged the disclosure, request SMS permission here
//                        // Example code for requesting SEND_SMS permission
//                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//                            // Permission not granted, request it using the ActivityResultLauncher
////  Temp here 1                         requestSmsPermission();
//                            requestPermissions();
////                            Toast.makeText(getActivity(), "here 2", Toast.LENGTH_SHORT).show();
////                            requestSendSmsPermission.launch(Manifest.permission.SEND_SMS);
//                        }
//
//                    })
//                    .setNegativeButton("Deny", (dialog, which) -> {
//                        // User canceled, handle accordingly
//                    })
//                    .create();
//
//        }
//    public void requestPermissions() {
//        // below line is use to request permission in the current activity.
//        // this method is use to handle error in runtime permissions
//        Dexter.withContext(getActivity())
//                // below line is use to request the number of permissions which are required in our app.
//                .withPermissions(Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION)
//                // after adding permissions we are calling an with listener method.
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                        // this method is called when all permissions are granted
//                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
//                            // do you work now
//                            Toast.makeText(getActivity(), "All the permissions are granted..", Toast.LENGTH_SHORT).show();
//                        }
//                        // check for permanent denial of any permission
//                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
//                            // permission is denied permanently, we will show user a dialog message.
//                            showSettingsDialog();
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                        // this method is called when user grants some permission and denies some of them.
//                        permissionToken.continuePermissionRequest();
//                    }
//                }).withErrorListener(error -> {
//                    // we are displaying a toast message for error message.
//                    Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
//                })
//                // below line is use to run the permissions on same thread and to check the permissions
//                .onSameThread().check();
//    }
//    public void showSettingsDialog() {
//        // we are displaying an alert dialog for permissions
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        // below line is the title for our alert dialog.
//        builder.setTitle("Need Permissions");
//
//        // below line is our message for our dialog
//        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
//        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
//            // this method is called on click on positive button and on clicking shit button
//            // we are redirecting our user from our app to the settings page of our app.
//            dialog.cancel();
//            // below is the intent from which we are redirecting our user.
//            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
//            intent.setData(uri);
//            startActivityForResult(intent, 101);
//        });
//        builder.setNegativeButton("Cancel", (dialog, which) -> {
//            // this method is called when user click on negative button.
//            dialog.cancel();
//        });
//        // below line is used to display our dialog
//        builder.show();
//    }
//}
//
//        public static class CallPermissionDisclosureDialog extends DialogFragment {
//            @Override
//            public AlertDialog onCreateDialog(Bundle savedInstanceState) {
//                return new AlertDialog.Builder(getActivity())
//                        .setTitle("Call/Contact Permission Disclaimer")
//                        .setMessage("This app collects contact data to enable default number for an emergency situation. even when the app is closed or not in use, using the SOS or shake feature.")
//                        .setPositiveButton("Accept", (dialog, which) -> {
//                            // User acknowledged the disclosure, request SMS permission here
//                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                                // Permission not granted, request it.
////                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_AUTO_CALL);
////    temp here 2                            requestCallPermission();
//                                requestPermissions();
////                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_AUTO_CALL);
//                            }
//                        })
//                        .setNegativeButton("Deny", (dialog, which) -> {
//                            // User canceled, handle accordingly
//                        })
//                        .create();
//            }
//            public void requestPermissions() {
//                // below line is use to request permission in the current activity.
//                // this method is use to handle error in runtime permissions
//                Dexter.withContext(getActivity())
//                        // below line is use to request the number of permissions which are required in our app.
//                        .withPermissions(
//                                Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION
//                                )
//                        // after adding permissions we are calling an with listener method.
//                        .withListener(new MultiplePermissionsListener() {
//                            @Override
//                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                                // this method is called when all permissions are granted
//                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
//                                    // do you work now
//                                    Toast.makeText(getActivity(), "All the permissions are granted..", Toast.LENGTH_SHORT).show();
//                                }
//                                // check for permanent denial of any permission
//                                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
//                                    // permission is denied permanently, we will show user a dialog message.
//                                    showSettingsDialog();
//                                }
//                            }
//
//                            @Override
//                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                                // this method is called when user grants some permission and denies some of them.
//                                permissionToken.continuePermissionRequest();
//                            }
//                        }).withErrorListener(error -> {
//                            // we are displaying a toast message for error message.
//                            Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
//                        })
//                        // below line is use to run the permissions on same thread and to check the permissions
//                        .onSameThread().check();
//            }
//            public void showSettingsDialog() {
//                // we are displaying an alert dialog for permissions
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//                // below line is the title for our alert dialog.
//                builder.setTitle("Need Permissions");
//
//                // below line is our message for our dialog
//                builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
//                builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
//                    // this method is called on click on positive button and on clicking shit button
//                    // we are redirecting our user from our app to the settings page of our app.
//                    dialog.cancel();
//                    // below is the intent from which we are redirecting our user.
//                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
//                    intent.setData(uri);
//                    startActivityForResult(intent, 101);
//                });
//                builder.setNegativeButton("Cancel", (dialog, which) -> {
//                    // this method is called when user click on negative button.
//                    dialog.cancel();
//                });
//                // below line is used to display our dialog
//                builder.show();
//            }
//        }
//
//        public static class LocationPermissionDisclosureDialog extends DialogFragment {
//            @Override
//            public AlertDialog onCreateDialog(Bundle savedInstanceState) {
//                return new AlertDialog.Builder(getActivity())
//                        .setTitle("Location Permission Disclaimer")
//                        .setMessage("This app collects location data to enable emergency location sharing, even when the app is closed or not in use, using the SOS or shake feature.")
//                        .setPositiveButton("Accept", (dialog, which) -> {
//                            // User acknowledged the disclosure, request SMS permission here
//                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                // Permission not granted, request it.
////   temp here 3                              requestLocationPermission();
//                                requestPermissions();
////                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
////                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
//                            }
//                        })
//                        .setNegativeButton("Deny", (dialog, which) -> {
//                            // User canceled, handle accordingly
//                        })
//                        .create();
//            }
//            public void requestPermissions() {
//                // below line is use to request permission in the current activity.
//                // this method is use to handle error in runtime permissions
//                Dexter.withContext(getActivity())
//                        // below line is use to request the number of permissions which are required in our app.
//                        .withPermissions(Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION)
//                        // after adding permissions we are calling an with listener method.
//                        .withListener(new MultiplePermissionsListener() {
//                            @Override
//                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                                // this method is called when all permissions are granted
//                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
//                                    // do you work now
//                                    Toast.makeText(getActivity(), "All the permissions are granted..", Toast.LENGTH_SHORT).show();
//                                }
//                                // check for permanent denial of any permission
//                                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
//                                    // permission is denied permanently, we will show user a dialog message.
//                                    showSettingsDialog();
//                                }
//                            }
//
//                            @Override
//                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                                // this method is called when user grants some permission and denies some of them.
//                                permissionToken.continuePermissionRequest();
//                            }
//                        }).withErrorListener(error -> {
//                            // we are displaying a toast message for error message.
//                            Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
//                        })
//                        // below line is use to run the permissions on same thread and to check the permissions
//                        .onSameThread().check();
//            }
//            public void showSettingsDialog() {
//                // we are displaying an alert dialog for permissions
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
//
//                // below line is the title for our alert dialog.
//                builder.setTitle("Need Permissions");
//
//                // below line is our message for our dialog
//                builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
//                builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
//                    // this method is called on click on positive button and on clicking shit button
//                    // we are redirecting our user from our app to the settings page of our app.
//                    dialog.cancel();
//                    // below is the intent from which we are redirecting our user.
//                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
//                    intent.setData(uri);
//                    startActivityForResult(intent, 101);
//                });
//                builder.setNegativeButton("Cancel", (dialog, which) -> {
//                    // this method is called when user click on negative button.
//                    dialog.cancel();
//                });
//                // below line is used to display our dialog
//                builder.show();
//            }
//        }
//
// Here 1    public static void requestSmsPermission() {
//        String smsPermission = Manifest.permission.SEND_SMS;
//        // Request the SMS permission using the ActivityResultLauncher
//        requestPermissionLauncher.launch(smsPermission);
//    }
//    public static void requestCallPermission() {
//        String smsPermission = Manifest.permission.CALL_PHONE;
//
//        // Request the SMS permission using the ActivityResultLauncher
//        requestPermissionLauncher.launch(smsPermission);
//    }
//    public static void requestLocationPermission() {
//        String smsPermission = Manifest.permission.ACCESS_FINE_LOCATION;
//        // Request the SMS permission using the ActivityResultLauncher
//        requestPermissionLauncher.launch(smsPermission);
// Here End 1   }
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
//        private void showPermissionRationaleDialog(String permission) {
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
//            alertDialogBuilder.setTitle("Permission Required")
//                    .setMessage("This app needs the " + permission + " permission for some functionality. Please grant the permission in the app settings.")
//                    .setPositiveButton("Go to Settings", (dialog, which) -> {
//                        // Open app settings
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        intent.setData(Uri.fromParts("package", requireActivity().getPackageName(), null));
//                        startActivity(intent);
//                    })
//                    .setNegativeButton("Cancel", (dialog, which) -> {
//                        // Handle cancellation
//                    })
//                    .setCancelable(false)
//                    .create()
//                    .show();
//        }

//        @Override
//        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//            if (permissions.length == 0) {
//                return;
//            }
//
//            boolean allPermissionsGranted = true;
//
//            if (grantResults.length > 0) {
//                for (int grantResult : grantResults) {
//                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
//                        allPermissionsGranted = false;
//                        break;
//                    }
//                }
//            }
//
//            if (!allPermissionsGranted) {
//                boolean somePermissionsForeverDenied = false;
//
//                for (String permission : permissions) {
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
//                        // Denied
//                        Log.e("denied", permission);
//                    } else {
//                        if (ActivityCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
//                            // Allowed
//                            Log.e("allowed", permission);
//                        } else {
//                            // Set to never ask again
//                            Log.e("set to never ask again", permission);
//                            somePermissionsForeverDenied = true;
//                        }
//                    }
//                }
//
//                if (somePermissionsForeverDenied) {
//                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//                    alertDialogBuilder.setTitle("Permissions Required")
//                            .setMessage("You have forcefully denied some of the required permissions " +
//                                    "for this action. Please open settings, go to permissions, and allow them.")
//                            .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                                            Uri.fromParts("package", getActivity().getPackageName(), null));
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
//                                }
//                            })
//                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                }
//                            })
//                            .setCancelable(false)
//                            .create()
//                            .show();
//                }
//            } else {
//                switch (requestCode) {
//                    case MY_PERMISSIONS_REQUEST_SEND_SMS:
//                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                            Toast.makeText(getActivity(), "SMS Granted", Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                    case MY_PERMISSIONS_REQUEST_AUTO_CALL:
//                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                            Toast.makeText(getActivity(), "Call Granted", Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                    case MY_PERMISSIONS_REQUEST_LOCATION:
//                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                            Toast.makeText(getActivity(), "Location Granted", Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                }
//            }
//        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
        }

}
