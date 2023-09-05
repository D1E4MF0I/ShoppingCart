package com.dreamfor.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dreamfor.shoppingcart.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    EditText usernameET;
    EditText passwordET;
    Button lBtn;
    Button rBtn;

    DatabaseHelper dbHelper;

    private void clearAll(){
        usernameET.setText("");
        passwordET.setText("");
    }

    private void init(){
        usernameET = findViewById(R.id.login_et_username);
        passwordET = findViewById(R.id.login_et_password);
        lBtn = findViewById(R.id.login_lButton);
        rBtn = findViewById(R.id.login_rButton);

        dbHelper = new DatabaseHelper(this);

        // 在登录按钮点击事件中进行验证
        lBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入的用户名和密码
                String username = usernameET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();

                // 验证用户名和密码是否为空
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "请输入用户名和密码！", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 验证用户名和密码格式是否合法
                String usernameRegex = "\\w+"; // 用户名由字母、数字或下划线组成
                String passwordRegex = ".{6,}"; // 密码至少6位
                if (!username.matches(usernameRegex) || !password.matches(passwordRegex)) {
                    Toast.makeText(LoginActivity.this, "用户名或密码格式不正确，请重新输入！", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 查询用户信息
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String[] columns = {DatabaseHelper.COLUMN_PASSWORD};
                String selection = DatabaseHelper.COLUMN_USERNAME + "=?";
                String[] selectionArgs = {username};
                Cursor cursor = null;
                try {
                    cursor = db.query(DatabaseHelper.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
                    if (cursor.moveToFirst()) {
                        String pwd = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD));
                        if (pwd.equals(password)) {
                            // 密码匹配成功，跳转到主界面
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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







                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                // 清楚ET数据
                clearAll();

                startActivity(intent);
            }
        });

        // 设置注册按钮点击事件
        rBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到注册页面
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                // 清楚ET数据
                clearAll();

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }
}