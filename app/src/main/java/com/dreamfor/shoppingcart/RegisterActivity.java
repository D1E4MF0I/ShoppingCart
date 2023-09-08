package com.dreamfor.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dreamfor.shoppingcart.dao.impl.UserDaoImpl;
import com.dreamfor.shoppingcart.database.DatabaseHelper;
import com.dreamfor.shoppingcart.domain.User;
import com.dreamfor.shoppingcart.service.UserService;
import com.dreamfor.shoppingcart.service.impl.UserServiceImpl;

public class RegisterActivity extends AppCompatActivity {
    EditText usernameET;
    EditText passwordET;
    EditText passwordRET;
    Button regBtn;
    Button cancelBtn;

    UserService userService;

    private void init(){
        usernameET = findViewById(R.id.reg_usernameET);
        passwordET = findViewById(R.id.reg_passwordET);
        passwordRET = findViewById(R.id.reg_passwordRET);

        regBtn = findViewById(R.id.reg_regBtn);
        cancelBtn = findViewById(R.id.reg_cancelBtn);

        userService = new UserServiceImpl(new UserDaoImpl(new DatabaseHelper(getApplicationContext())));

        // 注册验证
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入的用户名和密码
                String username = usernameET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();
                String passwordR = passwordRET.getText().toString().trim();

                // 验证用户名和密码是否为空
                if (username.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请输入注册用户名！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入注册密码！", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 验证用户名和密码格式是否合法
                String usernameRegex = ".{6,}"; // 用户名至少6位
                String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z0-9_]{6,}$"; // 密码必须同时包含字母和数字 至少是6位
                if(!username.matches(usernameRegex)){
                    Toast.makeText(RegisterActivity.this, "用户名至少6位", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.matches(passwordRegex)){
                    Toast.makeText(RegisterActivity.this, "密码必须同时包含字母和数字，至少6位", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(userService.isUserExists(username)){
                    Toast.makeText(RegisterActivity.this, "该用户名已存在，请输入其他用户名！", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    // 验证重复密码
                    if(passwordR.isEmpty()){
                        Toast.makeText(getApplicationContext(), "请重复密码！", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if(!password.equals(passwordR)){
                            Toast.makeText(getApplicationContext(), "两次密码输入不同，请确认密码！", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            User user = new User(username, password);
                            if(userService.insertUser(user)){
                                Toast.makeText(getApplicationContext(), "添加用户成功！", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }else{
                                Toast.makeText(getApplicationContext(), "添加用户失败！\n用户已存在或密码错误！\n请稍后再试！", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
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
        setContentView(R.layout.activity_register);

        setStatusBar();
        init();
    }
}