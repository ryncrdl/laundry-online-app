package com.example.onlinelaundry.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private final String BASE_URL = "https://us-east-1.aws.data.mongodb-api.com/";
    private static ApiEndpoints apiEndpoints;
    public ApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiEndpoints = retrofit.create(ApiEndpoints.class);
    }
    public static ApiEndpoints getApiService() {
        return apiEndpoints;
    }

}
