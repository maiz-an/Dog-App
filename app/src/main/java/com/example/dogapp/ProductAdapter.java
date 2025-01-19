package com.example.dogapp;

import android.content.Context;
import android.content.ContentValues;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Product> productList;
    private DatabaseHelper dbHelper;

    public ProductAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.dbHelper = new DatabaseHelper(context); // Initialize the DatabaseHelper
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        }

        Product product = productList.get(position);

        //  these IDs match those in  product_item.xml but showing error idk why but still working so who cares lol!!  bowW bowW!!
        TextView nameTextView = convertView.findViewById(R.id.productName);
        TextView priceTextView = convertView.findViewById(R.id.productPrice);
        ImageView productImageView = convertView.findViewById(R.id.productImage);
        EditText quantityEditText = convertView.findViewById(R.id.productQuantity);
        Button addToCartButton = convertView.findViewById(R.id.addToCartButton);

        nameTextView.setText(product.getName());
        priceTextView.setText(String.format("Rs.%.2f", product.getPrice()));

        if (product.getImagePath() != null) {
            productImageView.setImageURI(Uri.fromFile(new File(product.getImagePath())));
        } else {
            productImageView.setImageResource(android.R.drawable.ic_menu_report_image); // Built-in placeholder icon
        }

        addToCartButton.setOnClickListener(v -> {
            String quantityText = quantityEditText.getText().toString().trim();
            if (quantityText.isEmpty()) {
                Toast.makeText(context, "Please enter a quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                Toast.makeText(context, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add to cart in the database
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.COL_CART_PRODUCT_ID, product.getId());
            contentValues.put(DatabaseHelper.COL_CART_QUANTITY, quantity);

            long result = dbHelper.getWritableDatabase().insert(DatabaseHelper.TABLE_CART, null, contentValues);

            if (result != -1) {
                Toast.makeText(context, "Product added to cart successfully!", Toast.LENGTH_SHORT).show();

                // Clear the quantity field
                quantityEditText.setText("");

                // Optionally clear other fields if necessary (e.g., reset other views or selections)
            } else {
                Toast.makeText(context, "Failed to add product to cart", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
