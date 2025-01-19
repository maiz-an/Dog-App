package com.example.dogapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {
    private EditText feedbackEditText;
    private Button sendFeedbackButton;
    private static final String TAG = "FeedbackActivity";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedbackEditText = findViewById(R.id.feedbackEditText);
        sendFeedbackButton = findViewById(R.id.sendFeedbackButton);
        dbHelper = new DatabaseHelper(this);

        // Retrieve the current user's ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("UserID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        sendFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = feedbackEditText.getText().toString().trim();
                if (!feedback.isEmpty()) {
                    long result = dbHelper.insertFeedback(userId, feedback);
                    if (result != -1) {
                        Toast.makeText(FeedbackActivity.this, "Feedback sent!", Toast.LENGTH_SHORT).show();
                        feedbackEditText.setText("");
                        finish();
                    } else {
                        Toast.makeText(FeedbackActivity.this, "Failed to send feedback. Database error.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to insert feedback into the database.");
                    }
                } else {
                    Toast.makeText(FeedbackActivity.this, "Please enter your feedback.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
