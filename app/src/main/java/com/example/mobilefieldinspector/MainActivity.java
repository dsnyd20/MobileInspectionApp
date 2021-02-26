package com.example.mobilefieldinspector;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilefieldinspector.database.AppRepository;

import java.util.concurrent.Executor;

import static com.example.mobilefieldinspector.utilities.Constants.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel(MainActivity.this, CHANNEL_ID);

    }

    private void createNotificationChannel(Context context, String CHANNEL_ID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Mobile Field Inspector";
            String description = "Mobile Field Inspector Alert";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showRailcarList(View view) {
        Intent intent = new Intent(this, RailcarListActivity.class);
        startActivity(intent);
    }

    public void showShopList(View view) {
        Intent intent = new Intent(this, ShopListActivity.class);
        startActivity(intent);
    }

    public void showInspectionList(View view) {
        Intent intent = new Intent(this, InspectionListActivity.class);
        startActivity(intent);
    }
}