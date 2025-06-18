package com.example.donorblood;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class splash_screen extends AppCompatActivity implements Animation.AnimationListener{
ImageView imageView;
Animation animation;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        imageView=findViewById(R.id.splash_logo);
        animation= AnimationUtils.loadAnimation(splash_screen.this,R.anim.splash_anim);
        animation.setAnimationListener(this);
        imageView.startAnimation(animation);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent(splash_screen.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },30000);
        }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
