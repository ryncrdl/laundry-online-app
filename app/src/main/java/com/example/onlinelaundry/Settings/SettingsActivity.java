package com.example.onlinelaundry.Settings;


import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.onlinelaundry.Api.ApiClient;
import com.example.onlinelaundry.Api.ApiEndpoints;
import com.example.onlinelaundry.Dashboard.DashboardActivity;
import com.example.onlinelaundry.Login.AccountResponse;
import com.example.onlinelaundry.R;
import com.example.onlinelaundry.Registration.CreateAccount;
import com.example.onlinelaundry.Utils.ImageConverter;
import com.example.onlinelaundry.Utils.LoadingDialog;
import com.example.onlinelaundry.Utils.Validation;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private Uri selectedImageUri;
    private AppCompatButton btnHome, btnUpdate, btnChangePassword, btnUpload;
    private ImageView imgValidId;
    private TextView btnUpdatePassword;
    private RelativeLayout cChangePassword, cBasicInfo;
    private String accountId;
    private EditText txtFullName, txtUsername, txtPassword, txtConfirmPassword, txtEmail, txtContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnUpload = findViewById(R.id.btnUpload);
        imgValidId = findViewById(R.id.imgValidId);

        btnUpload.setOnClickListener(view->{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        txtFullName = findViewById(R.id.fullName);
        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);
        txtConfirmPassword = findViewById(R.id.confirmPassword);
        txtEmail = findViewById(R.id.email);
        txtContact = findViewById(R.id.contactNumber);


        cChangePassword = findViewById(R.id.changePasswordContainer);
        cBasicInfo = findViewById(R.id.containerBasicDetails);

       btnHome = findViewById(R.id.btnHome);
       btnUpdate = findViewById(R.id.btnUpdate);
       btnUpdatePassword = findViewById(R.id.btnPasswordUpdate);
       btnChangePassword = findViewById(R.id.btnChangePassword);

        btnUpdatePassword.setOnClickListener(view -> {
            cChangePassword.setVisibility(View.VISIBLE);
            cBasicInfo.setVisibility(View.GONE);
        });


       btnHome.setOnClickListener(view -> {
           onBackPressed();
       });


       btnUpdate.setOnClickListener(view -> {
           String fullName = txtFullName.getText().toString().trim().toLowerCase();
           String username = txtUsername.getText().toString().trim().toLowerCase();
           String email = txtEmail.getText().toString().trim().toLowerCase();
           String contact = txtContact.getText().toString().trim().toLowerCase();

           if(fullName.isEmpty() || username.isEmpty()  || email.isEmpty() || contact.isEmpty()){
               Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
           }else if (fullName.length() < 5) {
               Toast.makeText(this, "Full name must be at least 5 characters", Toast.LENGTH_SHORT).show();
           } else if (username.length() < 5) {
               Toast.makeText(this, "Username must be at least 5 characters", Toast.LENGTH_SHORT).show();
           }else if (!Validation.isValidEmail(email)) {
               Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show();
           } else if (!Validation.isValidContact(contact)) {
               Toast.makeText(this, "Invalid contact format.", Toast.LENGTH_SHORT).show();
           }else{
               String imgString = ImageConverter.encodeImageToBase64JPEG(imgValidId);
               updateBasicDetails(imgString, accountId, fullName, username, email, contact);
           }
       });

       btnChangePassword.setOnClickListener(view ->{
           String password = txtPassword.getText().toString().trim().toLowerCase();
           String confirmPassword = txtConfirmPassword.getText().toString().trim().toLowerCase();

          if (password.length() < 8) {
               Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
           } else if (!password.equals(confirmPassword)) {
               Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
           }else{
              changePassword(accountId, password);
          }
       });


        btnHome.setOnClickListener(view -> {
            cChangePassword.setVisibility(View.GONE);
            cBasicInfo.setVisibility(View.VISIBLE);
            onBackPressed();
        });

        getUserDataFromSharedPreferences();

    }

    private void changePassword(String accountId, String password) {
        LoadingDialog loading = new LoadingDialog(this);
        loading.show();
        ApiClient apiClient = new ApiClient();
        ApiEndpoints apiService = apiClient.getApiService();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("accountId", accountId);
        jsonObject.addProperty("fullName", "");
        jsonObject.addProperty("username", "");
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("email", "");
        jsonObject.addProperty("contact", "");
        jsonObject.addProperty("validId", "");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<CreateAccount> call = apiService.updateUserById(requestBody);
        call.enqueue(new Callback<CreateAccount>() {
            @Override
            public void onResponse(Call<CreateAccount> call, Response<CreateAccount> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SettingsActivity.this, "Password has been changed", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    loading.hide();
                }
            }
            @Override
            public void onFailure(Call<CreateAccount> call, Throwable t) {
                loading.hide();
                Toast.makeText(SettingsActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBasicDetails(String validId, String accountId, String fullName, String username, String email, String contact) {
        LoadingDialog loading = new LoadingDialog(this);
        loading.show();
        ApiClient apiClient = new ApiClient();
        ApiEndpoints apiService = apiClient.getApiService();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("accountId", accountId);
        jsonObject.addProperty("fullName", fullName);
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", "");
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("contact", contact);
        jsonObject.addProperty("validId", validId);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<CreateAccount> call = apiService.updateUserById(requestBody);
        call.enqueue(new Callback<CreateAccount>() {
            @Override
            public void onResponse(Call<CreateAccount> call, Response<CreateAccount> response) {
                if (response.isSuccessful()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("username", username);

                    editor.apply();


                    Toast.makeText(SettingsActivity.this, "Details updated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(SettingsActivity.this, DashboardActivity.class));
                    loading.hide();
                }
            }
            @Override
            public void onFailure(Call<CreateAccount> call, Throwable t) {
                loading.hide();
                Toast.makeText(SettingsActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getUserDataFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String getAccountId = sharedPreferences.getString("accountId", "");

        if (!getAccountId.isEmpty()) {
            accountId = getAccountId;
            getUserDetails(getAccountId);

        }
    }

    private void getUserDetails(String accountId){
        LoadingDialog loading = new LoadingDialog(this);
        loading.show();

        ApiClient apiClient = new ApiClient();
        ApiEndpoints apiService = apiClient.getApiService();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("accountId", accountId);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<AccountResponse> call = apiService.getUserById(requestBody);

        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                if (response.isSuccessful()) {
                    AccountResponse accountResponse = response.body();
                    if (accountResponse != null) {
                        String getFullName = accountResponse.getFullName();
                        String getUsername = accountResponse.getUsername();
                        String getEmail = accountResponse.getEmail();
                        String getContact = accountResponse.getContact();

                        String getValidId = accountResponse.getValidId();

                        Bitmap bitmapImage = ImageConverter.StringToImage(getValidId);
                        Drawable image = new BitmapDrawable(getResources(), bitmapImage);
                        imgValidId.setBackground(image);

                        txtFullName.setText(getFullName.toString());
                        txtUsername.setText(getUsername.toString());
                        txtEmail.setText(getEmail.toString());
                        txtContact.setText(getContact.toString());

                        loading.hide();
                    }
                }
            }
            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                loading.hide();
                Toast.makeText(SettingsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                Bitmap imageBitmap = getBitmapFromUri(selectedImageUri);
                if (imageBitmap != null) {
                    Drawable productImage = new BitmapDrawable(this.getResources(), imageBitmap);
                    imgValidId.setBackground(productImage);
                }
            } else {
                imgValidId.setBackgroundResource(R.drawable.bg);
            }
        }
    }


    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
