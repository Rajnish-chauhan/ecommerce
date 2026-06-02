package com.project.ecommerce.model;

public class OrderItem {

    // Removed ID and Order reference to avoid infinite loops in MongoDB
    private Product product;
    private int quantity;

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}