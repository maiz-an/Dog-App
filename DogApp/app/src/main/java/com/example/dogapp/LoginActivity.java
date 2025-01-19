package com.example.dogapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button loginBtn, signUpBtn;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signUpBtn = findViewById(R.id.signUpBtn);
        ImageView facebookIcon = findViewById(R.id.facebookIcon);
        ImageView twitterIcon = findViewById(R.id.twitterIcon);
        ImageView instagramIcon = findViewById(R.id.instagramIcon);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();

                // Check for admin credentials
                if (emailText.equals("admin@dogapp.com") && passwordText.equals("admin123")) {
                    // If the admin credentials are correct, navigate to the Admin Dashboard
                    Intent intent = new Intent(LoginActivity.this, AdminPanelActivity.class);
                    startActivity(intent);
                    return;
                }

                Cursor userCursor = dbHelper.getUser(emailText, passwordText);
                if (userCursor != null && userCursor.moveToFirst()) {
                    String userName = userCursor.getString(userCursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME));
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("userName", userName);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Email or Password!", Toast.LENGTH_SHORT).show();
                }
                userCursor.close();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
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

