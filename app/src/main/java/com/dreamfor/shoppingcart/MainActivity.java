package com.dreamfor.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView userNameTV;

    private void init(){
        userNameTV = findViewById(R.id.main_userName);
        SharedPreferences sharedPreferences = getSharedPreferences(SplashScreenActivity.LoginShare,MODE_PRIVATE);
        String username = sharedPreferences.getString(SplashScreenActivity.KEY_USERNAME, "");
        userNameTV.setText(username);

        userNameTV.setOnClickListener(new View.OnClickListener() {
            private long exitTime;
            @Override
            public void onClick(View v) {
                if(System.currentTimeMillis() - exitTime > 2000 || exitTime == 0){
                    Toast.makeText(getApplicationContext(), "再次点击退出账号！", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    // 清除登陆记录
                    SharedPreferences sharedPreferences = getSharedPreferences(SplashScreenActivity.LoginShare,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SplashScreenActivity.KEY_USERNAME, "");
                    editor.apply();

                    Toast.makeText(getApplicationContext(), "账号已退出", Toast.LENGTH_SHORT).show();

                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }
}