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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser authUser;
    private DatabaseReference mDatabase;
    private static final int REQUEST_FOREGROUND_SERVICE_PERMISSION = 123;

    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    CheckBox sosCall, sosMessage, shakeMessage;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_REQUEST_AUTO_CALL = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 2;
    public String lat = "";
    public String lon = "";

    FusedLocationProviderClient mFusedLocationClient;

    private SensorManager mSensorManager;
    private int request_Code = 101;
    private float mAccel;
    private Button b;
    private float mAccelLast;
    Spinner spinner;
    public EditText editText;
    int level = 0;
    Button childParent, geoButton;
    public Boolean now = false;
    String[] levels = {"Level-1", "Level-2", "Level-3"};

    String SMSTEXT = "";
    String LOCAITONTEXT = "";
    String CALLTEXT = "";
    public static ActivityResultLauncher<String> requestPermissionLauncher;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        authUser = auth.getCurrentUser();
        if (authUser == null) {
            Intent intent = new Intent(getActivity(), login.class);
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
            }
        });

        geoButton = view.findViewById(R.id.btnGeoFencing);
        geoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_bottom_sheet_geo();
            }
        });

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
                }
            }

        });

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
                }
            }

        });

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
                }
            }

        });

        int check_permission1 = ContextCompat.checkSelfPermission(getActivity(),

                Manifest.permission.ACCESS_FINE_LOCATION);
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
            }
        });

    }

    private void show_bottom_sheet_connect() {
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
        switchActive.setChecked(sharedPreferences.getBoolean("childService", false));
        token.setText(sharedPreferences.getString("childGmail", ""));
        assert done != null;
        done.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if (switchActive.isChecked()) {
                    ActiveServiceForAll(UpdateCountService.class);
                } else {
                    DeactivateServiceForAll(UpdateCountService.class);
                }
                myEditConnect.putBoolean("childService", switchActive.isChecked());
                myEditConnect.apply();
                bottomSheet.dismiss();
            }
        });
        assert track != null;
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                if (token.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "Enter your child token", Toast.LENGTH_SHORT).show();
                    return;
                }
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.FOREGROUND_SERVICE}, REQUEST_FOREGROUND_SERVICE_PERMISSION);
                            Toast.makeText(getActivity(), "Please allow permission", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            if (!foregroundServiceRunningForAll(ChildService.class.getName())) {
                                Intent serviceIntent = new Intent(getActivity(), ChildService.class);
                                serviceIntent.putExtra("childGmail", token.getText().toString().trim());
                                getActivity().startService(serviceIntent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                mDatabase.child("users").child(authUser.getUid()).child("locationDetails").addValueEventListener(postListener);
                myEdit.putString("childGmail", token.getText().toString().trim());
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
                shareIntent.setType("text/plain");
                String shareText = authUser.getUid() + "";
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
                myEditConnect.putBoolean("childService", false);
                bottomSheet.dismiss();
            }
        });
        bottomSheet.show();
    }

    private void show_bottom_sheet_geo() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        int check_permission2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (check_permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            Toast.makeText(getActivity(), "Please turn on location", Toast.LENGTH_SHORT).show();
            return;
        }
//        double latitude,longitude;

        final BottomSheetDialog bottomSheet = new BottomSheetDialog(getActivity());
        bottomSheet.setContentView(R.layout.geo_fencing_setting);
        ImageButton btnReset = bottomSheet.findViewById(R.id.btnReset);
        TextView textAddress = bottomSheet.findViewById(R.id.textAddress);
        TextView textCoordinate = bottomSheet.findViewById(R.id.textCoordinate);
        EditText editGeo = bottomSheet.findViewById(R.id.editGeo);

        String tempAdd = sharedPreferences.getString("safeAdd","a");
        float tempLat = sharedPreferences.getFloat("safeLatitude",0);
        float tempLong = sharedPreferences.getFloat("safeLongitude",0);
        float tempGeo = sharedPreferences.getFloat("safeRadius",0);

//        Toast.makeText(getActivity(), ""+tempAdd, Toast.LENGTH_SHORT).show();

        if(!tempAdd.equals("a")) {
            assert textAddress != null;
            textAddress.setText(tempAdd);
        }
        if(tempLat!=0) {
            assert textCoordinate != null;
            textCoordinate.setText(tempLat+","+tempLong);
        }
        if(editGeo!=null && tempGeo!=0) {
            editGeo.setText(tempGeo+"");
        }

        bottomSheet.show();
        assert btnReset != null;
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    float latitude = (float) location.getLatitude();
                                    float longitude = (float) location.getLongitude();

//                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
//                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                    myEdit.putFloat("safeLatitude", latitude);
                                    myEdit.putFloat("safeLongitude", longitude);
                                    myEdit.apply();

                                    Geocoder geocoder;
                                    List<Address> addresses;
                                    geocoder = new Geocoder(getActivity(), Locale.getDefault());

                                    try {
                                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

                                    String address = addresses.get(0).getAddressLine(0);
                                    myEdit.putString("safeAdd", address);
                                    myEdit.apply();

                                    // Now retrieve the updated values
                                    String tempAdd = sharedPreferences.getString("safeAdd", "a");
                                    float tempLat = sharedPreferences.getFloat("safeLatitude", 0);
                                    float tempLong = sharedPreferences.getFloat("safeLongitude", 0);

                                    if (!tempAdd.equals("a") && textAddress != null) {
                                        textAddress.setText(tempAdd);
                                    }
                                    if (tempLat != 0 && textCoordinate != null) {
                                        textCoordinate.setText(tempLat + "," + tempLong);
                                    }
                                }
                            }
                        });
            }
        });

        Button done = bottomSheet.findViewById(R.id.geoDone);
        done.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                EditText geoRadius = bottomSheet.findViewById(R.id.editGeo);
                assert geoRadius != null;
                String radius = geoRadius.getText().toString().trim();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                if(!radius.equals(""))
                    myEdit.putFloat("safeRadius", Float.parseFloat(radius));
                myEdit.apply();
                bottomSheet.dismiss();
                Toast.makeText(getActivity(), "It might take 5 to 10 seconds to change to the setting.", Toast.LENGTH_SHORT).show();
            }
        });
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

    public void ActiveServiceForAll(Class<?> serviceClass) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.FOREGROUND_SERVICE}, REQUEST_FOREGROUND_SERVICE_PERMISSION);
        } else {
            if (!foregroundServiceRunningForAll(serviceClass.getName())) {
                Intent serviceIntent = new Intent(getActivity(), serviceClass);
                getActivity().startService(serviceIntent);
            } else {
                Intent serviceIntent = new Intent(getActivity(), serviceClass);
                getActivity().stopService(new Intent(getActivity(), serviceClass));
                getActivity().startService(serviceIntent);
            }
        }
    }

    public void DeactivateServiceForAll(Class<?> serviceClass) {
        now = false;
        if (foregroundServiceRunningForAll(serviceClass.getName())) {
            Intent serviceIntent = new Intent(getActivity(), serviceClass);
            getActivity().stopService(serviceIntent);
            Toast.makeText(getActivity(), "destroyed", Toast.LENGTH_SHORT).show();
        }
    }

    public void requestPermissions() {
        Dexter.withContext(getActivity())
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.CALL_PHONE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            Toast.makeText(getActivity(), "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        for (PermissionRequest permission : permissions) {
                            if (token != null) {
                                if (shouldShowRequestPermissionRationale(permission.getName())) {
                                    new AlertDialog.Builder(getActivity())
                                            .setMessage("We need this permission for XYZ functionality.")
                                            .setPositiveButton("OK", (dialog, which) -> token.continuePermissionRequest())
                                            .setNegativeButton("Cancel", (dialog, which) -> token.cancelPermissionRequest())
                                            .show();
                                } else {
                                    showSettingsDialog();
                                }
                            }
                        }
                    }
                })
                .withErrorListener(error -> {
                    Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                })
                .onSameThread()
                .check();
    }

    public void showSettingsDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}