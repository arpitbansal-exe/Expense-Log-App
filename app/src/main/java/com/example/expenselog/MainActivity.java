package com.example.expenselog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN=2500;

    //variables
    Animation topAnim,bottomAnim,slideout;
    TextView logo,slogan,textView3;

    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        slideout= AnimationUtils.loadAnimation(this,R.anim.slide_out);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        logo=findViewById(R.id.textView);
        slogan=findViewById(R.id.textView2);
        textView3=findViewById(R.id.textView3);

        lottieAnimationView=findViewById(R.id.lottieAnimationView);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);
        textView3.setAnimation(slideout);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);

    }
}