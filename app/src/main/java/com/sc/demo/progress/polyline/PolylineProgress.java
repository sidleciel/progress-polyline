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
    private float progress = 0.f;
    private Paint mPaint;
    private Path  mPath;

    private boolean hasEndPoint = true;
    private boolean useDefault  = true;
    int mProgressColor = 0x00D9D8, mBackgroundColor = 0x73778F;
    int   mStrokeWidth;
    float mDashWidth = 10;

    float[] intervals = new float[]{mDashWidth, mDashWidth};
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
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        if (useDefault)
            setDefaultPath();
    }

    public int getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.mStrokeWidth = strokeWidth;
    }

    public void setDashWidth(float dashWidth) {
        intervals = new float[]{dashWidth, dashWidth};
        mDashWidth = dashWidth;
    }

    public float getDashWidth() {
        return mDashWidth;
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

    public void setPath(Path path) {
        useDefault = false;
        mPath.reset();
        mPath.set(path);
        invalidate();
    }

    protected void setDefaultPath() {
        int mPolyRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20.f, getResources().getDisplayMetrics());
        Path path = new Path();
        int circleRadius = mStrokeWidth * 2, circleX = circleRadius + mStrokeWidth, circleY = circleRadius + mStrokeWidth, traingleHalfHeight = circleRadius;
        float x = circleX, y = circleY;
        path.moveTo(x, y);
        y = mHeight - mStrokeWidth - mPolyRadius - traingleHalfHeight;
        path.lineTo(x, y);
        RectF rectF = new RectF(x, y - mPolyRadius - mStrokeWidth, x + mPolyRadius * 2, y + mPolyRadius);
        path.arcTo(rectF, 180, -90);
        x = mWidth - mStrokeWidth;
        y = y + mPolyRadius;
        path.lineTo(x, y);
        setPath(path);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        PathMeasure measure = new PathMeasure(mPath, false);
        float circleRadius = mStrokeWidth * 2, triangleHeight = (circleRadius * 2.4f), triangleBottom = (float) (Math.tan(Math.toRadians(30)) * triangleHeight * 2);
        float[] startPos = new float[2], endPos = new float[2], endTan = new float[2];

        float length = measure.getLength();
        measure.getPosTan(0, startPos, null);
        measure.getPosTan(length, endPos, endTan);
        //获取旋转角度
        float degree = (float) (Math.atan2(endTan[1], endTan[0]) * 180 / Math.PI);

        Path dst = new Path();
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(mBackgroundColor);
        if (hasEndPoint) {
            length -= triangleHeight;
            //背景-圆
            mPaint.setStyle(Paint.Style.FILL);
            dst.reset();
            dst.moveTo(startPos[0], startPos[1]);
            dst.addCircle(startPos[0], startPos[1], circleRadius, Path.Direction.CW);//顺时针绘制
            canvas.drawPath(dst, mPaint);
            //背景-三角形
            canvas.save();
            dst.reset();
            canvas.translate(endPos[0], endPos[1]);
            dst.rLineTo(-triangleHeight, -triangleBottom / 2);
            dst.rLineTo(0, triangleBottom);
            dst.close();
            canvas.rotate(degree);
            canvas.drawPath(dst, mPaint);
            canvas.restore();
            mPaint.setStyle(Paint.Style.STROKE);
        }
        dst.reset();
        PathEffect pathEffect = new DashPathEffect(intervals, mDashWidth);
        mPaint.setPathEffect(pathEffect);
        measure.getSegment(0, length, dst, true);
        canvas.drawPath(dst, mPaint);

        //重置参数
        mPaint.setPathEffect(null);
        mPaint.setColor(mProgressColor);

        //前景进度
        dst.reset();
        dst.moveTo(startPos[0], startPos[1]);
        float circleProgress = 1.f, lineProgress = 0.f, triangleProgress = 0.f;
        float circleProgressPercent = 0.2f, triangleProgressPercent = 0.2f, lineProgressPercent = 1.f - circleProgressPercent - triangleProgressPercent;
        if (progress < circleProgressPercent) circleProgress = progress / circleProgressPercent;
        if (!hasEndPoint) {
            triangleProgressPercent = circleProgressPercent = 0.f;
            lineProgress = 1.f;
            circleProgress = triangleProgress = 0;
        }
        RectF rectF;
        //圆
        if (hasEndPoint) {
            mPaint.setStyle(Paint.Style.FILL);
            if (circleProgress == 1.f) {
                dst.addCircle(startPos[0], startPos[1], circleRadius, Path.Direction.CW);//顺时针绘制
                canvas.drawPath(dst, mPaint);
            } else {
                float angle = 360.f * circleProgress;
                rectF = new RectF(startPos[0] - circleRadius, startPos[1] - circleRadius, startPos[0] + circleRadius, startPos[1] + circleRadius);
                dst.arcTo(rectF, -90 - angle / 2, angle, true);
                dst.close();
                canvas.drawPath(dst, mPaint);
            }
            mPaint.setStyle(Paint.Style.STROKE);
        }
        //进度
        if (circleProgress == 1.f || !hasEndPoint) {
            lineProgress = (progress - circleProgressPercent) / lineProgressPercent;
            if (lineProgress > 1.f) lineProgress = 1.f;
        }
        measure.getSegment(0, length * lineProgress, dst, true);
        canvas.drawPath(dst, mPaint);
        //三角形
        if (hasEndPoint) {
            if (lineProgress == 1.f) {
                triangleProgress = (progress - circleProgressPercent - lineProgressPercent) / triangleProgressPercent;
            }
            canvas.save();
            canvas.translate(endPos[0], endPos[1]);
            canvas.rotate(degree);
            dst.reset();
            mPaint.setStyle(Paint.Style.FILL);
            float d1 = triangleHeight * (1 - triangleProgress), h1 = (float) (d1 * Math.tan(Math.toRadians(30)));
            dst.moveTo(-triangleHeight, -triangleBottom / 2);
            dst.lineTo(-d1, -h1);
            dst.lineTo(-d1, +h1);
            dst.lineTo(-triangleHeight, +triangleBottom / 2);
            dst.close();
            canvas.drawPath(dst, mPaint);
            canvas.restore();
        }

    }
}
