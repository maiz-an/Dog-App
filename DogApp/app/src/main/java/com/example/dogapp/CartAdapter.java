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

public class CartAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CartItem> cartItems;
    private OnCartItemInteractionListener listener;

    public CartAdapter(Context context, ArrayList<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        if (context instanceof OnCartItemInteractionListener) {
            this.listener = (OnCartItemInteractionListener) context;
        }
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cartItems.get(position).getProductId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        }

        CartItem cartItem = cartItems.get(position);

        TextView nameTextView = convertView.findViewById(R.id.cartProductNameTextView);
        TextView priceTextView = convertView.findViewById(R.id.cartProductPriceTextView);
        TextView quantityTextView = convertView.findViewById(R.id.cartProductQuantityTextView);
        TextView subtotalTextView = convertView.findViewById(R.id.cartProductSubtotalTextView);
        Button removeButton = convertView.findViewById(R.id.removeFromCartButton);

        nameTextView.setText(cartItem.getProductName());
        priceTextView.setText(String.format("Rs.%.2f", cartItem.getProductPrice()));
        quantityTextView.setText(String.valueOf(cartItem.getQuantity()));
        subtotalTextView.setText(String.format("Rs.%.2f", cartItem.getSubtotal()));

        removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(cartItem.getProductId(), position);
            }
        });

        return convertView;
    }

    public interface OnCartItemInteractionListener {
        void onRemoveItem(int productId, int position);
    }
}
