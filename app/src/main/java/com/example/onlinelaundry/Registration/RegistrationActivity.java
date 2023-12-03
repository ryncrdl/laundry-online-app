package com.example.onlinelaundry.Registration;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.onlinelaundry.Api.ApiClient;
import com.example.onlinelaundry.Api.ApiEndpoints;
import com.example.onlinelaundry.Login.LoginActivity;
import com.example.onlinelaundry.R;
import com.example.onlinelaundry.Utils.ImageConverter;
import com.example.onlinelaundry.Utils.LoadingDialog;
import com.example.onlinelaundry.Utils.ModalMessage;
import com.example.onlinelaundry.Utils.Validation;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    private Uri selectedImageUri;
    private AppCompatButton btnRegister, btnUpload;
    private ImageView imgValidId;
    private TextView btnLogin;
    private EditText txtFullName, txtUsername, txtPassword, txtConfirmPassword, txtEmail, txtContact, txtAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        btnUpload = findViewById(R.id.btnUpload);
        imgValidId = findViewById(R.id.imgValidId);

        btnUpload.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });


        btnLogin = findViewById(R.id.loginButton);
        btnRegister = findViewById(R.id.btnRegister);
        txtFullName = findViewById(R.id.fullName);
        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);
        txtConfirmPassword = findViewById(R.id.confirmPassword);
        txtEmail = findViewById(R.id.email);
        txtContact = findViewById(R.id.contactNumber);
        txtAddress = findViewById(R.id.address);
        btnLogin.setOnClickListener(view->{
            startActivity(new Intent(this, LoginActivity.class));
        });

        btnRegister.setOnClickListener(view -> {
            String fullName = txtFullName.getText().toString().trim().toLowerCase();
            String username = txtUsername.getText().toString().trim().toLowerCase();
            String password = txtPassword.getText().toString().trim().toLowerCase();
            String confirmPassword = txtConfirmPassword.getText().toString().trim().toLowerCase();
            String email = txtEmail.getText().toString().trim().toLowerCase();
            String contact = txtContact.getText().toString().trim().toLowerCase();
            String address = txtAddress.getText().toString().trim().toLowerCase();
            validateDetails(fullName, username, password, confirmPassword, email, contact, address);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Get the selected image as a Bitmap
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

    private void validateDetails(String fullName, String username, String password, String confirmPassword, String email, String contact, String address) {
        if(fullName.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || contact.isEmpty() || address.isEmpty()){
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        }else if (fullName.length() < 5) {
            Toast.makeText(this, "Full name must be at least 5 characters", Toast.LENGTH_SHORT).show();
        } else if (username.length() < 5) {
            Toast.makeText(this, "Username must be at least 5 characters", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
        } else if (!Validation.isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show();
        } else if (!Validation.isValidContact(contact)) {
            Toast.makeText(this, "Invalid contact format.", Toast.LENGTH_SHORT).show();
        }else if(selectedImageUri == null){
            Toast.makeText(this, "Select Valid ID.", Toast.LENGTH_SHORT).show();
        }else{
            String imgString = ImageConverter.encodeImageToBase64JPEG(imgValidId);
            createAccount(imgString, fullName,username,password,email,contact, address);
        }
    }

    private void createAccount(String imgString, String fullName, String username, String password, String email, String contact, String address) {
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        ApiClient apiClient = new ApiClient();
        ApiEndpoints apiService = apiClient.getApiService();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fullName", fullName);
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("contact", contact);
        jsonObject.addProperty("validId", imgString);
        jsonObject.addProperty("rfidNo", "");
        jsonObject.addProperty("verify", false);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<CreateAccount> call = apiService.createAccount(requestBody);
        call.enqueue(new Callback<CreateAccount>() {
            @Override
            public void onResponse(Call<CreateAccount> call, Response<CreateAccount> response) {
                if (response.isSuccessful()) {
                    loadingDialog.hide();
                    ModalMessage messageDialog = new ModalMessage(RegistrationActivity.this);
                    messageDialog.setMessage("Your Account has been verifying\nPlease wait for approval.");

                    messageDialog.setOkayButton("Okay", v -> {
                        messageDialog.dismiss();
                       finish();
                       onBackPressed();
                    });

                    messageDialog.show();

                } else {
                    loadingDialog.hide();
                    if(!response.errorBody().equals(null)){
                        try {
                            String errorResponse = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorResponse);
                            if (jsonObject.has("message")) {
                                String errorMessage = jsonObject.getString("message");
                                Toast.makeText(RegistrationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegistrationActivity.this, "Error processing response.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateAccount> call, Throwable t) {
                // Network error, handle failure case here
                loadingDialog.hide();
                Toast.makeText(RegistrationActivity.this, "Network error. Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
