package com.dreamfor.shoppingcart.domain;

import java.text.DecimalFormat;

public class Product {
    private int product_id;
    private String product_name;
    private Double price;
    private String product_text;

    public Product() {
    }

    public Product(String product_name, Double price) {
        this.product_name = product_name;

        // 格式化存储
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String formattedPrice = decimalFormat.format(price);

        this.price = Double.valueOf(formattedPrice);
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        // 格式化
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String formattedPrice = decimalFormat.format(price);

        this.price = Double.valueOf(formattedPrice);
    }

    public String getProduct_text() {
        return product_text;
    }

    public void setProduct_text(String product_text) {
        this.product_text = product_text;
    }
}
