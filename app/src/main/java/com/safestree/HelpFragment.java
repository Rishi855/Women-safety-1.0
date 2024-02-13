package com.safestree;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.safestree.about.appUse;
import com.safestree.about.community;


public class HelpFragment extends Fragment {


    TextView textCommunity,textAppUse;
    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        textCommunity = view.findViewById(R.id.textCommunity);
        textAppUse = view.findViewById(R.id.textAppUse);

        replaceFragment(new appUse());
        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
        Typeface regularTypeface = Typeface.defaultFromStyle(Typeface.NORMAL);
        textCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new community());
                textCommunity.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_top_navigation));
                textAppUse.setBackground(null);
                textCommunity.setTypeface(boldTypeface);
                textAppUse.setTypeface(regularTypeface);
            }
        });
        textAppUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new appUse());
                textAppUse.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_top_navigation));
                textCommunity.setBackground(null);
                textAppUse.setTypeface(boldTypeface);
                textCommunity.setTypeface(regularTypeface);
            }
        });



        return view;
    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragmentHelp,fragment);
        fragmentTransaction.commit();
    }

}