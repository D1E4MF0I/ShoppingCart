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
    public long updateSelectedProducts(int userId, int productId, int quantity) {
        return productDao.setProductFromUser(userId, productId, quantity);
    }

    // 获取用户选择的商品
    @Override
    public List<ProductQuantity> getUserProductQuantities(int userId) {
        return productDao.getUserProductQuantities(userId);
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
                user.setUser_id(userId);
                user.setUsername(cursor.getString(userIndex));
                user.setPassword(cursor.getString(passIndex));
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

    @Override
    public boolean isUserExists(String username) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = databaseHelper.getReadableDatabase();

            // 查询是否存在相同的用户名
            String selection = DatabaseHelper.COLUMN_USERNAME + " = ?";
            String[] selectionArgs = {username};
            cursor = db.query(DatabaseHelper.TABLE_USERS, null, selection, selectionArgs, null, null, null);

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

    @Override
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                new String[] {DatabaseHelper.COLUMN_USER_ID, DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_PASSWORD},
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                int columnIndexUserId = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID);
                int columnIndexUsername = cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME);
                int columnIndexPassword = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD);

                User user = new User();
                // 获取索引对应的字段值
                int userId = cursor.getInt(columnIndexUserId);
                String username = cursor.getString(columnIndexUsername);
                String password = cursor.getString(columnIndexPassword);

                // 设置字段值到User对象中
                user.setUser_id(userId);
                user.setUsername(username);
                user.setPassword(password);

                userList.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return userList;
    }


}
