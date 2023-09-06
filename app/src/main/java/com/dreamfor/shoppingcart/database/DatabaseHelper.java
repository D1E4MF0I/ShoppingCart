package com.dreamfor.shoppingcart.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mydatabase.db";
    public static final int DATABASE_VERSION = 1;

    // 用户表
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    private static final String SQL_CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
            "(" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USERNAME + " TEXT, " +
            COLUMN_PASSWORD + " TEXT)";

    // 商品表
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_PRODUCT_NAME = "product_name";
    public static final String COLUMN_PRICE = "price";

    public static final String COLUMN_PRODUCT_TEXT = "product_text";
    private static final String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS +
            "(" + COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PRODUCT_NAME + " TEXT, " +
            COLUMN_PRICE + " REAL, " +
            COLUMN_PRODUCT_TEXT + " TEXT)";

    // 关联表
    public static final String TABLE_USER_PRODUCTS = "user_products";
    public static final String COLUMN_USER_PRODUCT_ID = "user_product_id";
    public static final String COLUMN_USER_ID_FK = "user_id";
    public static final String COLUMN_PRODUCT_ID_FK = "product_id";
    public static final String COLUMN_PRODUCT_NAME_FK = "product_name";
    public static final String COLUMN_QUANTITY = "quantity";
    private static final String SQL_CREATE_USER_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_USER_PRODUCTS +
            "(" + COLUMN_USER_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USER_ID_FK + " INTEGER, " +
            COLUMN_PRODUCT_ID_FK + " INTEGER, " +
            COLUMN_QUANTITY + " INTEGER, " +
            COLUMN_PRODUCT_NAME_FK + " TEXT, " +
            "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")," +
            "FOREIGN KEY(" + COLUMN_PRODUCT_ID_FK + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + "))";





    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        db.execSQL(SQL_CREATE_USER_PRODUCTS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 在这里升级数据库或者删除旧表并重新创建新表
    }
}

