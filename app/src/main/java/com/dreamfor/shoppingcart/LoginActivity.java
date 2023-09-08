package com.dreamfor.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
                Toast.makeText(LoginActivity.this, "用户名至少6位", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.matches(passwordRegex)){
                Toast.makeText(LoginActivity.this, "密码必须同时包含字母和数字，至少6位", Toast.LENGTH_SHORT).show();
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

                        finish();
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
        setContentView(R.layout.activity_login);
//        setStatusBar();
        init();
    }
}