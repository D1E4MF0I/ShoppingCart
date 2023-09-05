package com.dreamfor.shoppingcart.service.impl;

import com.dreamfor.shoppingcart.dao.ProductDao;
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
        return productDao.getUserProducts(userId);
    }

    @Override
    public boolean setProductAndSyncUser(int userId, int productId, int quantity) {
        long updates = productDao.setProductFromUser(userId, productId, quantity);
        if(updates != -1){
            return true;
        }else{
            return false;
        }
    }
}
