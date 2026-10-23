package com.example.fruitshop.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Bảng Fruit(id, name, category, price, quantity, expiryDate, description)
public class Fruit {
    // expiryDate lưu dạng yyyy-MM-dd để so sánh chuỗi = so sánh ngày
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private int id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private String expiryDate;
    private String description;

    public Fruit() {
    }

    public Fruit(int id, String name, String category, double price, int quantity,
                 String expiryDate, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Hết hạn khi ngày hết hạn < hôm nay (hạn đúng hôm nay vẫn bán được)
    public boolean isExpired() {
        String today = new SimpleDateFormat(DATE_FORMAT, Locale.US).format(new Date());
        return expiryDate.compareTo(today) < 0;
    }
}
