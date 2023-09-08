package com.dreamfor.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.dreamfor.shoppingcart.domain.ProductQuantity;
import com.dreamfor.shoppingcart.service.ProductService;
import com.dreamfor.shoppingcart.service.impl.ProductServiceImpl;

import java.text.DecimalFormat;
import java.util.Locale;

public class ProductInfoActivity extends AppCompatActivity {
    EditText info_pName_et;
    EditText info_price_et;
    Button info_add_btn;
    Button info_sub_btn;
    Button info_up_btn;
    EditText info_text_et;
    TextView info_back_tv;

    ProductService productService;

    private ProductQuantity productQuantity;

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
        info_pName_et = findViewById(R.id.info_pName_et);
        info_price_et = findViewById(R.id.info_price_et);
        info_add_btn = findViewById(R.id.info_add_btn);
        info_sub_btn = findViewById(R.id.info_sub_btn);
        info_up_btn = findViewById(R.id.info_up_btn);
        info_text_et = findViewById(R.id.info_text_et);
        info_back_tv = findViewById(R.id.info_back_tv);

        info_pName_et.setEnabled(false);
        info_price_et.setEnabled(false);
        info_text_et.setEnabled(false);

        productService = new ProductServiceImpl(new ProductDaoImpl(new DatabaseHelper(this)));


        // 获取传递的信息
        Intent intent = getIntent();
        String product_name = intent.getStringExtra("product_name");
        Double product_price = intent.getDoubleExtra("product_price", 0.00);
        String product_text = intent.getStringExtra("product_text");

        int product_id = intent.getIntExtra("product_id", -1);

        // 获得ProductQuantity
        SharedPreferences sharedPreferences = getSharedPreferences(SplashScreenActivity.LoginShare, MODE_PRIVATE);
        int userId = sharedPreferences.getInt(SplashScreenActivity.KEY_USERID, -1);
        productQuantity = productService.getProductQuantityById(userId, product_id);

        info_pName_et.setText(product_name);
        info_price_et.setText(String.format(Locale.getDefault(), "%.2f", product_price));
        info_text_et.setText(product_text);

        // 更新按钮
        info_up_btn.setOnClickListener(v -> {
            if(info_up_btn.getText().equals("更改")){
                info_up_btn.setText("保存");

                info_pName_et.setEnabled(true);
                info_price_et.setEnabled(true);
                info_text_et.setEnabled(true);

                info_add_btn.setEnabled(false);
                info_sub_btn.setEnabled(false);
            } else {
                info_up_btn.setText("更改");

                info_pName_et.setEnabled(false);
                info_price_et.setEnabled(false);
                info_text_et.setEnabled(false);

                info_add_btn.setEnabled(true);
                info_sub_btn.setEnabled(true);

                String editName = String.valueOf(info_pName_et.getText());
                Double editPrice = Double.valueOf(String.valueOf(info_price_et.getText()));
                String editText = String.valueOf(info_text_et.getText());

                // 格式化两位数
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                String formattedPrice = decimalFormat.format(editPrice);

                Product product = new Product();
                product.setProduct_id(product_id);
                product.setProduct_name(editName);
                product.setPrice(Double.valueOf(formattedPrice));
                product.setProduct_text(editText);

                // 更新商品信息
                if(productService.updateProduct(product)){
                    Toast.makeText(this, "更新成功！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "更新失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 返回按钮
        // TODO:（可选）增加临时保存机制，返回时候，填入上次保存信息
        info_back_tv.setOnClickListener(v -> {
            if(info_up_btn.getText().equals("保存")){
                Toast.makeText(this, "请保存后返回！", Toast.LENGTH_SHORT).show();
                return;
            } else {
                finish();
            }
        });

        // 增加按钮
        info_add_btn.setOnClickListener(v -> {
            if(userId != -1){
                productQuantity.setQuantity(productQuantity.getQuantity() + 1);
                productService.setProductAndSyncUser(userId, product_id, productQuantity.getQuantity());
                Toast.makeText(this, "已添加到购物车中！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "用户ID获取错误，请联系管理员处理！", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        // 减少按钮
        info_sub_btn.setOnClickListener(v -> {
            if(userId != -1){
                if(productQuantity.getQuantity() <= 0){
                    Toast.makeText(this, "购物车里已经没有这个物品了！", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(this, "删除成功！", Toast.LENGTH_SHORT).show();
                    productQuantity.setQuantity(productQuantity.getQuantity() - 1);
                    productService.setProductAndSyncUser(userId, product_id, productQuantity.getQuantity());
                }
            } else {
                Toast.makeText(this, "用户ID获取错误，请联系管理员处理！", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

//        setStatusBar();
        init();
    }
}