package com.dreamfor.shoppingcart.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dreamfor.shoppingcart.dao.ProductDao;
import com.dreamfor.shoppingcart.database.DatabaseHelper;
import com.dreamfor.shoppingcart.domain.Product;
import com.dreamfor.shoppingcart.domain.ProductItem;
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

        Product product = getProductById(productId);

        // 步骤 2：在 user_products 表中查找对应的购买记录
        String selection = DatabaseHelper.COLUMN_USER_ID_FK + " = ? AND " + DatabaseHelper.COLUMN_PRODUCT_ID_FK + " = ?";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(productId)};

        Cursor cursor = db.query(DatabaseHelper.TABLE_USER_PRODUCTS, null, selection, selectionArgs, null, null, null);

        long rowsAffected;

        if (cursor.moveToFirst()) {
            // 已存在购买记录
            ContentValues updateValues = new ContentValues();
            updateValues.put(DatabaseHelper.COLUMN_QUANTITY, quantity);
            updateValues.put(DatabaseHelper.COLUMN_PRODUCT_NAME_FK, product.getProduct_name());

            if (quantity <= 0) {
                // 数量为 0，删除记录
                rowsAffected = db.delete(DatabaseHelper.TABLE_USER_PRODUCTS, selection, selectionArgs);
            } else {
                // 更新数量
                rowsAffected = db.update(DatabaseHelper.TABLE_USER_PRODUCTS, updateValues, selection, selectionArgs);
            }
        } else {
            // 不存在购买记录，插入新记录
            ContentValues userProductValues = new ContentValues();
            userProductValues.put(DatabaseHelper.COLUMN_USER_ID_FK, userId);
            userProductValues.put(DatabaseHelper.COLUMN_PRODUCT_ID_FK, productId);
            userProductValues.put(DatabaseHelper.COLUMN_QUANTITY, quantity); // 设置初始数量
            userProductValues.put(DatabaseHelper.COLUMN_PRODUCT_NAME_FK, product.getProduct_name());

            rowsAffected = db.insert(DatabaseHelper.TABLE_USER_PRODUCTS, null, userProductValues);
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
            product.setProduct_id(productId);
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


                product.setProduct_id(cursor.getInt(idIndex));
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

    @Override
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID);
                int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME);
                int priceIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRICE);
                int textIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_TEXT);


                int productId = cursor.getInt(idIndex);
                String productName = cursor.getString(nameIndex);
                double price = cursor.getDouble(priceIndex);
                String productText = cursor.getString(textIndex);

                Product product = new Product(productName, price);
                product.setProduct_id(productId);
                product.setProduct_text(productText);

                productList.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return productList;
    }

    @Override
    public int updateProduct(Product product) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PRODUCT_ID, product.getProduct_id());
        values.put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.getProduct_name());
        values.put(DatabaseHelper.COLUMN_PRICE, product.getPrice());
        values.put(DatabaseHelper.COLUMN_PRODUCT_TEXT, product.getProduct_text());

        String whereClause = DatabaseHelper.COLUMN_PRODUCT_ID + " = ?";
        String[] whereArgs = {String.valueOf(product.getProduct_id())};

        int numRowsUpdated = db.update(DatabaseHelper.TABLE_PRODUCTS, values, whereClause, whereArgs);

        db.close();

        return numRowsUpdated;
    }

    @Override
    public ProductQuantity getProductQuantityById(int userId, int productId) {
        ProductQuantity productQuantity = null;

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_ID_FK, userId);
        values.put(DatabaseHelper.COLUMN_PRODUCT_ID_FK, productId);

        String[] columns = {DatabaseHelper.COLUMN_QUANTITY};
        String selection = DatabaseHelper.COLUMN_USER_ID_FK + " = ? AND " + DatabaseHelper.COLUMN_PRODUCT_ID_FK + " = ? ";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(productId)};
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER_PRODUCTS, columns, selection, selectionArgs, null, null, null);

        if(cursor.moveToFirst()){
            int qIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY);
            int quantity = cursor.getInt(qIndex);
            productQuantity = new ProductQuantity(productId, userId, quantity);
        } else{
            productQuantity = new ProductQuantity(productId, userId, 0);
        }

        // 获取名字（总感觉会在很多地方出问题。。。
        Product product = getProductById(productId);
        if(product != null)
            productQuantity.setProductName(product.getProduct_name());

        cursor.close();
        db.close();
        return productQuantity;
    }

    @Override
    public long insertProduct(Product product) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.getProduct_name());
        values.put(DatabaseHelper.COLUMN_PRICE, product.getPrice());
        values.put(DatabaseHelper.COLUMN_PRODUCT_TEXT, product.getProduct_text());

        long id = db.insertOrThrow(DatabaseHelper.TABLE_PRODUCTS, null, values);
        db.close();
        return id;
    }

    @Override
    public int deleteProductByName(String productName) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // 删除商品表中商品
        String whereClause = DatabaseHelper.COLUMN_PRODUCT_NAME + " = ?";
        String[] whereArgs = {productName};
        int delete = db.delete(DatabaseHelper.TABLE_PRODUCTS, whereClause, whereArgs);

        // 删除中间表商品
        whereClause = DatabaseHelper.COLUMN_PRODUCT_NAME_FK + " = ?";
        int delete2 = db.delete(DatabaseHelper.TABLE_USER_PRODUCTS, whereClause, whereArgs);
        return Math.min(delete, delete2);
    }

    @Override
    public boolean isProductExists(String productName) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = databaseHelper.getReadableDatabase();

            // 查询是否存在相同名称的商品
            String selection = DatabaseHelper.COLUMN_PRODUCT_NAME + " = ?";
            String[] selectionArgs = {productName};
            cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, null, selection, selectionArgs, null, null, null);

            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }
}
