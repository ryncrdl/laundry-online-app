package com.example.onlinelaundry.Booking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private Spinner dateSpinner, servicesSpinner;
    private RadioButton cash, gCash, pickUp, delivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        cash = findViewById(R.id.cash);
        gCash = findViewById(R.id.gcash);
        pickUp = findViewById(R.id.pickUp);
        delivery = findViewById(R.id.delivery);
        cash.setChecked(true);
        pickUp.setChecked(true);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnHome = findViewById(R.id.btnHome);

        dateSpinner = findViewById(R.id.dateSpinner);

        Calendar calendar = Calendar.getInstance();
        Date startDate = calendar.getTime();

        List<String> dateList = getDates(startDate, 31);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dateList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dateSpinner.setAdapter(adapter);


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
            String getDate = dateSpinner.getSelectedItem().toString();
            String getService = servicesSpinner.getSelectedItem().toString();
            String getPaymentMethod = cash.isChecked() ? "cash" : "gCash";
            String getPickUpOption = cash.isChecked() ? "Pick Up" : "Delivery";

            submitBooking(accountId, getDate, getService, getPaymentMethod, getPickUpOption);
        });
    }

    private void submitBooking(String accountId, String date, String service, String paymentMethod, String pickUpOption){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        ApiClient apiClient = new ApiClient();
        ApiEndpoints apiService = apiClient.getApiService();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("accountId", accountId);
        jsonObject.addProperty("date", date);
        jsonObject.addProperty("service", service);
        jsonObject.addProperty("paymentMethod", paymentMethod);
        jsonObject.addProperty("pickUpOption", pickUpOption);

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

    private List<String> getDates(Date startDate, int numberOfDays) {
        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        for (int i = 0; i < numberOfDays; i++) {
            dateList.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dateList;
    }
}
