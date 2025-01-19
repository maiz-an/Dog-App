package com.example.dogapp;

public class Order {
    private int orderId;
    private int userId;
    private String status;
    private double total;

    public Order(int orderId, int userId, String status, double total) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.total = total;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public double getTotal() {
        return total;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
