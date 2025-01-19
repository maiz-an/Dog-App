package com.example.dogapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public  class ProductsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "ProductsActivity";

    private EditText productNameEditText, productDescriptionEditText, productPriceEditText, searchProductEditText;
    private Spinner productCategorySpinner, editProductCategorySpinner;
    private Button addProductButton, searchProductButton, selectImageButton, saveChangesBtn, deleteProductBtn;
    private ListView productListView;
    private ImageView productImageView, editProductImageView;
    private DatabaseHelper dbHelper;
    private ArrayList<String> productList;
    private ArrayAdapter<String> adapter;
    private Bitmap productImage;
    private int selectedProductId = -1;
    private LinearLayout editDeleteSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        // Initialize UI components and DatabaseHelper
        productNameEditText = findViewById(R.id.productNameEditText);
        productDescriptionEditText = findViewById(R.id.productDescriptionEditText);
        productPriceEditText = findViewById(R.id.productPriceEditText);
        productCategorySpinner = findViewById(R.id.productCategorySpinner);
        addProductButton = findViewById(R.id.addProductButton);
        searchProductEditText = findViewById(R.id.searchProductEditText);
        searchProductButton = findViewById(R.id.searchProductButton);
        productListView = findViewById(R.id.productListView);
        selectImageButton = findViewById(R.id.selectImageButton);
        productImageView = findViewById(R.id.productImageView);
        saveChangesBtn = findViewById(R.id.saveChangesBtn);
        deleteProductBtn = findViewById(R.id.deleteProductBtn);
        editDeleteSection = findViewById(R.id.editDeleteSection);
        editProductCategorySpinner = findViewById(R.id.editProductCategorySpinner);
        editProductImageView = findViewById(R.id.editProductImageView);

        dbHelper = new DatabaseHelper(this);

        // Setup product categories
        String[] categories = {"Food", "Toys", "Medicine"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productCategorySpinner.setAdapter(categoryAdapter);
        editProductCategorySpinner.setAdapter(categoryAdapter);

        // Setup product list adapter
        productList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        productListView.setAdapter(adapter);

        loadProducts();

        addProductButton.setOnClickListener(v -> addProduct());
        searchProductButton.setOnClickListener(v -> searchProducts(searchProductEditText.getText().toString().trim()));
        selectImageButton.setOnClickListener(v -> openImageSelector());

        productListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProduct = productList.get(position);
            selectedProductId = Integer.parseInt(selectedProduct.split(":")[0]); // Assume ID is part of the list item
            loadSelectedProductDetails(selectedProductId);
        });

        saveChangesBtn.setOnClickListener(v -> {
            if (selectedProductId != -1) {
                saveProductDetails();
            }
        });

        deleteProductBtn.setOnClickListener(v -> {
            if (selectedProductId != -1) {
                deleteProduct(selectedProductId);
            }
        });

    }

    private void loadProducts() {
        Cursor cursor = dbHelper.getAllProducts();
        productList.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_NAME));
                productList.add(id + ": " + name);
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void searchProducts(String query) {
        Cursor cursor = dbHelper.searchProductsByName(query);
        productList.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_NAME));
                productList.add(id + ": " + name);
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void loadSelectedProductDetails(int productId) {
        Cursor cursor = dbHelper.getProductById(productId);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_DESCRIPTION));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PRICE));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_CATEGORY));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PICTURE));

            // Populate the edit section with the selected product's details
            EditText editProductName = findViewById(R.id.editProductName);
            EditText editProductDescription = findViewById(R.id.editProductDescription);
            EditText editProductPrice = findViewById(R.id.editProductPrice);
            editProductPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


            editProductName.setText(name);
            editProductDescription.setText(description);
            editProductPrice.setText(String.valueOf(price));
            editProductCategorySpinner.setSelection(((ArrayAdapter<String>)editProductCategorySpinner.getAdapter()).getPosition(category));

            if (imagePath != null) {
                editProductImageView.setImageURI(Uri.fromFile(new File(imagePath)));
            }

            editDeleteSection.setVisibility(View.VISIBLE);
            cursor.close();
        }
    }

    private void saveProductDetails() {
        EditText editProductName = findViewById(R.id.editProductName);
        EditText editProductDescription = findViewById(R.id.editProductDescription);
        EditText editProductPrice = findViewById(R.id.editProductPrice);

        String name = editProductName.getText().toString().trim();
        String description = editProductDescription.getText().toString().trim();
        double price = Double.parseDouble(editProductPrice.getText().toString().trim());
        String category = editProductCategorySpinner.getSelectedItem().toString();

        String imagePath = saveImageToStorage(productImage);
        if (imagePath == null && selectedProductId != -1) {
            Cursor cursor = dbHelper.getProductById(selectedProductId);
            if (cursor != null && cursor.moveToFirst()) {
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PICTURE));
                cursor.close();
            }
        }

        int rowsAffected = dbHelper.updateProduct(selectedProductId, name, description, price, category, imagePath);

        if (rowsAffected > 0) {
            loadProducts(); // Refresh the product list after saving
            editDeleteSection.setVisibility(View.GONE);
            selectedProductId = -1;
            Toast.makeText(ProductsActivity.this, "Product details updated successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ProductsActivity.this, "Error updating product details. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProduct(int productId) {
        int rowsAffected = dbHelper.deleteProductById(productId);

        if (rowsAffected > 0) {
            loadProducts(); // Refresh the product list after deletion
            editDeleteSection.setVisibility(View.GONE);
            selectedProductId = -1;
            Toast.makeText(ProductsActivity.this, "Product deleted successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ProductsActivity.this, "Error deleting product. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        productNameEditText.setText("");
        productDescriptionEditText.setText("");
        productPriceEditText.setText("");
        productCategorySpinner.setSelection(0);
        productImageView.setImageResource(android.R.color.transparent);
        productImage = null;
    }


    private void addProduct() {
        String name = productNameEditText.getText().toString().trim();
        String description = productDescriptionEditText.getText().toString().trim();
        double price = Double.parseDouble(productPriceEditText.getText().toString().trim());
        String category = productCategorySpinner.getSelectedItem().toString();

        if (name.isEmpty() || description.isEmpty() || price <= 0 || productImage == null) {
            Toast.makeText(this, "Please fill out all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        String imagePath = saveImageToStorage(productImage);
        if (imagePath == null) {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COL_PRODUCT_NAME, name);
        contentValues.put(DatabaseHelper.COL_PRODUCT_DESCRIPTION, description);
        contentValues.put(DatabaseHelper.COL_PRODUCT_PRICE, price);
        contentValues.put(DatabaseHelper.COL_PRODUCT_CATEGORY, category);
        contentValues.put(DatabaseHelper.COL_PRODUCT_PICTURE, imagePath);

        long result = dbHelper.addProduct(name, description, price, category, imagePath);

        if (result != -1) {
            Toast.makeText(this, "Product added successfully!", Toast.LENGTH_SHORT).show();
            loadProducts();
            clearForm();
        } else {
            Log.e(TAG, "Failed to add product");
            Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImageToStorage(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "Bitmap is null, cannot save image.");
            return null;
        }

        File directory = getApplicationContext().getDir("imageDir", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            boolean dirCreated = directory.mkdir();
            if (!dirCreated) {
                Log.e(TAG, "Failed to create directory for image storage.");
                return null;
            }
        }

        File imageFile = new File(directory, System.currentTimeMillis() + ".jpg");

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            Log.d(TAG, "Image saved successfully: " + imageFile.getAbsolutePath());
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Failed to save image: " + e.getMessage(), e);
            return null;
        }
    }

    private void openImageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                productImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                productImageView.setImageBitmap(productImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
