package com.dreamfor.shoppingcart.service.impl;

import com.dreamfor.shoppingcart.dao.ProductDao;
import com.dreamfor.shoppingcart.domain.Product;
import com.dreamfor.shoppingcart.domain.ProductQuantity;
import com.dreamfor.shoppingcart.service.ProductService;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    private ProductDao productDao;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public List<ProductQuantity> getUserProducts(int userId) {
        return productDao.getUserProductQuantities(userId);
    }

    @Override
    public boolean setProductAndSyncUser(int userId, int productId, int quantity) {
        long updates = productDao.setProductFromUser(userId, productId, quantity);
        return updates > 0;
    }

    @Override
    public Product getProduct(int productId) {
        return productDao.getProductById(productId);
    }

    @Override
    public List<Product> getProductsByNameFromDB(String productName) {
        return productDao.getProductsByNameFromDB(productName);
    }

    @Override
    public List<Product> getProductsByProductQuantities(List<ProductQuantity> productQuantityList) {
        return productDao.getProductsByProductQuantities(productQuantityList);
    }
}
