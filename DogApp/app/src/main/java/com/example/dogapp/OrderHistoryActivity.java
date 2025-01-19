package com.example.dogapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class OrderHistoryActivity extends AppCompatActivity {

    private ListView ordersListView;
    private ArrayList<Order> ordersList;
    private UserOrdersAdapter ordersAdapter;  // Use the new adapter
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        ordersListView = findViewById(R.id.ordersListView);
        dbHelper = new DatabaseHelper(this);

        loadUserOrders();
    }

    private void loadUserOrders() {
        ordersList = new ArrayList<>();

        // Get the current user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("UserID", -1);

        if (userId != -1) {
            Cursor cursor = dbHelper.getOrdersByUserId(userId);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int orderId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_ID));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_STATUS));
                    double total = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_TOTAL));

                    ordersList.add(new Order(orderId, userId, status, total));
                }
                cursor.close();
            }
        }

        ordersAdapter = new UserOrdersAdapter(this, ordersList);  // Initialize the new adapter
        ordersListView.setAdapter(ordersAdapter);
    }
}
