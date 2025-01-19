package com.example.dogapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class FeedbackManagementActivity extends AppCompatActivity {
    private ListView feedbackListView;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> feedbackAdapter;
    private ArrayList<String> feedbackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_management);

        // Initialize the ListView and DatabaseHelper
        feedbackListView = findViewById(R.id.feedbackListView);
        dbHelper = new DatabaseHelper(this);
        feedbackList = new ArrayList<>();

        // Load the feedback from the database
        loadFeedback();

        // Set up the ArrayAdapter to display the feedback
        feedbackAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, feedbackList);
        feedbackListView.setAdapter(feedbackAdapter);
    }

    // Method to load feedback from the database
    private void loadFeedback() {
        Cursor cursor = dbHelper.getAllFeedback();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int userId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_FEEDBACK_USER_ID));
                String feedback = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_FEEDBACK_TEXT));
                feedbackList.add("User ID: " + userId + "\nFeedback: " + feedback);
            }
            cursor.close();
        }
    }
}
