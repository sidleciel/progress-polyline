package com.sc.demo.progress.polyline;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateDecelerateInterpolator;

public class MainActivity extends AppCompatActivity {
    PolylineProgress test, test1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test = findViewById(R.id.test);
        test1 = findViewById(R.id.test1);

        startAnimator(test);
        startAnimator(test1);
    }

    void startAnimator(PolylineProgress target) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "progress", 0.f, 1.f)
                .setDuration(3000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }
}
