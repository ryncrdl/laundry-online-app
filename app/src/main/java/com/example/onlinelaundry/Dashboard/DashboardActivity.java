package com.example.onlinelaundry.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.onlinelaundry.Booking.BookingActivity;
import com.example.onlinelaundry.Login.LoginActivity;
import com.example.onlinelaundry.R;
import com.example.onlinelaundry.Settings.SettingsActivity;
import com.example.onlinelaundry.Status.StatusActivity;

public class DashboardActivity  extends AppCompatActivity {

    private TextView wc;
    private String accountId;
    private AppCompatButton btnLogout, btnBooking, btnSettings,btnStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        wc = findViewById(R.id.wc);
        btnLogout = findViewById(R.id.btnLogout);
        btnBooking = findViewById(R.id.btnBooking);
        btnSettings = findViewById(R.id.btnSetting);
        btnStatus = findViewById(R.id.btnBookStatus);

        btnStatus.setOnClickListener(view-> startActivity(new Intent(this, StatusActivity.class)));


        btnSettings.setOnClickListener(view ->{
            startActivity(new Intent(this, SettingsActivity.class));
        });

        btnBooking.setOnClickListener(view ->{
            startActivity(new Intent(this, BookingActivity.class));
        });

        btnLogout.setOnClickListener(view -> {
            logoutUser();
        });

        getUserDataFromSharedPreferences();
    }

    private void logoutUser() {
        clearUserData();
        setLoggedInStatus(false);
        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
        finish();
    }

    private void clearUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("accountId");
        editor.remove("username");
        editor.apply();
    }

    private void setLoggedInStatus(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

    private void getUserDataFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String getAccountId = sharedPreferences.getString("accountId", "");
        String getUsername = sharedPreferences.getString("username", "");

        if (!getAccountId.isEmpty() && !getUsername.isEmpty() ) {
            accountId = getAccountId;
            wc.setText("Hi "+getUsername + "\nWelcome to Online Laundry");
        }
    }

}
