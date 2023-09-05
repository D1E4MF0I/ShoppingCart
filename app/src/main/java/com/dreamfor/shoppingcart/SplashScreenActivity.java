package com.dreamfor.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {
    ImageView splash_Screen_RotatoICON;

    public static final String LoginShare = "LoginSharePreferences";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_USERID = "userID";

    private void init(){
        splash_Screen_RotatoICON = findViewById(R.id.splash_Screen_RotatoICON);
    }

    private void setRotate(){
        if(splash_Screen_RotatoICON == null)
            init();
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(splash_Screen_RotatoICON, "rotation", 0f, 360f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        setRotate();

        new Handler().postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences(LoginShare, MODE_PRIVATE);
            String userName = sharedPreferences.getString(KEY_USERNAME, "");
            Integer user_id = sharedPreferences.getInt(KEY_USERID, -1);

            Intent intent;

            if(userName.isEmpty() || user_id == -1){
                intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            } else {
                intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}