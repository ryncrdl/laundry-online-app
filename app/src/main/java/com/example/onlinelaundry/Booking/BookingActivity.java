package com.example.onlinelaundry.Booking;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.onlinelaundry.Api.ApiClient;
import com.example.onlinelaundry.Api.ApiEndpoints;
import com.example.onlinelaundry.Login.LoginActivity;
import com.example.onlinelaundry.R;
import com.example.onlinelaundry.Registration.CreateAccount;
import com.example.onlinelaundry.Registration.RegistrationActivity;
import com.example.onlinelaundry.Status.StatusActivity;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity   extends AppCompatActivity {

    private AppCompatButton btnSubmit, btnHome;
    private Spinner servicesSpinner;
    private AppCompatButton dateSpinner;
    private EditText kg;
    private RadioButton cash, gCash, pickUp, delivery;
    private TextView txttotal;
    AtomicReference<Double> getTotal = new AtomicReference<>(0.0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        cash = findViewById(R.id.cash);
        gCash = findViewById(R.id.gcash);
        kg = findViewById(R.id.kg);
        txttotal = findViewById(R.id.total);
        String[] servicesArray = {"Wash/Dry/Fold", "Wash/Dry/Press", "Hand Wash/Fold/Press", "Rush Laundry", "Premium Dry Cleaning", "Standard Wash & Fold"};

        kg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not needed for your case, but required to implement
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not needed for your case, but required to implement
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Get the dynamically changing value of EditText
                String dynamicValue = editable.toString();
                String selectedService = servicesSpinner.getSelectedItem().toString().toLowerCase();
                updatePrice(selectedService, dynamicValue);

            }
        });



        pickUp = findViewById(R.id.pickUp);
        delivery = findViewById(R.id.delivery);
        cash.setChecked(true);
        pickUp.setChecked(true);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnHome = findViewById(R.id.btnHome);

        dateSpinner = findViewById(R.id.dateSpinner);

        dateSpinner.setOnClickListener(view -> {
            openDateDialog(dateSpinner);
        });

        servicesSpinner = findViewById(R.id.servicesSpinner);
        servicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected service from the spinner
                String selectedService = servicesArray[position].toLowerCase();

                // Update the price based on the selected service
                updatePrice(selectedService, kg.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where nothing is selected (if needed)
            }
        });

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
            String getUnit = kg.getText().toString();
            String getPaymentMethod = cash.isChecked() ? "cash" : "gCash";
            String getPickUpOption = cash.isChecked() ? "Pick Up" : "Delivery";
            String gTotal = String.valueOf(getTotal.get());
            if(getDate.equals("Select Date") ){
                Toast.makeText(this, "Please select date.", Toast.LENGTH_SHORT).show();
            }else{
                submitBooking(accountId, getDate, getService,getUnit, getPaymentMethod, getPickUpOption, gTotal);
            }
        });
    }

    private void updatePrice(String selectedService, String dynamicValue) {
        double price = 0.0;

        switch (selectedService) {
            case "wash/dry/fold":
                price = 60.0;
                break;
            case "wash/dry/press":
                price = 70.0;
                break;
            case "hand wash/fold/press":
                price = 80.0;
                break;
            case "rush laundry":
                price = 120.0;
                break;
            case "premium dry cleaning":
                price = 100.0;
                break;
            case "standard wash & fold":
                price = 55.0;
                break;
        }

        if (!dynamicValue.isEmpty()) {
            double total = price * Double.parseDouble(dynamicValue);
            getTotal.set(total);
            txttotal.setText("Total: " + total);
        }else{
            getTotal.set(0.0);
            txttotal.setText("Total: P0.00");
        }
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

    private void submitBooking(String accountId, String date, String service, String unit, String paymentMethod, String pickUpOption, String total){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        ApiClient apiClient = new ApiClient();
        ApiEndpoints apiService = apiClient.getApiService();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("accountId", accountId);
        jsonObject.addProperty("date", date);
        jsonObject.addProperty("service", service);
        jsonObject.addProperty("kg", unit);
        jsonObject.addProperty("total", total);
        jsonObject.addProperty("paymentMethod", paymentMethod);
        jsonObject.addProperty("pickUpOption", pickUpOption);
        jsonObject.addProperty("status", false);

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
