package com.akif.blooddonationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class splashScreenActivity extends AppCompatActivity {
   private ImageView logo;
   private   TextView slogan,title;
   private Animation topAnimation,bottomAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        logo=findViewById(R.id.logo);
        slogan=findViewById(R.id.slogan);
        title=findViewById(R.id.title);

        topAnimation= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnimation= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        logo.setAnimation(topAnimation);
        slogan.setAnimation(bottomAnimation);
        title.setAnimation(bottomAnimation);

        int SPLASH_SCREEN=4500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(splashScreenActivity.this,loginActivity.class);
                startActivity(intent);
                finish();

            }
        },SPLASH_SCREEN);
    }
}