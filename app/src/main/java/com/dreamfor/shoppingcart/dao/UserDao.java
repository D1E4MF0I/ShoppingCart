package com.dreamfor.shoppingcart.dao;

import com.dreamfor.shoppingcart.domain.ProductQuantity;
import com.dreamfor.shoppingcart.domain.User;

import java.util.List;

public interface UserDao {
    // 更新用户选择的商品
    public long updateSelectedProducts(int userId, int productId, int quantity);

    // 获取用户选择的商品
    public List<ProductQuantity> getUserProductQuantities(int userId);


    // 根据用户ID获取用户所有数据
    public User queryUserByID(int userId);

    // 增加用户
    public long insertUser(User user);


    // 判断账号是否存在
    boolean isUserExists(String username);

    // 获取所有用户数据
    List<User> getAllUsers();
}
