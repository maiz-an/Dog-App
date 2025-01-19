package com.example.dogapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private Button profileBtn, catalogBtn, cartBtn, educationBtn,feedbackBtn;
    private TextView welcomeTextView;

    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        profileBtn = findViewById(R.id.profileBtn);
        catalogBtn = findViewById(R.id.catalogBtn);
        cartBtn = findViewById(R.id.cartBtn);
        educationBtn = findViewById(R.id.educationBtn);
        feedbackBtn = findViewById(R.id.feedbackBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        // Retrieve the user's name from the intent
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        welcomeTextView.setText("Welcome, " + userName + "!");



        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform any session clearing if necessary
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
                startActivity(intent);
                finish(); // Close the current activity
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
            }
        });


        catalogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CatalogActivity.class);
                startActivity(intent);
            }
        });

        cartBtn.setOnClickListener(v -> {
            Intent intent1 = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent1);
        });


        educationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EducationActivity.class);
                startActivity(intent);
            }
        });

        // Set up feedback button
        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the FeedbackActivity
                Intent intent = new Intent(HomeActivity.this, FeedbackActivity.class);
                startActivity(intent);
            }
        });
    }
}
