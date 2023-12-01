package com.example.onlinelaundry.Booking;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.onlinelaundry.Api.ApiClient;
import com.example.onlinelaundry.Api.ApiEndpoints;
import com.example.onlinelaundry.Login.LoginActivity;
import com.example.onlinelaundry.R;
import com.example.onlinelaundry.Registration.CreateAccount;
import com.example.onlinelaundry.Registration.RegistrationActivity;
import com.example.onlinelaundry.Utils.LoadingDialog;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity   extends AppCompatActivity {

    private AppCompatButton btnSubmit, btnHome;
    private Spinner servicesSpinner;
    private AppCompatButton dateSpinner;

    private RadioButton cash, gCash, pickUp, delivery, kg, pack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        cash = findViewById(R.id.cash);
        gCash = findViewById(R.id.gcash);
        kg = findViewById(R.id.kg);
        pickUp = findViewById(R.id.pickUp);
        delivery = findViewById(R.id.delivery);
        kg.setChecked(true);
        cash.setChecked(true);
        pickUp.setChecked(true);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnHome = findViewById(R.id.btnHome);

        dateSpinner = findViewById(R.id.dateSpinner);

        dateSpinner.setOnClickListener(view -> {
            openDateDialog(dateSpinner);
        });

        servicesSpinner = findViewById(R.id.servicesSpinner);

        String[] servicesArray = {"Wash/Dry/Fold", "Wash/Dry/Press", "Hand Wash/Fold/Press", "Rush Laundry", "Premium Dry Cleaning", "Standard Wash & Fold"};
        ArrayAdapter<String> servicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, servicesArray);

        servicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        servicesSpinner.setAdapter(servicesAdapter);

        btnHome.setOnClickListener(view -> {
            onBackPressed();
        });

        btnSubmit.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
            String accountId = sharedPreferences.getString("accountId", "");
            String getDate = dateSpinner.getText().toString();
            String getService = servicesSpinner.getSelectedItem().toString();
            String getUnit = kg.isChecked() ? "per Kg" : "per Package";
            String getPaymentMethod = cash.isChecked() ? "cash" : "gCash";
            String getPickUpOption = cash.isChecked() ? "Pick Up" : "Delivery";

            if(getDate.equals("Select Date") ){
                Toast.makeText(this, "Please select date.", Toast.LENGTH_SHORT).show();
            }else{
                submitBooking(accountId, getDate, getService,getUnit, getPaymentMethod, getPickUpOption);
            }
        });
    }

    private void openDateDialog(AppCompatButton btn) {
        DatePickerDialog dialog = new DatePickerDialog(this,
                (datePicker, year, month, day) -> {
                    String sYear = String.valueOf(year);
                    String sMonth = String.valueOf(month + 1);
                    String sDay = String.valueOf(day);

                    btn.setText(sYear + "-" + sMonth + "-"+ sDay);
                },

                2023, 0, 0);

        dialog.show();
    }

    private void submitBooking(String accountId, String date, String service, String unit, String paymentMethod, String pickUpOption){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        ApiClient apiClient = new ApiClient();
        ApiEndpoints apiService = apiClient.getApiService();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("accountId", accountId);
        jsonObject.addProperty("date", date);
        jsonObject.addProperty("service", service);
        jsonObject.addProperty("unit", unit);
        jsonObject.addProperty("paymentMethod", paymentMethod);
        jsonObject.addProperty("pickUpOption", pickUpOption);
        jsonObject.addProperty("total", 0);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<CreateBooking> call = apiService.createBooking(requestBody);
        call.enqueue(new Callback<CreateBooking>() {
            @Override
            public void onResponse(Call<CreateBooking> call, Response<CreateBooking> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BookingActivity.this, "Requested booking successfully submitted", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    loadingDialog.hide();

                } else {
                    loadingDialog.hide();
                    if(!response.errorBody().equals(null)){
                        try {
                            String errorResponse = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorResponse);
                            if (jsonObject.has("message")) {
                                String errorMessage = jsonObject.getString("message");
                                Toast.makeText(BookingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BookingActivity.this, "Booking failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BookingActivity.this, "Error processing response.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateBooking> call, Throwable t) {
                // Network error, handle failure case here
                loadingDialog.hide();
                Toast.makeText(BookingActivity.this, "Network error. Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
