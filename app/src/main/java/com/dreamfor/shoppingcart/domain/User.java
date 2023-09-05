package com.dreamfor.shoppingcart.domain;

import java.util.List;

public class User {
    private String user_id;

    private String username;
    private String password;
    private List<ProductQuantity> selected_products;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ProductQuantity> getSelected_products() {
        return selected_products;
    }

    public void setSelected_products(List<ProductQuantity> selected_products) {
        this.selected_products = selected_products;
    }
}
