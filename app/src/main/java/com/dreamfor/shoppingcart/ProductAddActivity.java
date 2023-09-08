package com.dreamfor.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamfor.shoppingcart.dao.impl.ProductDaoImpl;
import com.dreamfor.shoppingcart.database.DatabaseHelper;
import com.dreamfor.shoppingcart.domain.Product;
import com.dreamfor.shoppingcart.service.ProductService;
import com.dreamfor.shoppingcart.service.impl.ProductServiceImpl;

import java.text.DecimalFormat;
import java.util.Locale;

public class ProductAddActivity extends AppCompatActivity {
    EditText add_pname_et;
    TextView add_back_tv;
    EditText add_price_et;
    Button add_add_btn;
    Button add_del_btn;
    EditText add_text_et;

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

            // 格式化存储
            priceStr = priceStr.isEmpty() ? "0.00" : priceStr;
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            String formattedPrice = decimalFormat.format(Double.valueOf(priceStr));
            product.setPrice(Double.valueOf(formattedPrice));

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
        setStatusBar();

        init();
    }
}