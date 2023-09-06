package com.dreamfor.shoppingcart.service;

import com.dreamfor.shoppingcart.domain.ProductQuantity;
import com.dreamfor.shoppingcart.domain.User;

import java.util.List;

public interface UserService {
    // 增加用户
    boolean insertUser(User user);

    // 根据用户ID查询用户信息
    User queryUserByID(int id);

    // 更新用户选择的商品
    boolean updateSelectedProducts(int userId, int productId, int quantity);

    // 获取用户选择的商品
    List<ProductQuantity> getUserProductQuantities(int userId);
}
