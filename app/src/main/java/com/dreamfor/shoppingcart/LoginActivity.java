package com.dreamfor.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dreamfor.shoppingcart.dao.impl.UserDaoImpl;
import com.dreamfor.shoppingcart.database.DatabaseHelper;
import com.dreamfor.shoppingcart.domain.User;
import com.dreamfor.shoppingcart.service.ProductService;
import com.dreamfor.shoppingcart.service.UserService;
import com.dreamfor.shoppingcart.service.impl.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    AutoCompleteTextView usernameACT;
    EditText passwordET;
    Button lBtn;
    Button rBtn;

    DatabaseHelper dbHelper;

    UserService userService;
    ProductService productService;

    public static final String AUTO_LOGIN_FLAG = "autoLogin";

    @Override
    protected void onPostResume() {
        init();
        super.onPostResume();
    }

    private void clearAll(){
        usernameACT.setText("");
        passwordET.setText("");
    }

    private void init(){
        usernameACT = findViewById(R.id.login_act_username);
        passwordET = findViewById(R.id.login_et_password);
        lBtn = findViewById(R.id.login_lButton);
        rBtn = findViewById(R.id.login_rButton);

        dbHelper = new DatabaseHelper(this);

        userService = new UserServiceImpl(new UserDaoImpl(dbHelper));

        // 下拉框，自动填充
        List<User> allUsers = userService.getAllUsers();
        List<String> allUserNames = new ArrayList<>();
        for (User user : allUsers) {
            allUserNames.add(user.getUsername());
        }
        System.out.println(allUserNames);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, R.layout.dropdown_item, allUserNames);
        usernameACT.setAdapter(adapter);

        // 在登录按钮点击事件中进行验证
        lBtn.setOnClickListener(v -> {
            // 获取输入的用户名和密码
            String username = usernameACT.getText().toString().trim();
            String password = passwordET.getText().toString().trim();
            // 用于保存用户ID
            Integer user_id = -1;

            // 验证用户名和密码是否为空
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "请输入用户名和密码！", Toast.LENGTH_SHORT).show();
                return;
            }

            // 验证用户名和密码格式是否合法
            String usernameRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z0-9_]{8,}$\n"; // 用户名必须同时包含字母和数字可选下划线
            String passwordRegex = ".{8,}"; // 密码至少8位
            if (!username.matches(usernameRegex) || !password.matches(passwordRegex)) {
                Toast.makeText(LoginActivity.this, "用户名或密码格式不正确，请重新输入！\n用户名必须同时包含字母和数字！\n密码至少8位！", Toast.LENGTH_SHORT).show();
                return;
            }

            // 查询用户信息
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String[] columns = {DatabaseHelper.COLUMN_PASSWORD, DatabaseHelper.COLUMN_USER_ID};
            String selection = DatabaseHelper.COLUMN_USERNAME + "=?";
            String[] selectionArgs = {username};
            Cursor cursor = null;
            try {
                cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);

                // 登录许可
                boolean loginCK = false;
                // 用户存在
                boolean userExist = false;

                // 通过扫描所有存在用户，判断密码匹配（解决存在同名用户，不能登录其他用户的情况
                while(cursor.moveToNext()){
                    int columnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD);
                    if(columnIndex >= 0){
                        userExist = true;
                        String pwd = cursor.getString(columnIndex);
                        if (pwd.equals(password)) {
                            int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID);
                            user_id = cursor.getInt(idIndex);
                            loginCK = true;
                            break;
                        }
                    }
                }

                if(userExist){
                    if(loginCK && user_id != -1){
                        // 记录登录情况
                        SharedPreferences sharedPreferences = getSharedPreferences(SplashScreenActivity.LoginShare, MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString(SplashScreenActivity.KEY_USERNAME, username);
                        edit.putInt(SplashScreenActivity.KEY_USERID, user_id);
                        edit.apply();

                        // 密码匹配成功，跳转到主界面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        // 清楚ET数据
                        clearAll();

                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "密码错误，请重新输入！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "该用户名不存在，请重新输入！", Toast.LENGTH_SHORT).show();
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }
        });

        // 设置注册按钮点击事件
        rBtn.setOnClickListener(v -> {
            // 跳转到注册页面
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

            // 清楚ET数据
            clearAll();

            startActivity(intent);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }
}