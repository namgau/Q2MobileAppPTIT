package com.example.fruitshop.models;

// Bảng Order(id, customerId, orderDate, totalAmount, status)
public class Order {
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_CONFIRMED = "Confirmed";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String[] STATUSES = {STATUS_PENDING, STATUS_CONFIRMED, STATUS_COMPLETED};

    private int id;
    private int customerId;
    private String orderDate;
    private double totalAmount;
    private String status;

    public Order() {
    }

    public Order(int id, int customerId, String orderDate, double totalAmount, String status) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
