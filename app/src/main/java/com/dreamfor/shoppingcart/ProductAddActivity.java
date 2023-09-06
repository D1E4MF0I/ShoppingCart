package com.dreamfor.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamfor.shoppingcart.dao.impl.ProductDaoImpl;
import com.dreamfor.shoppingcart.database.DatabaseHelper;
import com.dreamfor.shoppingcart.domain.Product;
import com.dreamfor.shoppingcart.service.ProductService;
import com.dreamfor.shoppingcart.service.impl.ProductServiceImpl;

public class ProductAddActivity extends AppCompatActivity {
    EditText add_pname_et;
    TextView add_back_tv;
    EditText add_price_et;
    Button add_add_btn;
    Button add_del_btn;
    EditText add_text_et;

    ProductService productService;

    private void init(){
        add_pname_et = findViewById(R.id.add_pname_et);
        add_price_et = findViewById(R.id.add_price_et);
        add_text_et = findViewById(R.id.add_text_et);

        add_add_btn = findViewById(R.id.add_add_btn);
        add_del_btn = findViewById(R.id.add_del_btn);
        add_back_tv = findViewById(R.id.add_back_tv);

        productService = new ProductServiceImpl(new ProductDaoImpl(new DatabaseHelper(this)));

        // 添加按钮
        add_add_btn.setOnClickListener(v -> {
            Product product = new Product();
            product.setProduct_name(String.valueOf(add_pname_et.getText()));
            String priceStr = String.valueOf(add_price_et.getText());
            priceStr = priceStr.isEmpty() ? "0.00" : priceStr;
            product.setPrice(Double.valueOf(priceStr));
            product.setProduct_text(String.valueOf(add_text_et.getText()));

            // 处理空置情况
            if(product.getProduct_name().isEmpty()){
                Toast.makeText(this, "商品至少得有个名字吧！", Toast.LENGTH_SHORT).show();
                return;
            }
            if(product.getProduct_text().isEmpty())
                product.setProduct_text("这是一段默认文字，因为这个商品并没有描述……");
            if(product.getPrice() == 0.00){
                product.setProduct_text("(慈善家商品) " + product.getProduct_text());
            }

            if(!productService.isProductExists(product.getProduct_name())){
                if(productService.insertProduct(product)){
                    Toast.makeText(this, "添加成功！", Toast.LENGTH_SHORT).show();
                    add_pname_et.setText("");
                    add_price_et.setText("");
                    add_text_et.setText("");
                } else {
                    Toast.makeText(this, "添加失败！未知错误！", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(this, "添加失败！此商品已经存在！", Toast.LENGTH_SHORT).show();
                add_pname_et.setText("");
                add_price_et.setText("");
                add_text_et.setText("");
                return;
            }
        });

        // 删除按钮
        add_del_btn.setOnClickListener(v -> {
            String product_name = String.valueOf(add_pname_et.getText());
            if(productService.deleteProductByName(product_name)){
                Toast.makeText(this, "删除成功！", Toast.LENGTH_SHORT).show();
                add_pname_et.setText("");
                add_price_et.setText("");
                add_text_et.setText("");
            } else {
                Toast.makeText(this, "删除失败！商品列表没这东西！", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        // 返回按钮
        add_back_tv.setOnClickListener(v -> {
            finish();
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);

        init();
    }
}