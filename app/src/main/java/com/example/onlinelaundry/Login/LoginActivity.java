package com.example.onlinelaundry.Login;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.onlinelaundry.Api.ApiClient;
import com.example.onlinelaundry.Api.ApiEndpoints;
import com.example.onlinelaundry.Dashboard.DashboardActivity;
import com.example.onlinelaundry.R;
import com.example.onlinelaundry.Registration.RegistrationActivity;
import com.example.onlinelaundry.Utils.LoadingDialog;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity  extends AppCompatActivity {

    private AppCompatButton btnLogin;
    private TextView btnRegister;
    private EditText txtUsername, txtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (isUserLoggedIn()) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }

        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);

        btnLogin = findViewById(R.id.loginButton);
        btnRegister = findViewById(R.id.registerButton);

        btnLogin.setOnClickListener(view ->{
            String getUsername = txtUsername.getText().toString().trim().toLowerCase();
            String getPassword = txtPassword.getText().toString().trim().toLowerCase();
            validateInfo(getUsername, getPassword);
        });

        btnRegister.setOnClickListener(view ->{
            startActivity( new Intent(this, RegistrationActivity.class));
        });
    }

    private void validateInfo(String username, String password){
        if(username.isEmpty()){
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
        }else if(password.isEmpty()){
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
        }else{
            savedLoginSession(username,password);
        }
    }

    private void savedLoginSession(String username, String password){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        ApiClient apiClient = new ApiClient();
        ApiEndpoints apiService = apiClient.getApiService();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<AccountResponse> call = apiService.loginAccount(requestBody);
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                AccountResponse createResponse = response.body();
                if (response.code() == 200) {
                    String accountId = createResponse.get_id();
                    String username = createResponse.getUsername();
                    Toast.makeText(LoginActivity.this, "Successfully login", Toast.LENGTH_SHORT).show();
                    setLoggedInStatus(true);
                    storeUserData(accountId, username);
                    loadingDialog.hide();
                    finish();
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                } else {
                    setLoggedInStatus(false);
                    loadingDialog.hide();
                    Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                loadingDialog.hide();
                Toast.makeText(LoginActivity.this, "Server failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeUserData(String personId, String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("accountId", personId);
        editor.putString("username", username);

        editor.apply();
    }

    private void setLoggedInStatus(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }
}
