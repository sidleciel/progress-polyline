package com.sc.demo.progress.polyline;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class PolylineProgress extends View {
    private float progress = 0.3f, length;
    private Paint mPaint;
    private Path  mPath;

    boolean hasDashBackground = true;
    int     mProgressColor    = 0x00D9D8, mBackgroundColor = 0x73778F;
    int mStrokeWidth, mDashWidth, mStartRadius, mPolyRadius;

    float[] intervals = new float[]{10, 10};
    private int mWidth, mHeight;

    public PolylineProgress(Context context) {
        super(context);
        init();
    }

    public PolylineProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PolylineProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        mPath = new Path();
        mPaint = new Paint();

        mProgressColor = Color.parseColor("#00D9D8");
        mBackgroundColor = Color.parseColor("#73778F");

        mPaint.setColor(mProgressColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true); // 抗锯齿
        mPaint.setDither(true); // 防抖动

        Resources res = getResources();
        mStrokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.f, res.getDisplayMetrics());
        mDashWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.f, res.getDisplayMetrics());
        mStartRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20.f, res.getDisplayMetrics());
        mPolyRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20.f, res.getDisplayMetrics());
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        if (progress < 0.f || progress > 1.f)
            throw new IllegalArgumentException("progress not between 0.0f and 1.0f");
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path dst = new Path();
        mPath.reset();
        int circleRadius = mStrokeWidth * 2, circleX = mStartRadius, circleY = circleRadius + mStrokeWidth, traingleHalfHeight = circleRadius;
        float x = circleX, y = circleY;
        float d = traingleHalfHeight * 2.4f, h = (float) (Math.tan(Math.toRadians(30)) * d);

        mPaint.setColor(mBackgroundColor);
        //背景-圆
        mPaint.setStyle(Paint.Style.FILL);
        dst.moveTo(circleX, circleY);
        dst.addCircle(circleX, circleY, circleRadius, Path.Direction.CW);//顺时针绘制
        canvas.drawPath(dst, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        //背景-dash
        mPath.moveTo(x, y);
        y = mHeight - mStrokeWidth - mPolyRadius - traingleHalfHeight;
        mPath.lineTo(x, y);
        RectF rectF = new RectF(x, y - mPolyRadius - mStrokeWidth, x + mPolyRadius * 2, y + mPolyRadius);
        mPath.arcTo(rectF, 180, -90);
        x = mWidth - mStrokeWidth;
        y = y + mPolyRadius;
        mPath.lineTo(x - d, y);

        if (hasDashBackground) {
            PathEffect pathEffect = new DashPathEffect(intervals, mPolyRadius);
            mPaint.setPathEffect(pathEffect);
            canvas.drawPath(mPath, mPaint);
        }
        //背景-三角形
        mPaint.setStyle(Paint.Style.FILL);
        dst.moveTo(x, y);
        dst.rLineTo(-d, -h);
        dst.rLineTo(0, h * 2);
        dst.close();
        canvas.drawPath(dst, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);

        //重置参数
        dst.reset();
        dst.moveTo(circleX, circleY);
        mPaint.setPathEffect(null);
        mPaint.setColor(mProgressColor);

        //前景进度
        float circleProgress = 1.f;
        float circleProgressPercent = 0.2f, triangleProgressPercent = 0.2f, lineProgressPercent = 1.f - circleProgressPercent - triangleProgressPercent;
        if (progress < circleProgressPercent) {
            circleProgress = progress / circleProgressPercent;
        }
        //圆
        mPaint.setStyle(Paint.Style.FILL);
        if (circleProgress == 1.f) {
            dst.addCircle(circleX, circleY, circleRadius, Path.Direction.CW);//顺时针绘制
            canvas.drawPath(dst, mPaint);
        } else {
            float angle = 360.f * circleProgress;
            rectF = new RectF(circleX - circleRadius, circleY - circleRadius, circleX + circleRadius, circleY + circleRadius);
            dst.arcTo(rectF, -90 - angle / 2, angle, true);
            dst.close();
            canvas.drawPath(dst, mPaint);
        }
        mPaint.setStyle(Paint.Style.STROKE);
        //进度
        float lineProgress = 0.f;
        if (circleProgress == 1.f) {
            lineProgress = (progress - circleProgressPercent) / lineProgressPercent;
            if (lineProgress > 1.f) lineProgress = 1.f;
        }
        PathMeasure measure = new PathMeasure(mPath, false);
        length = measure.getLength();
        measure.getSegment(0, length * lineProgress, dst, true);
        canvas.drawPath(dst, mPaint);
        //三角形
        float triangleProgress = 0.f;
        if (lineProgress == 1.f) {
            triangleProgress = (progress - circleProgressPercent - lineProgressPercent) / triangleProgressPercent;
        }
        dst.reset();
        mPaint.setStyle(Paint.Style.FILL);
        float d1 = d * (1 - triangleProgress), h1 = (float) (d1 * Math.tan(Math.toRadians(30)));
        dst.moveTo(x - d, y - h);
        dst.lineTo(x - d1, y - h1);
        dst.lineTo(x - d1, y + h1);
        dst.lineTo(x - d, y + h);
        dst.close();
        canvas.drawPath(dst, mPaint);

    }
}
