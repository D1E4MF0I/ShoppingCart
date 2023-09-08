package com.dreamfor.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import com.dreamfor.shoppingcart.adapter.ProductAdapter;
import com.dreamfor.shoppingcart.dao.impl.ProductDaoImpl;
import com.dreamfor.shoppingcart.database.DatabaseHelper;
import com.dreamfor.shoppingcart.domain.Product;
import com.dreamfor.shoppingcart.domain.ProductItem;
import com.dreamfor.shoppingcart.service.ProductService;
import com.dreamfor.shoppingcart.service.impl.ProductServiceImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProductsShowActivity extends AppCompatActivity {
    TextView ps_back_tv;
    EditText ps_search_et;
    ImageButton ps_search_ib;
    ImageButton ps_gadd_ib;
    ListView ps_lv;

    ProductService productService;

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

    private void init(){
        ps_back_tv = findViewById(R.id.ps_back_tv);
        ps_search_et = findViewById(R.id.ps_search_et);
        ps_search_ib = findViewById(R.id.ps_search_ib);
        ps_gadd_ib = findViewById(R.id.ps_gadd_ib);
        ps_lv = findViewById(R.id.ps_lv);

        productService = new ProductServiceImpl(new ProductDaoImpl(new DatabaseHelper(this)));

        ps_back_tv.setOnClickListener(v -> finish());

        List<Product> productList = productService.getAllProducts();

        // 设定Adapter
        ProductAdapter productAdapter = new ProductAdapter(this, productList);
        ps_lv.setAdapter(productAdapter);

        // 临时存储
        List<Product> tempSearchIB = new ArrayList<>();

        // 搜索按键
        ps_search_ib.setOnClickListener(v -> {
            String keyword = ps_search_et.getText().toString();

            productList.addAll(tempSearchIB);
            tempSearchIB.clear();

            if(!keyword.isEmpty()){
                // 创建一个迭代器
                Iterator<Product> iterator = productList.iterator();
                while (iterator.hasNext()) {
                    Product product = iterator.next();
                    if (!product.getProduct_name().contains(keyword)) {
                        // 添加到临时列表中
                        tempSearchIB.add(product);

                        // 从原列表中删除
                        iterator.remove();
                    }
                }
            }
            productAdapter.notifyDataSetChanged();
        });

        ps_gadd_ib.setOnClickListener(v -> {
            Intent intent = new Intent(ProductsShowActivity.this, ProductAddActivity.class);
            ps_search_et.setText("");
            startActivity(intent);
        });

    }

    // TODO:（可选优化）处理刷新问题
    @Override
    protected void onPostResume() {
        init();
        super.onPostResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_show);

//        setStatusBar();
        // TODO:商品展示列表
        init();

    }
}