package com.example.snowflake;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class SnowView extends View {
    private int flakeCount;// 雪花数量
    private static final int DELAY = 5; // 延迟
    private SnowFlake[] mSnowFlakes; // 雪花
    private int minSize;
    private int maxSize;
    private Drawable flakeSrc;
    private float spMin;
    private float spMax;

    public SnowView(Context context) {
        super(context);
    }

    public SnowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initattrs(context, attrs);
    }

    public SnowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initattrs(context, attrs);
    }

    private void initattrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Snow);
        flakeCount = typedArray.getInt(R.styleable.Snow_flakeCount, 150);
        minSize = typedArray.getInt(R.styleable.Snow_minSize, 7);
        maxSize = typedArray.getInt(R.styleable.Snow_maxSize, 20);
        flakeSrc = typedArray.getDrawable(R.styleable.Snow_flakeSrc);
        spMin = typedArray.getFloat(R.styleable.Snow_minSpeed, 2);
        spMax = typedArray.getFloat(R.styleable.Snow_maxSpeed, 4);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            initSnow(w, h);
        }
    }

    private void initSnow(int width, int height) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); // 抗锯齿
        paint.setColor(Color.WHITE); // 白色雪花
        paint.setStyle(Paint.Style.FILL); // 填充;
        mSnowFlakes = new SnowFlake[flakeCount];
        for (int i = 0; i < flakeCount; ++i) {
            mSnowFlakes[i] = SnowFlake.create(width, height, paint, minSize, maxSize, flakeSrc, spMin, spMax);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (SnowFlake s : mSnowFlakes) {
            s.onDraw(canvas);
        }
        // 隔一段时间重绘一次, 动画效果
        getHandler().postDelayed(runnable, DELAY);
    }

    // 重绘线程
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };
}