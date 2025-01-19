package com.example.dogapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CatalogActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private GridView productGridView;
    private EditText searchProductEditText;
    private Spinner categorySpinner;
    private ArrayList<Product> productList;
    private ProductAdapter adapter;  // Custom adapter for product grid
    private String selectedCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        dbHelper = new DatabaseHelper(this);
        productGridView = findViewById(R.id.productGridView);
        searchProductEditText = findViewById(R.id.searchProductEditText);
        Button searchProductButton = findViewById(R.id.searchProductButton);
        categorySpinner = findViewById(R.id.categorySpinner);

        setupCategorySpinner();
        loadProducts();

        searchProductButton.setOnClickListener(v -> searchProducts(searchProductEditText.getText().toString().trim()));

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
                loadProducts();  // Reload products based on selected category
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "All";
            }
        });

        productGridView.setOnItemClickListener((parent, view, position, id) -> {
            Product selectedProduct = productList.get(position);
            // Handle adding to cart
            addToCart(selectedProduct.getId(), 1);  // Assume default quantity is 1 for now
        });




    }

    private void setupCategorySpinner() {
        String[] categories = {"All", "Food", "Toys", "Medicine"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void loadProducts() {
        productList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllProducts();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_DESCRIPTION));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PRICE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_CATEGORY));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PICTURE));

                // Filter by selected category
                if (selectedCategory.equals("All") || selectedCategory.equals(category)) {
                    productList.add(new Product(id, name, description, price, category, imagePath));
                }
            }
            cursor.close();
        }

        adapter = new ProductAdapter(this, productList);
        productGridView.setAdapter(adapter);
    }

    private void searchProducts(String query) {
        productList.clear();
        Cursor cursor = dbHelper.searchProductsByName(query);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_DESCRIPTION));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PRICE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_CATEGORY));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PICTURE));

                // Filter by selected category
                if (selectedCategory.equals("All") || selectedCategory.equals(category)) {
                    productList.add(new Product(id, name, description, price, category, imagePath));
                }
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void addToCart(int productId, int quantity) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_CART_PRODUCT_ID, productId);
        values.put(DatabaseHelper.COL_CART_QUANTITY, quantity);

        long result = dbHelper.getWritableDatabase().insert(DatabaseHelper.TABLE_CART, null, values);

        if (result != -1) {
            Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
        }
    }

}
