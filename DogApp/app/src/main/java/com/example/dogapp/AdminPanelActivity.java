package com.example.dogapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelActivity extends AppCompatActivity {
    private Button userBtn, productsBtn, ordersBtn, feedbackBtn;
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        // Initialize buttons
        userBtn = findViewById(R.id.userBtn);
        productsBtn = findViewById(R.id.productsBtn);
        ordersBtn = findViewById(R.id.ordersBtn);
        feedbackBtn = findViewById(R.id.feedbackBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        // Set onClickListeners to handle button clicks
        userBtn.setOnClickListener(v -> openUserManagement());
        productsBtn.setOnClickListener(v -> openProductsManagement());
        ordersBtn.setOnClickListener(v -> openOrdersManagement());
        feedbackBtn.setOnClickListener(v -> openFeedbackManagement());

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear session or perform logout actions here if necessary

                // Redirect to login activity and clear the activity stack
                Intent intent = new Intent(AdminPanelActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

    private void openUserManagement() {
        Intent intent = new Intent(AdminPanelActivity.this, UserListActivity.class);
        startActivity(intent);
    }

    private void openProductsManagement() {
        Intent intent = new Intent(AdminPanelActivity.this, ProductsActivity.class);
        startActivity(intent);
    }

    private void openOrdersManagement() {
        Intent intent = new Intent(AdminPanelActivity.this, AdminOrderManagementActivity.class);
        startActivity(intent);
    }

    private void openFeedbackManagement() {
        Log.d("AdminPanelActivity", "Feedback button clicked, launching FeedbackManagementActivity");
        Intent intent = new Intent(AdminPanelActivity.this, FeedbackManagementActivity.class);
        startActivity(intent);
    }

}
