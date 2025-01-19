package com.example.dogapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

public class RegisterActivity extends AppCompatActivity {
    private EditText cusname, email, password, confirmPassword, address;
    private Button registerBtn;
    private DatabaseHelper dbHelper;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize DatabaseHelper and UI elements
        dbHelper = new DatabaseHelper(this);
        cusname = findViewById(R.id.cusname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        address = findViewById(R.id.address);
        registerBtn = findViewById(R.id.registerBtn);
        ImageView facebookIcon = findViewById(R.id.facebookIcon);
        ImageView twitterIcon = findViewById(R.id.twitterIcon);
        ImageView instagramIcon = findViewById(R.id.instagramIcon);

        // Set onClickListener for the register button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input from user
                String nameText = cusname.getText().toString().trim();
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();
                String confirmPasswordText = confirmPassword.getText().toString().trim();
                String addressText = address.getText().toString().trim();

                Log.d(TAG, "Register button clicked.");

                // Validate input
                if (nameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty() || addressText.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the email already exists in the database
                if (dbHelper.checkUserExists(emailText)) {
                    Toast.makeText(RegisterActivity.this, "Email already exists!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Email already exists: " + emailText);
                    return; // Stop further execution
                }

                // Check if passwords match
                if (passwordText.equals(confirmPasswordText)) {
                    // Try to insert the new user into the database
                    long result = dbHelper.insertUser(nameText, emailText, passwordText, addressText);
                    if (result != -1) {
                        Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Registration successful.");
                        // Redirect to LoginActivity after successful registration
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration Failed! Try again.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Registration failed. Inserted ID: " + result);
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Passwords do not match.");
                }
            }
        });

        facebookIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebookLink();
            }
        });

        twitterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTwitterLink();
            }
        });

        instagramIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInstagramLink();
            }
        });
    }
    private void openFacebookLink() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/profile.php?id=61553304986903&mibextid=LQQJ4d"));
        startActivity(browserIntent);
    }

    private void openTwitterLink() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/mrde11_"));
        startActivity(browserIntent);
    }

    private void openInstagramLink() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/mr.de11_?igsh=M2NrOWcwcjNicGp0&utm_source=qr"));
        startActivity(browserIntent);
    }
}
