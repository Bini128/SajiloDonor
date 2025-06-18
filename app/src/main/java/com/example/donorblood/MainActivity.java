package com.example.donorblood;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 imageSlider;
    private Handler handler = new Handler();
    private List<Integer> imageList;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageSlider = findViewById(R.id.imageSlider);
        Button startButton = findViewById(R.id.startButton);

        imageList = new ArrayList<>();
        imageList.add(R.drawable.home);
        imageList.add(R.drawable.home2);

        ImageAdapter adapter = new ImageAdapter(imageList);
        imageSlider.setAdapter(adapter);

        handler.postDelayed(sliderRunnable, 2000);

        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,login.class);
            startActivity(intent);
            finish(); // Optional if you don't want to go back to homepage
        });
    }

    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (position == imageList.size()) position = 0;
            imageSlider.setCurrentItem(position++, true);
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(sliderRunnable);
    }
}
