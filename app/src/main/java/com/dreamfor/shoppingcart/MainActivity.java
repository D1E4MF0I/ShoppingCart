package com.dreamfor.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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

        SharedPreferences sharedPreferences = getSharedPreferences(SplashScreenActivity.LoginShare,MODE_PRIVATE);
        String username = sharedPreferences.getString(SplashScreenActivity.KEY_USERNAME, "");
        Integer user_id = sharedPreferences.getInt(SplashScreenActivity.KEY_USERID, -1);
        if(user_id == -1){
            Toast.makeText(getApplicationContext(), "账号出现故障，请联系系统管理员！", Toast.LENGTH_SHORT).show();
        }
        userNameTV.setText("#" + user_id + " " + username);

        // 账户名点击退出操作
        userNameTV.setOnClickListener(new View.OnClickListener() {
            private long exitTime;
            @Override
            public void onClick(View v) {
                if(System.currentTimeMillis() - exitTime > 2000 || exitTime == 0){
                    Toast.makeText(MainActivity.this, "再次点击退出账号！", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                    return;
                } else {
                    // 清除登陆记录
                    SharedPreferences sharedPreferences = getSharedPreferences(SplashScreenActivity.LoginShare,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SplashScreenActivity.KEY_USERNAME, "");
                    editor.putInt(SplashScreenActivity.KEY_USERID, -1);
                    editor.apply();

                    Toast.makeText(MainActivity.this, "账号已退出", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });



        // 获取物品数量集
        List<ProductQuantity> productQuantityList = productService.getUserProducts(user_id);
        // 获得用户对应物品列表
        List<Product> productList = productService.getProductsByProductQuantities(productQuantityList);


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

    //是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
    protected boolean useThemestatusBarColor = false;
    //是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置
    protected boolean useStatusBarColor = true;
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (useThemestatusBarColor) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colortheme));//设置状态栏背景色
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);//透明
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        } else {
            Toast.makeText(this, "低于4.4的android系统版本不存在沉浸式状态栏", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setStatusBar();
        init();
    }

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