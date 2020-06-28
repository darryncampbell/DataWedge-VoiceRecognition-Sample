package com.darryncampbell.dwvoicerecognition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import static com.darryncampbell.dwvoicerecognition.DWUtilities.EXTRA_INTENT_ACTION;
import static com.darryncampbell.dwvoicerecognition.DWUtilities.PROFILE_NAME;

public class MainActivity extends AppCompatActivity implements Subject {

    public static final String LOG_TAG = "DWVoiceSample";
    private List<Observer> observers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //  Some issue with the hardware contention on the mic if we try to create the profile each time without previously deleting it
        DWUtilities.DeleteDWProfile(getApplicationContext(), PROFILE_NAME);
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                DWUtilities.CreateDWProfile(getApplicationContext(), PROFILE_NAME);
            }
        };
        handler.postDelayed(r, 1000);
        observers = new ArrayList<>();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(EXTRA_INTENT_ACTION);
        registerReceiver(myBroadcastReceiver, filter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(myBroadcastReceiver);
    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(EXTRA_INTENT_ACTION))
            {
                //  Received voice input.  Notify observers
                Bundle b = intent.getExtras();
                for (String key : b.keySet())
                {
                    //  todo why is this being called so many times??
                    Log.v(LOG_TAG, key);
                    String recognisedSpeech = intent.getStringExtra("com.symbol.datawedge.data_string");
                    Log.i(LOG_TAG, recognisedSpeech);
                    notifyObservers(recognisedSpeech);
                }
            }
        }
    };

    @Override
    public void register(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unregister(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String recognisedText) {
        for (final Observer observer : observers) {
            observer.update(recognisedText);
        }
    }
}