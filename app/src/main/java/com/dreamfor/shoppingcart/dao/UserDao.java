package com.dreamfor.shoppingcart.dao;

import com.dreamfor.shoppingcart.domain.User;

import java.util.List;

public interface UserDao {
    // 更新用户选择的商品
    public void updateSelectedProducts(int userId, List<Integer> selectedProducts);

    // 获取用户选择的商品
    public List<Integer> getSelectedProducts(int userId);


    // 显示用户选择的商品
    public User queryUserByID(int userId);

    // 增加用户
    public long insertUser(User user);
}
