package com.example.dogapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class UserOrdersAdapter extends ArrayAdapter<Order> {

    public UserOrdersAdapter(Context context, List<Order> orders) {
        super(context, 0, orders);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Order order = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_item_user, parent, false);
        }

        TextView orderIdTextView = convertView.findViewById(R.id.orderIdTextView);
        TextView userIdTextView = convertView.findViewById(R.id.userIdTextView);
        TextView statusTextView = convertView.findViewById(R.id.statusTextView);
        TextView totalTextView = convertView.findViewById(R.id.totalTextView);

        orderIdTextView.setText("Order ID: " + order.getOrderId());
        userIdTextView.setText("User ID: " + order.getUserId());
        statusTextView.setText("Status: " + order.getStatus());
        totalTextView.setText("Total: Rs. " + order.getTotal());

        // No buttons for changing status or deleting orders

        return convertView;
    }
}
