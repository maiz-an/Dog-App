package com.example.dogapp;

public class CartItem {
    private int productId;
    private String productName;
    private double productPrice;
    private int quantity;
    private double subtotal;

    public CartItem(int productId, String productName, double productPrice, int quantity, double subtotal) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }
}
