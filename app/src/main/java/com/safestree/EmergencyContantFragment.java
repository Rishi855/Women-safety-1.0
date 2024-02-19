package com.safestree;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class EmergencyContantFragment extends Fragment {
    FloatingActionButton addNewContact,addNewDelete;
    ExtendedFloatingActionButton action;
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    private static final int PICK_CONTACT_REQUEST = 1;
//    private TextView nameTextView;
//    private TextView phoneNumberTextView;
    View view;
    ArrayList<CustomListView> arrayList;
    TextView addContactText,deleteContactText,actionText;
    Boolean allVisible;
    public EmergencyContantFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_emergency_contant, container, false);

//        Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();


        addNewContact =view.findViewById(R.id.add_new_contact);
        addNewDelete = view.findViewById(R.id.add_new_delete);
        action=view.findViewById(R.id.add_fab);
        addContactText=view.findViewById(R.id.addContactText);
        deleteContactText=view.findViewById(R.id.deleteContactText);
        actionText=view.findViewById(R.id.actionText);

        addContactText.setVisibility(View.GONE);
        deleteContactText.setVisibility(View.GONE);
        actionText.setVisibility(View.GONE);

        addNewContact.setVisibility(View.GONE);
        addNewDelete.setVisibility(View.GONE);
        allVisible=false;
        action.shrink();
//        Toast.makeText(getActivity(),""+action.isExtended(), Toast.LENGTH_SHORT).show();
        arrayList = new ArrayList<CustomListView>();

        Map<String,String> hashMap = loadMap();
        for (Map.Entry<String,String> entry : hashMap.entrySet())
        {
            arrayList.add(new CustomListView(R.drawable.ic_person_foreground,entry.getKey(),entry.getValue()));
        }

        ContactAdaptor numbersArrayAdapter = new ContactAdaptor(getActivity(), arrayList);
        ListView numbersListView = (ListView) view.findViewById(R.id.contactListView);
        numbersListView.setAdapter(numbersArrayAdapter);


        Dialog dialog = new Dialog(getActivity());

//        addNewContact.setVisibility(View.INVISIBLE);
//        addNewDelete.setVisibility(View.INVISIBLE);
//
//        ValueAnimator pulseAnimator = ValueAnimator.ofFloat(1f, 1.2f, 1f);
//        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        pulseAnimator.setRepeatMode(ValueAnimator.RESTART);
//        pulseAnimator.setDuration(1000);
//
//        pulseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float animatedValue = (float) animation.getAnimatedValue();
//                action.setScaleX(animatedValue);
//                action.setScaleY(animatedValue);
//            }
//        });
//
//        action.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!allVisible) {
//                    // Fade in animation for addNewContact button
//                    addNewContact.setVisibility(View.VISIBLE);
//                    addNewContact.setAlpha(0f);
//                    addNewContact.animate()
//                            .translationY(0)
//                            .alpha(1f)
//                            .setDuration(500)
//                            .setInterpolator(new OvershootInterpolator())
//                            .start();
//
//                    // Fade in animation for addNewDelete button
//                    addNewDelete.setVisibility(View.VISIBLE);
//                    addNewDelete.setAlpha(0f);
//                    addNewDelete.animate()
//                            .translationY(0)
//                            .alpha(1f)
//                            .setDuration(500)
//                            .setInterpolator(new OvershootInterpolator())
//                            .start();
//
//                    // Start pulsating animation for the action button
//                    pulseAnimator.start();
//
//                    allVisible = true;
//                } else {
//                    // Fade out animation for addNewContact button
//                    addNewContact.animate()
//                            .translationY(100)
//                            .alpha(0f)
//                            .setDuration(500)
//                            .withEndAction(new Runnable() {
//                                @Override
//                                public void run() {
//                                    addNewContact.setVisibility(View.GONE);
//                                }
//                            })
//                            .setInterpolator(new AnticipateInterpolator())
//                            .start();
//
//                    // Fade out animation for addNewDelete button
//                    addNewDelete.animate()
//                            .translationY(200)
//                            .alpha(0f)
//                            .setDuration(500)
//                            .withEndAction(new Runnable() {
//                                @Override
//                                public void run() {
//                                    addNewDelete.setVisibility(View.GONE);
//                                }
//                            })
//                            .setInterpolator(new AnticipateInterpolator())
//                            .start();
//
//                    // Stop pulsating animation for the action button
//                    pulseAnimator.cancel();
//
//                    allVisible = false;
//                }
//            }
//        });

// ANIMATION OLD 1       addNewContact.setVisibility(View.INVISIBLE);
//        addNewDelete.setVisibility(View.INVISIBLE);
//
//        action.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!allVisible) {
//                    // Extend animation
//                    action.animate().rotation(45).setDuration(300).start();
//
//                    // Show the buttons with translation animation
//                    addNewContact.setVisibility(View.VISIBLE);
//                    addNewContact.setTranslationY(100); // Adjust the translation distance
//                    addNewContact.animate().translationY(0).setDuration(300).start();
//
//                    addNewDelete.setVisibility(View.VISIBLE);
//                    addNewDelete.setTranslationY(200); // Adjust the translation distance
//                    addNewDelete.animate().translationY(0).setDuration(300).start();
//
//                    allVisible = true;
//                } else {
//                    // Shrink animation
//                    action.animate().rotation(0).setDuration(300).start();
//
//                    // Hide the buttons with translation animation
//                    addNewContact.animate().translationY(100).setDuration(300).withEndAction(new Runnable() {
//                        @Override
//                        public void run() {
//                            addNewContact.setVisibility(View.GONE);
//                        }
//                    }).start();
//
//                    addNewDelete.animate().translationY(200).setDuration(300).withEndAction(new Runnable() {
//                        @Override
//                        public void run() {
//                            addNewDelete.setVisibility(View.GONE);
//                        }
//                    }).start();
//
//                    allVisible = false;
//                }
//            }
//        });

// Animation OLD 2
        addNewContact.setVisibility(View.INVISIBLE);
        addNewDelete.setVisibility(View.INVISIBLE);

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!allVisible) {
                    // Extend and fade in animation
                    action.animate().rotation(45).setDuration(300).start();

                    addNewContact.setVisibility(View.VISIBLE);
                    addNewContact.setAlpha(0f);
                    addNewContact.animate()
                            .translationY(0)
                            .alpha(1f)
                            .setDuration(300)
                            .start();

                    addNewDelete.setVisibility(View.VISIBLE);
                    addNewDelete.setAlpha(0f);
                    addNewDelete.animate()
                            .translationY(0)
                            .alpha(1f)
                            .setDuration(300)
                            .start();

                    allVisible = true;
                } else {
                    // Shrink and fade out animation
                    action.animate().rotation(0).setDuration(300).start();

                    addNewContact.animate()
                            .translationY(100)
                            .alpha(0f)
                            .setDuration(300)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    addNewContact.setVisibility(View.GONE);
                                }
                            }).start();

                    addNewDelete.animate()
                            .translationY(200)
                            .alpha(0f)
                            .setDuration(300)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    addNewDelete.setVisibility(View.GONE);
                                }
                            }).start();

                    allVisible = false;
                }
            }
        });

        addNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContactText.setVisibility(View.GONE);
                deleteContactText.setVisibility(View.GONE);
                actionText.setVisibility(View.GONE);

                addNewContact.setVisibility(View.GONE);
                addNewDelete.setVisibility(View.GONE);
                allVisible=false;
                action.shrink();
                allVisible=false;
                Map<String,String> hashMap = loadMap();
                if(hashMap.size()<3) {
                    pickContact();
                    numbersArrayAdapter.notifyDataSetChanged();
                }
                else Toast.makeText(getActivity(), "You can add maximum 3 contacts", Toast.LENGTH_SHORT).show();
            }
        });
        addNewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContactText.setVisibility(View.GONE);
                deleteContactText.setVisibility(View.GONE);
                actionText.setVisibility(View.GONE);

                addNewContact.setVisibility(View.GONE);
                addNewDelete.setVisibility(View.GONE);
                allVisible=false;
                action.shrink();
                Toast.makeText(getActivity(), "Deleted all contact except emergency number", Toast.LENGTH_SHORT).show();
                SharedPreferences pSharedPref = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
                pSharedPref.edit().remove("My_map").commit();
                String a = pSharedPref.getString("emergencyName","");
                String b = pSharedPref.getString("emergencyContact","");
                if(!a.equals("")) {
                    saveMap(a, b);
                    refreshData();
                }
            }
        });
        numbersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String> hashMap = loadMap();
                int index=0;
                for (Map.Entry<String,String> entry : hashMap.entrySet())
                {
                    if(index++==position)
                    {
                        SharedPreferences pSharedPref = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
                        pSharedPref.edit().remove("emergencyName").putString("emergencyName",entry.getKey()).apply();
                        pSharedPref.edit().remove("emergencyContact").putString("emergencyContact",entry.getValue()).apply();
                        break;
                    }
                }
                refreshData();
            }
        });

        numbersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String> hashMap = loadMap();
//                Toast.makeText(MainActivity.this, ""+hashMap.size(), Toast.LENGTH_SHORT).show();
//                if(hashMap.size()<3) {
                dialog.setContentView(R.layout.contact_entry);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.getWindow().getAttributes().windowAnimations = androidx.appcompat.R.style.Animation_AppCompat_Dialog;

                EditText textName = dialog.findViewById(R.id.userName);
                EditText textNumber = dialog.findViewById(R.id.userNumber);

                Button btnDone = dialog.findViewById(R.id.btnDone);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                int temp=0;
                for (Map.Entry<String,String> entry : hashMap.entrySet())
                {
                    if(temp++==position) {
                        textNumber.setText(entry.getValue());
                        textName.setText(entry.getKey());
                        break;
                    }
                }
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(textName.getText().toString().equals(""))
                        {
                            textName.setError("Please enter a name");
                        }
                        else if(textNumber.getText().toString().equals("") || textNumber.getText().toString().length()!=10)
                        {
                            textNumber.setError("Please enter a number");
                        }
                        else{
                            String contactName, contactNumber;
                            contactName = textName.getText().toString().trim();
                            contactNumber = textNumber.getText().toString().trim();
                            saveMap(contactName, contactNumber);
                            arrayList.add(new CustomListView(R.drawable.ic_person_foreground, contactName, contactNumber));
                            refreshData();
                            numbersArrayAdapter.notifyDataSetChanged();

                            dialog.dismiss();
                        }

//                            Toast.makeText(MainActivity.this, "Done clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences pSharedPref = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
                        String a = pSharedPref.getString("emergencyName","");
                        String b = pSharedPref.getString("emergencyContact","");
                        if(!a.equals(textName.getText().toString()) && !b.equals(textNumber.getText().toString())) {
                            hashMap.remove(textName.getText().toString(), textNumber.getText().toString());
                            JSONObject jsonObject = new JSONObject(hashMap);
                            String jsonString = jsonObject.toString();
                            pSharedPref.edit()
                                    .remove("My_map")
                                    .putString("My_map", jsonString)
                                    .apply();
                            refreshData();
                        }
                        else {
                            //warning
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            alertDialog.setTitle(Html.fromHtml("<font color='#332E2E'><b>Warning</b></font>"));
//                            alertDialog.getWindow().setColorMode(R.color.black);
                            alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_all);
                            alertDialog.setMessage(Html.fromHtml("<font color='#332E2E'>You can't delete default number.</font>"));
                            alertDialog.setIcon(R.drawable.baseline_crisis_alert_24);

                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getActivity(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                                }
                            });

                            alertDialog.show();
                        }

                        dialog.dismiss();
//                        Toast.makeText(getActivity(), "Delete clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
//                }
//                else Toast.makeText(MainActivity.this, "You can add maximum 3 contacts", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        ImageButton btnPolice = view.findViewById(R.id.btnPolice);
        ImageButton btnHospital = view.findViewById(R.id.btnAmbulance);
        btnPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check_permission_call = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE);
                if(check_permission_call == PackageManager.PERMISSION_GRANTED){
                    String dialCall = "tel:" + 100;
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dialCall)));
//                    Toast.makeText(MainActivity.this, "Call sent", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check_permission_call = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE);
                if(check_permission_call == PackageManager.PERMISSION_GRANTED){
                    String dialCall = "tel:" + 102;
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dialCall)));
                    Toast.makeText(getActivity(), "Call sent", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    public void refreshData(){
        arrayList = new ArrayList<CustomListView>();
        Map<String,String> hashMap = loadMap();
        for (Map.Entry<String,String> entry : hashMap.entrySet())
        {
            arrayList.add(new CustomListView(R.drawable.ic_person_foreground,entry.getKey(),entry.getValue()));
        }
        ContactAdaptor numbersArrayAdapter = new ContactAdaptor(getActivity(), arrayList);
        ListView numbersListView = (ListView) view.findViewById(R.id.contactListView);
        numbersListView.setAdapter(numbersArrayAdapter);
    }
    private void saveMap(String contactName,String contactNumber) {
        SharedPreferences pSharedPref = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        if (pSharedPref != null){
            Map<String,String> hashMap = loadMap();
            hashMap.put(contactName,contactNumber);
            JSONObject jsonObject = new JSONObject(hashMap);
            String jsonString = jsonObject.toString();
            pSharedPref.edit()
                    .remove("My_map")
                    .putString("My_map", jsonString)
                    .apply();
        }
    }

    private Map<String,String> loadMap() {
        Map<String,String> outputMap = new HashMap<>();
        SharedPreferences pSharedPref = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString("My_map", (new JSONObject()).toString());
                if (jsonString != null) {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Iterator<String> keysItr = jsonObject.keys();
                    while (keysItr.hasNext()) {
                        String key = keysItr.next();
                        String value = jsonObject.getString(key);
                        outputMap.put(key, value);
                    }
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return outputMap;
    }

    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
//        startActivityForResult(intent, PICK_CONTACT_REQUEST);
        startContactPickerActivity(intent,PICK_CONTACT_REQUEST);
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int resultCode = result.getResultCode();
                    Intent data = result.getData();

                    if (resultCode == Activity.RESULT_OK) {
                        if (data != null) {
                            Uri contactUri = data.getData();
                            if (contactUri != null) {
                                displayContactDetails(contactUri);
                            }
                        }
                    }
                }
            });
    private void startContactPickerActivity(Intent intent, int pickContactRequest) {
//        Intent intent1 = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        activityResultLaunch.launch(intent);
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
//            if (data != null) {
//                Uri contactUri = data.getData();
//                if (contactUri != null) {
//                    displayContactDetails(contactUri);
//                }
//            }
//        }
//    }

    private void displayContactDetails(Uri contactUri) {
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
//        Toast.makeText(getActivity(), projection[0]+" "+projection[1], Toast.LENGTH_SHORT).show();

        try (Cursor cursor = getActivity().getContentResolver().query(contactUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int phoneNumberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name = cursor.getString(nameColumnIndex);
                String phoneNumber = cursor.getString(phoneNumberColumnIndex);

                // Remove non-digit characters and get the last 10 digits of the phone number
                phoneNumber = phoneNumber.replaceAll("\\D+", "");
                if (phoneNumber.length() > 10) {
                    phoneNumber = phoneNumber.substring(phoneNumber.length() - 10);
                }
                saveMap(name, phoneNumber);
                arrayList.add(new CustomListView(R.drawable.ic_person_foreground, name, phoneNumber));
                refreshData();

//                nameTextView.setText("Contact Name: " + name);
//                phoneNumberTextView.setText("Phone Number: " + phoneNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences pSharedPref = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String a = pSharedPref.getString("emergencyName","");
        String b = pSharedPref.getString("emergencyContact","");
//        Toast.makeText(getActivity(), a+" "+b, Toast.LENGTH_SHORT).show();

    }
}