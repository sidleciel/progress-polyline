package com.sc.demo.progress.polyline;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;

public class MainActivity extends AppCompatActivity {
    PolylineProgress test, polylineProgress, polylineProgress2, polylineProgress3, polylineProgress4, polylineProgress5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test = findViewById(R.id.test);
        polylineProgress = findViewById(R.id.polylineProgress);
        polylineProgress2 = findViewById(R.id.polylineProgress2);
        polylineProgress3 = findViewById(R.id.polylineProgress3);
        polylineProgress4 = findViewById(R.id.polylineProgress4);
        polylineProgress5 = findViewById(R.id.polylineProgress5);


        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Path path = new Path();
                int height = test.getMeasuredHeight();
                int width = test.getMeasuredWidth();
                int strokeWidth = test.getStrokeWidth();
                int x = strokeWidth * 3, y = strokeWidth * 3;
                width -= x * 3;
                height -= y * 3;

                path.moveTo(x, y);
                path.rLineTo(0, height);
                path.rLineTo(width, 0);
                path.rLineTo(0, -height);
                path.rLineTo(-width, 0);
                test.setPath(path);
                startAnimator(test);

                setCustomPath(polylineProgress, 0);
                setCustomPath(polylineProgress2, 1);
                setCustomPath(polylineProgress3, 2);
                setCustomPath(polylineProgress4, 3);

                setLighting(polylineProgress5);
            }

            void setLighting(PolylineProgress progress) {
                Path path = new Path();
                int height = progress.getMeasuredHeight();
                int width = progress.getMeasuredWidth();
                int strokeWidth = progress.getStrokeWidth();
                int mPolyRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20.f, getResources().getDisplayMetrics());
                int circleRadius = strokeWidth * 2, circleX = circleRadius + strokeWidth, circleY = circleRadius + strokeWidth, traingleHalfHeight = circleRadius;

                float left = circleX, top = circleY, right = width - circleX, bottom = height - circleY;

                path.moveTo(width / 2, top);
                path.lineTo(width / 4, height / 2);
                path.lineTo(width / 4 * 3, height / 2);
                path.lineTo(width / 2, bottom);

                progress.setPath(path);
                startAnimator(progress);
            }

            void setCustomPath(PolylineProgress progress, int type) {
                Path path = new Path();
                int height = progress.getMeasuredHeight();
                int width = progress.getMeasuredWidth();
                int strokeWidth = progress.getStrokeWidth();
                int mPolyRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20.f, getResources().getDisplayMetrics());
                int circleRadius = strokeWidth * 2, circleX = circleRadius + strokeWidth, circleY = circleRadius + strokeWidth, traingleHalfHeight = circleRadius;

                float left = circleX, top = circleY, right = width - circleX, bottom = height - circleY;
                if (type == 0) {
                    float x = left, y = top;
                    path.moveTo(x, y);
                    y = bottom - mPolyRadius;
                    path.lineTo(x, y);
                    RectF rectF = new RectF(x, y, x + mPolyRadius * 2, bottom);
                    path.arcTo(rectF, 180, -90);
                    x = right;
                    y = bottom;
                    path.lineTo(x, y);
                } else if (type == 1) {
                    float x = right, y = top;
                    path.moveTo(x, y);
                    y = bottom - mPolyRadius;
                    path.lineTo(x, y);
                    RectF rectF = new RectF(x - mPolyRadius * 2, y, x, bottom);
                    path.arcTo(rectF, 0, 90);
                    x = left;
                    y = bottom;
                    path.lineTo(x, y);
                } else if (type == 2) {
                    float x = left, y = bottom;
                    path.moveTo(x, y);
                    y = top + mPolyRadius;
                    path.lineTo(x, y);
                    RectF rectF = new RectF(x, top, x + mPolyRadius * 2, y);
                    path.arcTo(rectF, 180, 90);
                    x = right;
                    y = top;
                    path.lineTo(x, y);
                } else if (type == 3) {
                    float x = right, y = bottom;
                    path.moveTo(x, y);
                    y = top + mPolyRadius;
                    path.lineTo(x, y);
                    RectF rectF = new RectF(x - mPolyRadius * 2, top, x, y);
                    path.arcTo(rectF, 0, -90);
                    x = left;
                    y = top;
                    path.lineTo(x, y);
                }

                progress.setPath(path);
                startAnimator(progress);
            }
        });

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
