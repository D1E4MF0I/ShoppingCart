package com.dreamfor.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

                // 验证用户名和密码是否为空
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请输入注册用户名和密码！", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
    }
}