package com.example.oceanicos.horusvideo201;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends Activity {
    ImageView imageView;
    Animation animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imageView = (ImageView)findViewById(R.id.imageView) ;
        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.animation_splashscreen);
        imageView.startAnimation(animation);
       /* imageView.post(new Runnable() {
            @Override
            public void run() {
                ((AnimationDrawable) imageView.getBackground()).start();
            }
        });*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(intent);
            }
        },4000);
    }
}
