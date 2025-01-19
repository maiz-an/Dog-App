package com.example.dogapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class EducationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);


    }

    // Open YouTube video for caring for a dog
    public void openCareVideo(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=example_care_video"));
        startActivity(intent);
    }

    // Open YouTube video for feeding a dog
    public void openFeedingVideo(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=example_feeding_video"));
        startActivity(intent);
    }

    // Open YouTube video for training a dog
    public void openTrainingVideo(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=example_training_video"));
        startActivity(intent);
    }
}
