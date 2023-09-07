package com.dreamfor.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamfor.shoppingcart.dao.impl.ProductDaoImpl;
import com.dreamfor.shoppingcart.database.DatabaseHelper;
import com.dreamfor.shoppingcart.domain.Product;
import com.dreamfor.shoppingcart.domain.ProductItem;
import com.dreamfor.shoppingcart.domain.ProductQuantity;
import com.dreamfor.shoppingcart.adapter.ShoppingCartAdapter;
import com.dreamfor.shoppingcart.service.ProductService;
import com.dreamfor.shoppingcart.service.impl.ProductServiceImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView userNameTV;
    EditText searchET;

    // TODO:返回消除？
    @Override
    protected void onDestroy() {
        // 清除登陆记录
        SharedPreferences sharedPreferences = getSharedPreferences(SplashScreenActivity.LoginShare,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SplashScreenActivity.KEY_USERNAME, "");
        editor.putInt(SplashScreenActivity.KEY_USERID, -1);
        editor.apply();

        Toast.makeText(getApplicationContext(), "账号已退出", Toast.LENGTH_SHORT).show();

        boolean autoLogin = sharedPreferences.getBoolean(LoginActivity.AUTO_LOGIN_FLAG, false);

        editor.putBoolean(LoginActivity.AUTO_LOGIN_FLAG, false);
        editor.apply();

        if(autoLogin){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        super.onDestroy();
    }

    ImageButton searchIB;
    ImageButton goodsIB;
    ListView lv;

    ProductService productService;

    private void init(){
        userNameTV = findViewById(R.id.main_userName);
        searchET = findViewById(R.id.main_search_et);
        searchIB = findViewById(R.id.main_search_ib);
        goodsIB = findViewById(R.id.main_goods_ib);
        lv = findViewById(R.id.main_lv);

        productService = new ProductServiceImpl(new ProductDaoImpl(new DatabaseHelper(this)));

        // 账户名点击退出操作
        SharedPreferences sharedPreferences = getSharedPreferences(SplashScreenActivity.LoginShare,MODE_PRIVATE);
        String username = sharedPreferences.getString(SplashScreenActivity.KEY_USERNAME, "");
        Integer user_id = sharedPreferences.getInt(SplashScreenActivity.KEY_USERID, -1);
        if(user_id == -1){
            Toast.makeText(getApplicationContext(), "账号出现故障，请联系系统管理员！", Toast.LENGTH_SHORT).show();
        }
        userNameTV.setText("#" + user_id + " " + username);

        userNameTV.setOnClickListener(new View.OnClickListener() {
            private long exitTime;
            @Override
            public void onClick(View v) {
                if(System.currentTimeMillis() - exitTime > 2000 || exitTime == 0){
                    Toast.makeText(getApplicationContext(), "再次点击退出账号！", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    // 清除登陆记录
                    SharedPreferences sharedPreferences = getSharedPreferences(SplashScreenActivity.LoginShare,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SplashScreenActivity.KEY_USERNAME, "");
                    editor.putInt(SplashScreenActivity.KEY_USERID, -1);
                    editor.apply();

                    Toast.makeText(getApplicationContext(), "账号已退出", Toast.LENGTH_SHORT).show();

                    boolean autoLogin = sharedPreferences.getBoolean(LoginActivity.AUTO_LOGIN_FLAG, false);

                    editor.putBoolean(LoginActivity.AUTO_LOGIN_FLAG, false);
                    editor.apply();

                    if(autoLogin){
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                    finish();
                }
            }
        });



        // 获取物品数量集
        List<ProductQuantity> productQuantityList = productService.getUserProducts(user_id);
        // 获得用户对应物品列表
        List<Product> productList = productService.getProductsByProductQuantities(productQuantityList);


        // TODO:Adapter更新优化（未测试
        // 设定Adapter
        List<ProductItem> productItemList = getProductItemList(productQuantityList, productList);

        // 保存未搜索
        List<ProductItem> tempSearchIB = new ArrayList<>();

        ShoppingCartAdapter shoppingCartAdapter = new ShoppingCartAdapter(this, productItemList, productService);
        lv.setAdapter(shoppingCartAdapter);

        // 搜索功能
        searchIB.setOnClickListener(v -> {
            String keyword = searchET.getText().toString();

            productItemList.addAll(tempSearchIB);
            tempSearchIB.clear();

            if(!keyword.isEmpty()){
                // 创建一个迭代器
                Iterator<ProductItem> iterator = productItemList.iterator();
                while (iterator.hasNext()) {
                    ProductItem productItem = iterator.next();
                    if (!productItem.getProduct_name().contains(keyword)) {
                        // 添加到临时列表中
                        tempSearchIB.add(productItem);

                        // 从原列表中删除
                        iterator.remove();
                    }
                }
            }
            shoppingCartAdapter.notifyDataSetChanged();
        });


        // 商品按钮 按下跳转 同时清空搜索栏
        goodsIB.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductsShowActivity.class);
            searchET.setText("");
            startActivity(intent);
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    // TODO:（可选优化）
    @Override
    protected void onPostResume() {
        init();
        super.onPostResume();
    }

    private List<ProductItem> getProductItemList(List<ProductQuantity> productQuantityList, List<Product> productList){
        List<ProductItem> productItemList = new ArrayList<>();
        for (int i = 0; i < productQuantityList.size() && i < productList.size(); i++) {
            ProductQuantity productQuantity = productQuantityList.get(i);
            Product product = productList.get(i);
            ProductItem productItem = new ProductItem();

            productItem.setUser_Id(productQuantity.getUserId());
            productItem.setProduct_id(productQuantity.getProductId());
            productItem.setQuantity(productQuantity.getQuantity());

            productItem.setProduct_name(product.getProduct_name());
            productItem.setProduct_text(product.getProduct_text());
            productItem.setPrice(product.getPrice());
            productItem.setAllPrice(productItem.getPrice() * productItem.getQuantity());

            productItemList.add(productItem);
        }
        return productItemList;
    }
}