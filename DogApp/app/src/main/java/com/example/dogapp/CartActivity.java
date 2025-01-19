package com.example.dogapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemInteractionListener {

    private DatabaseHelper dbHelper;
    private ArrayList<CartItem> cartItems;
    private CartAdapter cartAdapter;
    private GridView cartGridView;
    private TextView totalAmountTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        dbHelper = new DatabaseHelper(this);

        // Manual insertion of a test user for debugging purposes
        long userId = dbHelper.insertUser("xMaizxx", "maiz@xam.com", "password", "Test Address");
        Log.d("ManualTest", "Inserted User ID: " + userId);

        // Save this userId to SharedPreferences for testing
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("UserID", (int) userId);
        editor.apply();

        cartGridView = findViewById(R.id.cartGridView);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        Button proceedToPaymentButton = findViewById(R.id.proceedToPaymentButton);

        loadCartItems();
        updateTotalAmount();

        proceedToPaymentButton.setOnClickListener(v -> {
            int retrievedUserId = getCurrentUserId(); // Get current user ID
            Log.d("CartActivity", "Proceeding with userId: " + retrievedUserId); // Log user ID

            double totalAmount = calculateTotalAmount(); // Calculate total amount
            Log.d("CartActivity", "Calculated total amount: " + totalAmount); // Log total amount

            // Log before attempting to insert the order
            Log.d("CartActivity", "Attempting to insert order. userId: " + retrievedUserId + ", totalAmount: " + totalAmount);

            long orderId = dbHelper.insertOrder(retrievedUserId, totalAmount);

            if (orderId != -1) {
                Toast.makeText(CartActivity.this, "Order Confirmed. Order ID: " + orderId, Toast.LENGTH_SHORT).show();
                dbHelper.clearCart(); // Clear the cart
            } else {
                Toast.makeText(CartActivity.this, "Failed to confirm order. Please check logs for more details.", Toast.LENGTH_SHORT).show();
                Log.e("CartActivity", "Order insertion failed. Check the userId and database constraints.");
            }
        });





    }



    private double calculateTotalAmount() {
        double totalAmount = 0;
        for (CartItem item : cartItems) {
            totalAmount += item.getSubtotal();
        }
        return totalAmount;
    }


    private void loadCartItems() {
        cartItems = new ArrayList<>();
        Cursor cursor = dbHelper.getAllCartItems(); // Assuming this method returns a Cursor

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CART_PRODUCT_ID));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CART_QUANTITY));
                double price = getProductPriceById(productId); // Implemented method
                String productName = getProductNameById(productId); // Implemented method

                // Calculate subtotal
                double subtotal = price * quantity;

                // Create CartItem with the required parameters
                cartItems.add(new CartItem(productId, productName, price, quantity, subtotal));
            }
            cursor.close();
        }

        cartAdapter = new CartAdapter(this, cartItems);
        cartGridView.setAdapter(cartAdapter);
    }


    private double getProductPriceById(int productId) {
        // Implement this method to get the product price from the database
        Cursor cursor = dbHelper.getProductById(productId);
        if (cursor != null && cursor.moveToFirst()) {
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PRICE));
            cursor.close();
            return price;
        }
        return 0;
    }

    private String getProductNameById(int productId) {
        // Implement this method to get the product name from the database
        Cursor cursor = dbHelper.getProductById(productId);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_NAME));
            cursor.close();
            return name;
        }
        return "";
    }

    private void updateTotalAmount() {
        double totalAmount = 0;
        for (CartItem item : cartItems) {
            totalAmount += item.getSubtotal();
        }
        totalAmountTextView.setText(String.format("Total: Rs.%.2f", totalAmount));
    }

    @Override
    public void onRemoveItem(int productId, int position) {
        dbHelper.deleteCartItemByProductId(productId);
        cartItems.remove(position);
        cartAdapter.notifyDataSetChanged();
        updateTotalAmount();
        Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show();
    }

    private int getCurrentUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("UserID", -1);
        Log.d("CartActivity", "Current User ID: " + userId); // Add this log
        return userId;
    }



}
