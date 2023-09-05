package com.dreamfor.shoppingcart.dao;

import com.dreamfor.shoppingcart.domain.ProductQuantity;

import java.util.List;

public interface ProductDao {
    // 查询用户添加的商品以及对应数量
    List<ProductQuantity> getUserProducts(int userId);

    // 设定用户购买商品的数量 返回影响数量
    public long setProductFromUser(int userId, int productId, int quantity);

    // TODO:根据ProductID查询具体Product信息

}
