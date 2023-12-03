package com.example.onlinelaundry.Status;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelaundry.Api.ApiClient;
import com.example.onlinelaundry.Api.ApiEndpoints;
import com.example.onlinelaundry.Booking.BookingResponse;
import com.example.onlinelaundry.Booking.CreateBooking;
import com.example.onlinelaundry.Login.AccountResponse;
import com.example.onlinelaundry.R;
import com.example.onlinelaundry.Settings.SettingsActivity;
import com.example.onlinelaundry.Utils.ImageConverter;
import com.example.onlinelaundry.Utils.LoadingDialog;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusActivity extends AppCompatActivity {

    private static List<BookingResponse> booking = new ArrayList<>();
    private static StatusAdapter statusAdapter;

    private AppCompatButton btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_status);
        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(view -> {
            onBackPressed();
        });


        RecyclerView rvBooking = findViewById(R.id.rvBooking);
        rvBooking.setLayoutManager(new LinearLayoutManager(this));
        statusAdapter = new StatusAdapter(this, booking);
        rvBooking.setAdapter(statusAdapter);

        getUserDataFromSharedPreferences();
    }

    private void getUserDataFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String getAccountId = sharedPreferences.getString("accountId", "");
        if (!getAccountId.isEmpty()) {
            getBooking(getAccountId);
        }
    }

    private void getBooking(String accountId){
        LoadingDialog loading = new LoadingDialog(statusAdapter.context);
        loading.show();

        ApiClient apiClient = new ApiClient();
        ApiEndpoints apiService = apiClient.getApiService();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("accountId", accountId);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<List<BookingResponse>> call = apiService.getBooking(requestBody);
        call.enqueue(new Callback<List<BookingResponse>>() {
            @Override
            public void onResponse(Call<List<BookingResponse>> call, Response<List<BookingResponse>> response) {
                if (response.isSuccessful()) {
                    List<BookingResponse> bookingFetch = response.body();
                    if (bookingFetch != null) {
                        booking.clear();
                        booking.addAll(bookingFetch);
                        statusAdapter.notifyDataSetChanged();
                        loading.hide();
                    }else{
                        loading.hide();
                        Toast.makeText(StatusActivity.this, "You don't have any booking request", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<List<BookingResponse>> call, Throwable t) {
                loading.hide();
                Toast.makeText(StatusActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }
}
