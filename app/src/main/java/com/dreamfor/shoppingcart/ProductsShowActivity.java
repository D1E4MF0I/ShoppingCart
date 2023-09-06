package com.dreamfor.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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

        // TODO:商品展示列表
        init();

    }
}