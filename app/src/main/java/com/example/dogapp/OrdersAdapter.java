package com.example.dogapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class OrdersAdapter extends ArrayAdapter<Order> {

    public OrdersAdapter(Context context, List<Order> orders) {
        super(context, 0, orders);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Order order = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_item, parent, false);
        }

        TextView orderIdTextView = convertView.findViewById(R.id.orderIdTextView);
        TextView userIdTextView = convertView.findViewById(R.id.userIdTextView);
        TextView statusTextView = convertView.findViewById(R.id.statusTextView);
        TextView totalTextView = convertView.findViewById(R.id.totalTextView);
        Button changeStatusButton = convertView.findViewById(R.id.changeStatusButton);
        Button deleteOrderButton = convertView.findViewById(R.id.deleteOrderButton);

        orderIdTextView.setText("Order ID: " + order.getOrderId());
        userIdTextView.setText("User ID: " + order.getUserId());
        statusTextView.setText("Status: " + order.getStatus());
        totalTextView.setText("Total: Rs. " + order.getTotal());

        // Order status change logic for admin
        changeStatusButton.setOnClickListener(v -> {
            // Cycle through the order statuses
            switch (order.getStatus()) {
                case "pending":
                    order.setStatus("processing");
                    break;
                case "processing":
                    order.setStatus("shipped");
                    break;
                case "shipped":
                    order.setStatus("out for delivery");
                    break;
                case "out for delivery":
                    order.setStatus("delivered");
                    break;
                case "delivered":
                    order.setStatus("completed");
                    break;
                case "completed":
                    order.setStatus("pending"); // Loop back to pending if needed
                    break;
                default:
                    order.setStatus("pending"); // Default to pending if status is unknown
                    break;
            }

            // Update the status in the database
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            dbHelper.updateOrderStatus(order.getOrderId(), order.getStatus());

            // Notify the adapter to refresh the ListView
            notifyDataSetChanged();
        });

        // Delete order logic for admin
        deleteOrderButton.setOnClickListener(v -> {
            // Delete the order from the database
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            dbHelper.deleteOrderById(order.getOrderId());

            // Remove the order from the list
            remove(order);

            // Notify the adapter to refresh the ListView
            notifyDataSetChanged();

            // Show a confirmation message
            Toast.makeText(getContext(), "Order deleted", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
