package com.dreamfor.shoppingcart.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.dreamfor.shoppingcart.dao.UserDao;
import com.dreamfor.shoppingcart.database.DatabaseHelper;
import com.dreamfor.shoppingcart.domain.User;

import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    private DatabaseHelper databaseHelper;

    public UserDaoImpl(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    // 更新用户选择的商品
    @Override
    public void updateSelectedProducts(int userId, List<Integer> selectedProducts) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SELECTED_PRODUCTS, TextUtils.join(",", selectedProducts)); // 将选择的商品ID以逗号分隔的字符串形式保存
        db.update(DatabaseHelper.TABLE_USERS, values,  DatabaseHelper.COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
    }

    // 获取用户选择的商品
    @Override
    public List<Integer> getSelectedProducts(int userId) {
        List<Integer> selectedProducts = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, new String[]{DatabaseHelper.COLUMN_SELECTED_PRODUCTS}, DatabaseHelper.COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_SELECTED_PRODUCTS);
            if(columnIndex >= 0){
                String selectedProductsString = cursor.getString(columnIndex); // 获取保存的商品ID字符串
                String[] selectedProductsArray = selectedProductsString.split(",");
                for (String productId : selectedProductsArray) {
                    selectedProducts.add(Integer.parseInt(productId));
                }
            }
        }
        cursor.close();
        db.close();
        return selectedProducts;
    }

    @Override
    public User queryUserByID(int userId) {
        User user = null;

        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_USERS, new String[]{DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_PASSWORD, DatabaseHelper.COLUMN_SELECTED_PRODUCTS}, DatabaseHelper.COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);
        if(cursor.moveToFirst()){
            int userIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME);
            int passIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD);
            int selectIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_SELECTED_PRODUCTS);

            if(userIndex >= 0 && passIndex >= 0 && selectIndex >= 0){
                user = new User();
                user.setUser_id(String.valueOf(userId));
                user.setUsername(cursor.getString(userIndex));
                user.setPassword(cursor.getString(passIndex));
                List<Integer> selectedProducts = new ArrayList<>();

                String selectedProductsString = cursor.getString(selectIndex); // 获取保存的商品ID字符串
                String[] selectedProductsArray = selectedProductsString.split(",");
                for (String productId : selectedProductsArray) {
                    selectedProducts.add(Integer.parseInt(productId));
                }
                user.setSelected_products(selectedProducts);
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
