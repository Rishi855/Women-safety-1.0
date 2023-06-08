package com.example.customnavigationbar;

import static com.example.customnavigationbar.R.id.location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.customnavigationbar.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new LocationFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId()== R.id.location)
            {
                replaceFragment(new LocationFragment());
            }
            else if(item.getItemId()== R.id.emergencyContacts)
            {
                replaceFragment(new EmergencyContantFragment());
            }
            else if(item.getItemId()== R.id.sosAlert)
            {
                replaceFragment(new SosFragment());
            }
            else if(item.getItemId()== R.id.setting)
            {
                replaceFragment(new SettingFragment());
            }
            else if(item.getItemId()== R.id.help)
            {
                replaceFragment(new HelpFragment());
            }
            return true;
        });
    }
    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment,fragment);
        fragmentTransaction.commit();
    }
}