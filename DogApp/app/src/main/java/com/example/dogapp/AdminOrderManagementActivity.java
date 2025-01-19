package com.example.dogapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AdminOrderManagementActivity extends AppCompatActivity {

    private ListView ordersListView;
    private ArrayList<Order> ordersList;
    private OrdersAdapter ordersAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_management);

        ordersListView = findViewById(R.id.ordersListView);
        dbHelper = new DatabaseHelper(this);

        loadOrders();
    }

    private void loadOrders() {
        ordersList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllOrders();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int orderId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_USER_ID));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_STATUS));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ORDER_TOTAL));

                ordersList.add(new Order(orderId, userId, status, total));
            }
            cursor.close();
        }

        ordersAdapter = new OrdersAdapter(this, ordersList);
        ordersListView.setAdapter(ordersAdapter);
    }
}
