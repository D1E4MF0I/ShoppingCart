package com.dreamfor.shoppingcart.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.dreamfor.shoppingcart.dao.ProductDao;
import com.dreamfor.shoppingcart.dao.UserDao;
import com.dreamfor.shoppingcart.database.DatabaseHelper;
import com.dreamfor.shoppingcart.domain.ProductQuantity;
import com.dreamfor.shoppingcart.domain.User;

import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    private ProductDao productDao;

    private DatabaseHelper databaseHelper;

    public UserDaoImpl(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        productDao = new ProductDaoImpl(databaseHelper);
    }

    // 更新用户选择的商品
    @Override
    public void updateSelectedProducts(int userId, int productId, int quantity) {
        productDao.setProductFromUser(userId, productId, quantity);
    }

    // 获取用户选择的商品
    @Override
    public List<ProductQuantity> getSelectedProducts(int userId) {
        return productDao.getUserProducts(userId);
    }

    @Override
    public User queryUserByID(int userId) {
        User user = null;

        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_USERS, new String[]{DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_PASSWORD}, DatabaseHelper.COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);
        if(cursor.moveToFirst()){
            int userIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME);
            int passIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD);

            if(userIndex >= 0 && passIndex >= 0){
                user = new User();
                user.setUser_id(String.valueOf(userId));
                user.setUsername(cursor.getString(userIndex));
                user.setPassword(cursor.getString(passIndex));
                List<ProductQuantity> productQuantityList = productDao.getUserProducts(userId);
                user.setSelected_products(productQuantityList);
            }
        }
        return user;
    }

    @Override
    public long insertUser(User user) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
        values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());
        long id = db.insert(DatabaseHelper.TABLE_USERS, null,values);
        db.close();
        return id;
    }
}
