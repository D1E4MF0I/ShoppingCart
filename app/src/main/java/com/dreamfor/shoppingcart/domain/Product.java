package com.dreamfor.shoppingcart.domain;

public class Product {
    private String product_id;
    private String product_name;
    private String price;
    private String product_text;

    public Product() {
    }

    public Product(String product_name, String price) {
        this.product_name = product_name;
        this.price = price;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProduct_text() {
        return product_text;
    }

    public void setProduct_text(String product_text) {
        this.product_text = product_text;
    }
}
