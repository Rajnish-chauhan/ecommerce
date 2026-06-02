package com.project.ecommerce.model;

import java.util.Map;

public class OrderRequest {

    // key-product id (String in Mongo)
    // value - quantity
    private Map<String, Integer> productQuantities;
    private double totalAmount;

    public Map<String, Integer> getProductQuantities() { return productQuantities; }
    public void setProductQuantities(Map<String, Integer> productQuantities) { this.productQuantities = productQuantities; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}