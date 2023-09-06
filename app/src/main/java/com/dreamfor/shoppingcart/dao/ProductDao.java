package com.dreamfor.shoppingcart.dao;

import com.dreamfor.shoppingcart.domain.Product;
import com.dreamfor.shoppingcart.domain.ProductItem;
import com.dreamfor.shoppingcart.domain.ProductQuantity;

import java.util.List;

public interface ProductDao {
    // 查询用户添加的商品以及对应数量
    List<ProductQuantity> getUserProductQuantities(int userId);

    // 设定用户购买商品的数量 返回影响数量
    long setProductFromUser(int userId, int productId, int quantity);

    // 根据物品ID获取物品所有数据
    Product getProductById(int productId);

    // 根据物品名称匹配获得相应物品响应
    List<Product> getProductsByNameFromDB(String productName);

    // 根据ProductQuantity列表获取物品信息列表
    List<Product> getProductsByProductQuantities(List<ProductQuantity> productQuantityList);

    // 获取所有物品
    List<Product> getAllProducts();

    // 更新物品所有信息
    int updateProduct(Product product);

    // 根据用户ID和物品ID获取ProductQuantity
    ProductQuantity getProductQuantityById(int userId, int productId);

    // 添加商品
    long insertProduct(Product product);

    // 通过商品名称删除商品
    int deleteProductByName(String productName);

    // 通过商品名称查询是否有此商品
    boolean isProductExists(String productName);
}
