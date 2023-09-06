package com.dreamfor.shoppingcart.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dreamfor.shoppingcart.dao.ProductDao;
import com.dreamfor.shoppingcart.database.DatabaseHelper;
import com.dreamfor.shoppingcart.domain.Product;
import com.dreamfor.shoppingcart.domain.ProductQuantity;

import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements ProductDao {
    private DatabaseHelper databaseHelper;

    public ProductDaoImpl(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public List<ProductQuantity> getUserProductQuantities(int userId) {
        List<ProductQuantity> productQuantities = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String[] columns = {"user_products.product_id", "user_products.product_name", "user_products.quantity"};

        // 查询 user_products 表
        String selection = "user_products.user_id = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(DatabaseHelper.TABLE_USER_PRODUCTS, columns, selection, selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            int productId = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
            String productName = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

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
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(productId)};

        Cursor cursor = db.query("user_products", null, selection, selectionArgs, null, null, null);

        long rowsAffected;

        if (cursor.moveToFirst()) {
            // 已存在购买记录
            ContentValues updateValues = new ContentValues();
            updateValues.put("quantity", quantity);

            if (quantity <= 0) {
                // 数量为 0，删除记录
                rowsAffected = db.delete("user_products", selection, selectionArgs);
            } else {
                // 更新数量
                rowsAffected = db.update("user_products", updateValues, selection, selectionArgs);
            }
        } else {
            // 不存在购买记录，插入新记录
            ContentValues userProductValues = new ContentValues();
            userProductValues.put("user_id", userId);
            userProductValues.put("product_id", productId);
            userProductValues.put("quantity", quantity); // 设置初始数量

            rowsAffected = db.insert("user_products", null, userProductValues);
        }

        cursor.close();
        db.close();

        return rowsAffected;
    }


    @Override
    public Product getProductById(int productId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] columns = {
                DatabaseHelper.COLUMN_PRODUCT_ID,
                DatabaseHelper.COLUMN_PRODUCT_NAME,
                DatabaseHelper.COLUMN_PRICE,
                DatabaseHelper.COLUMN_PRODUCT_TEXT
        };
        String selection = DatabaseHelper.COLUMN_PRODUCT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(productId)};
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PRODUCTS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Product product = null;
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME);
            int priceIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE);
            int textIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_TEXT);

            String productName = cursor.getString(nameIndex);
            Double price = cursor.getDouble(priceIndex);
            String productText = cursor.getString(textIndex);

            product = new Product(productName, price);
            product.setProduct_id(String.valueOf(productId));
            product.setProduct_text(productText);
        }

        cursor.close();
        return product;
    }

    @Override
    public List<Product> getProductsByNameFromDB(String productName) {
        List<Product> productList = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_NAME + " LIKE '%" + productName + "%'";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID);
                int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME);
                int priceIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRICE);
                int textIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_TEXT);


                product.setProduct_id(cursor.getString(idIndex));
                product.setProduct_name(cursor.getString(nameIndex));
                product.setPrice(cursor.getDouble(priceIndex));
                product.setProduct_text(cursor.getString(textIndex));

                productList.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return productList;
    }

    @Override
    public List<Product> getProductsByProductQuantities(List<ProductQuantity> productQuantityList) {
        List<Product> productList = new ArrayList<>();
        for (ProductQuantity productQuantity : productQuantityList) {
            productList.add(getProductById(productQuantity.getProductId()));
        }
        return productList;
    }
}
