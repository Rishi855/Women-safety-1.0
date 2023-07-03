package com.example.customnavigationbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import org.w3c.dom.Text;

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

    View view;
    ArrayList<CustomListView> arrayList;
    TextView addContactText,deleteContactText,actionText;
    Boolean allVisible;
    public EmergencyContantFragment() {
        // Required empty public constructor
    }

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

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!allVisible)
                {
                    addNewContact.show();
                    addNewDelete.show();

                    addContactText.setVisibility(View.VISIBLE);
                    deleteContactText.setVisibility(View.VISIBLE);
                    actionText.setVisibility(View.VISIBLE);
                    action.extend();
                    allVisible=true;
                }
                else
                {
                    addContactText.setVisibility(View.GONE);
                    deleteContactText.setVisibility(View.GONE);
                    actionText.setVisibility(View.GONE);

                    addNewContact.setVisibility(View.GONE);
                    addNewDelete.setVisibility(View.GONE);
                    allVisible=false;
                    action.shrink();
                    allVisible=false;
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
//                Toast.makeText(MainActivity.this, ""+hashMap.size(), Toast.LENGTH_SHORT).show();
                if(hashMap.size()<3) {
                    dialog.setContentView(R.layout.contact_entry);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    dialog.getWindow().getAttributes().windowAnimations = androidx.appcompat.R.style.Animation_AppCompat_Dialog;

                    EditText textName = dialog.findViewById(R.id.userName);
                    EditText textNumber = dialog.findViewById(R.id.userNumber);

                    Button btnDone = dialog.findViewById(R.id.btnDone);
                    Button btnCancel = dialog.findViewById(R.id.btnCancel);

                    btnDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(textName.getText().toString().equals(""))
                            {
                                textName.setError("Please enter a name");
                            }
                            else if(textNumber.getText().toString().equals("") || textNumber.getText().toString().trim().length()!=10)
                            {
                                textNumber.setError("Please enter valid number");
//                                Toast.makeText(getActivity(), ""+textNumber.getText().toString().trim().length(), Toast.LENGTH_SHORT).show();
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
//                            Toast.makeText(getActivity(), ""+textNumber.getText().toString().trim().length(), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(getActivity(), "Done clicked", Toast.LENGTH_SHORT).show();
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Delete clicked", Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog.show();
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
//                        Toast.makeText(getActivity(), entry.getKey()+" "+entry.getValue(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences pSharedPref = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String a = pSharedPref.getString("emergencyName","");
        String b = pSharedPref.getString("emergencyContact","");
//        Toast.makeText(getActivity(), a+" "+b, Toast.LENGTH_SHORT).show();

    }
}