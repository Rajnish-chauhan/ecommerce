package com.project.ecommerce.model;

import java.util.Map;

public class OrderRequest {
    private Map<String, Integer> productQuantities;
    private double totalAmount;
    private String razorpay_payment_id;
    private String razorpay_order_id;
    private String razorpay_signature;

    public Map<String, Integer> getProductQuantities() { return productQuantities; }
    public void setProductQuantities(Map<String, Integer> productQuantities) { this.productQuantities = productQuantities; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getRazorpay_payment_id() { return razorpay_payment_id; }
    public void setRazorpay_payment_id(String razorpay_payment_id) { this.razorpay_payment_id = razorpay_payment_id; }
    public String getRazorpay_order_id() { return razorpay_order_id; }
    public void setRazorpay_order_id(String razorpay_order_id) { this.razorpay_order_id = razorpay_order_id; }
    public String getRazorpay_signature() { return razorpay_signature; }
    public void setRazorpay_signature(String razorpay_signature) { this.razorpay_signature = razorpay_signature; }
}