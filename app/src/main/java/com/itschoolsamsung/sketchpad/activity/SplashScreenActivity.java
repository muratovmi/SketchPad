package com.itschoolsamsung.sketchpad.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.itschoolsamsung.sketchpad.R;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);
        init();
    }

    public void init() {
        ImageView mBrushImageView = findViewById(R.id.brush_imageview);
        TextView mLineTextView = findViewById(R.id.line_textview);
        Animation mLinearAnim = AnimationUtils.loadAnimation(this, R.anim.linear_move);
        Animation mSweepAnim = AnimationUtils.loadAnimation(this, R.anim.slide_right);
        mLineTextView.setAnimation(mSweepAnim);
        mBrushImageView.setAnimation(mLinearAnim);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, DrawingBoard.class);
            startActivity(intent);
            finish();
        }, 9000);
    }
}