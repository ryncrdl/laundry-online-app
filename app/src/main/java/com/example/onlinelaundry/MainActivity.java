package com.example.onlinelaundry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.os.Bundle;
import android.widget.TextView;

import com.example.onlinelaundry.Login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView loadingText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingLabel);
        final int totalProgress = 100;
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            int progress = 0;
            @Override
            public void run() {
                progress += 1;
                progressBar.setProgress(progress);

                if (progress < totalProgress) {
                    loadingText.setText(progress + "%");
                    handler.postDelayed(this, 0);
                } else {
                    finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        }, 0);
    }
}