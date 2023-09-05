package com.dreamfor.shoppingcart.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dreamfor.shoppingcart.dao.ProductDao;
import com.dreamfor.shoppingcart.database.DatabaseHelper;
import com.dreamfor.shoppingcart.domain.ProductQuantity;

import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements ProductDao {
    private DatabaseHelper databaseHelper;

    public ProductDaoImpl(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public List<ProductQuantity> getUserProducts(int userId) {
        List<ProductQuantity> productQuantities = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String[] columns = { "products.product_id", "products.product_name", "user_products.quantity" };

        // 查询 user_products 表
        String selection = "user_products.user_id = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        Cursor cursor = db.query(DatabaseHelper.TABLE_USER_PRODUCTS, columns, selection, selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            int productId = cursor.getInt(cursor.getColumnIndexOrThrow("products.product_id"));
            String productName = cursor.getString(cursor.getColumnIndexOrThrow("products.product_name"));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("user_products.quantity"));

            ProductQuantity productQuantity = new ProductQuantity(productId, productName, quantity);
            productQuantity.setUserId(userId);
            productQuantities.add(productQuantity);
        }

        cursor.close();
        db.close();

        return productQuantities;
    }

    @Override
    public long setProductFromUser(int userId, int productId, int quantity) {
        // 步骤 1：在 products 表中插入商品
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // 步骤 2：在 user_products 表中查找对应的购买记录
        String selection = "user_id = ? AND product_id = ?";
        String[] selectionArgs = { String.valueOf(userId), String.valueOf(productId) };

        Cursor cursor = db.query("user_products", null, selection, selectionArgs, null, null, null);

        long flag = 0;

        if (cursor.moveToFirst()) {
            // 已存在购买记录，更新数量
            ContentValues updateValues = new ContentValues();
            updateValues.put("quantity", quantity);

            flag = db.update("user_products", updateValues, selection, selectionArgs);
            flag --;
        } else {
            // 不存在购买记录，插入新记录
            ContentValues userProductValues = new ContentValues();
            userProductValues.put("user_id", userId);
            userProductValues.put("product_id", productId);
            userProductValues.put("quantity", quantity); // 设置初始数量

            flag = db.insert("user_products", null, userProductValues);
        }



        cursor.close();
        db.close();

        return flag;
    }


}
