package com.example.customnavigationbar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;


public class HelpFragment extends Fragment {

    Boolean booleanDefaultInfo,booleanSosInfo,booleanShakeInfo,booleanPolice,booleanHospital;
    ImageView imageViewDefault,imageViewSos,imageViewShake,imageViewPolice,imageViewHospital;

    MaterialCardView defaultCardView,sosCardView,shakeCardView,policeCardView,hospitalCardView;
    LinearLayout defaultInfo,sosInfo,shakeInfo,policeInfo,hospitalInfo;
    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        defaultCardView = view.findViewById(R.id.defaultCardView);
        defaultInfo = view.findViewById(R.id.defaultInfo);
        imageViewDefault = view.findViewById(R.id.defaultArrow);
        booleanDefaultInfo = false;
        defaultCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(booleanDefaultInfo) {
                    defaultInfo.setVisibility(View.GONE);
                    booleanDefaultInfo=false;
                    imageViewDefault.setImageResource(R.drawable.ic_to_bottom_foreground);
                }
                else {
                    defaultInfo.setVisibility(View.VISIBLE);
                    imageViewDefault.setImageResource(R.drawable.ic_to_up_foreground);
                    booleanDefaultInfo=true;
                }
            }
        });

        policeCardView = view.findViewById(R.id.callPoliceCardView);
        policeInfo = view.findViewById(R.id.policeInfo);
        imageViewPolice = view.findViewById(R.id.policeArrow);
        booleanPolice = false;
        policeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(booleanPolice) {
                    policeInfo.setVisibility(View.GONE);
                    booleanPolice=false;
                    imageViewPolice.setImageResource(R.drawable.ic_to_bottom_foreground);
                }
                else {
                    policeInfo.setVisibility(View.VISIBLE);
                    imageViewPolice.setImageResource(R.drawable.ic_to_up_foreground);
                    booleanPolice=true;
                }
            }
        });

        hospitalCardView = view.findViewById(R.id.callHospitalCardView);
        hospitalInfo = view.findViewById(R.id.hospitalInfo);
        imageViewHospital = view.findViewById(R.id.hospitalArrow);
        booleanHospital = false;
        hospitalCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(booleanHospital) {
                    hospitalInfo.setVisibility(View.GONE);
                    booleanHospital=false;
                    imageViewHospital.setImageResource(R.drawable.ic_to_bottom_foreground);
                }
                else {
                    hospitalInfo.setVisibility(View.VISIBLE);
                    imageViewHospital.setImageResource(R.drawable.ic_to_up_foreground);
                    booleanHospital=true;
                }
            }
        });

        shakeCardView = view.findViewById(R.id.shakeCardView);
        shakeInfo = view.findViewById(R.id.shakeInfo);
        imageViewShake = view.findViewById(R.id.shakeArrow);
        booleanShakeInfo = false;
        shakeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(booleanShakeInfo) {
                    shakeInfo.setVisibility(View.GONE);
                    booleanShakeInfo=false;
                    imageViewShake.setImageResource(R.drawable.ic_to_bottom_foreground);
                }
                else {
                    shakeInfo.setVisibility(View.VISIBLE);
                    imageViewShake.setImageResource(R.drawable.ic_to_up_foreground);
                    booleanShakeInfo=true;
                }
            }
        });

        sosCardView = view.findViewById(R.id.sosCardView);
        sosInfo = view.findViewById(R.id.sosInfo);
        imageViewSos = view.findViewById(R.id.sosArrow);
        booleanSosInfo = false;
        sosCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(booleanSosInfo) {
                    sosInfo.setVisibility(View.GONE);
                    booleanSosInfo=false;
                    imageViewSos.setImageResource(R.drawable.ic_to_bottom_foreground);
                }
                else {
                    sosInfo.setVisibility(View.VISIBLE);
                    imageViewSos.setImageResource(R.drawable.ic_to_up_foreground);
                    booleanSosInfo=true;
                }
            }
        });


        return view;
    }
}