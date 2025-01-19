package com.example.dogapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    private EditText searchUserEditText, editUserName, editUserEmail, editUserPassword, editUserAddress;
    private Button searchUserBtn, saveChangesBtn, deleteUserBtn;
    private ListView userListView;
    private DatabaseHelper dbHelper;
    private ArrayList<String> userList;
    private ArrayAdapter<String> adapter;
    private int selectedUserId = -1;
    private LinearLayout editDeleteSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        searchUserEditText = findViewById(R.id.searchUserEditText);
        searchUserBtn = findViewById(R.id.searchUserBtn);
        userListView = findViewById(R.id.userListView);
        editUserName = findViewById(R.id.editUserName);
        editUserEmail = findViewById(R.id.editUserEmail);
        editUserPassword = findViewById(R.id.editUserPassword);
        editUserAddress = findViewById(R.id.editUserAddress);
        saveChangesBtn = findViewById(R.id.saveChangesBtn);
        deleteUserBtn = findViewById(R.id.deleteUserBtn);
        editDeleteSection = findViewById(R.id.editDeleteSection);
        dbHelper = new DatabaseHelper(this);

        userList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        userListView.setAdapter(adapter);

        // Load users initially
        loadUsers();

        searchUserBtn.setOnClickListener(v -> searchUsers(searchUserEditText.getText().toString().trim()));

        userListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUser = userList.get(position);
            selectedUserId = Integer.parseInt(selectedUser.split(":")[0]); // Assume ID is part of the list item
            loadSelectedUserDetails(selectedUserId);
        });

        saveChangesBtn.setOnClickListener(v -> {
            if (selectedUserId != -1) {
                saveUserDetails();
            }
        });

        deleteUserBtn.setOnClickListener(v -> {
            if (selectedUserId != -1) {
                deleteUser(selectedUserId);
            }
        });


    }

    private void loadUsers() {
        Cursor cursor = dbHelper.getAllUsers();
        userList.clear();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                Log.d("UserListActivity", "Users found: " + cursor.getCount()); // Debug log
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME));
                    userList.add(id + ": " + name);
                }
            } else {
                Log.d("UserListActivity", "No users found in database."); // Debug log
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void searchUsers(String query) {
        Cursor cursor = dbHelper.searchUsersByName(query);
        userList.clear();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                Log.d("UserListActivity", "Search results: " + cursor.getCount()); // Debug log
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME));
                    userList.add(id + ": " + name);
                }
            } else {
                Log.d("UserListActivity", "No users found matching query."); // Debug log
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void loadSelectedUserDetails(int userId) {
        Cursor cursor = dbHelper.getUserById(userId);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_EMAIL));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PASSWORD));
            String address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ADDRESS));

            editUserName.setText(name);
            editUserEmail.setText(email);
            editUserPassword.setText(password);
            editUserAddress.setText(address);

            editDeleteSection.setVisibility(View.VISIBLE);
            cursor.close();
        }
    }

    private void saveUserDetails() {
        String name = editUserName.getText().toString().trim();
        String email = editUserEmail.getText().toString().trim();
        String password = editUserPassword.getText().toString().trim();
        String address = editUserAddress.getText().toString().trim();

        int rowsAffected = dbHelper.updateUser(selectedUserId, name, email, password, address);

        if (rowsAffected > 0) {
            // Update was successful
            loadUsers(); // Refresh the user list after saving
            editDeleteSection.setVisibility(View.GONE);
            selectedUserId = -1;
            // Display a success message to the user
            Toast.makeText(UserListActivity.this, "User details updated successfully.", Toast.LENGTH_SHORT).show();
        } else {
            // Update failed, display an error message
            Toast.makeText(UserListActivity.this, "Error updating user details. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }


    private void deleteUser(int userId) {
        int rowsAffected = dbHelper.deleteUserById(userId);

        if (rowsAffected > 0) {
            // User deleted successfully
            loadUsers(); // Refresh the user list after deletion
            editDeleteSection.setVisibility(View.GONE);
            selectedUserId = -1;
            // Display a success message to the user
            Toast.makeText(UserListActivity.this, "User deleted successfully.", Toast.LENGTH_SHORT).show();
        } else {
            // Deletion failed, display an error message
            Toast.makeText(UserListActivity.this, "Error deleting user. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

}
