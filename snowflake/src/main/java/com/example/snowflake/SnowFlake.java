package com.example.snowflake;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

/**
 * Created by fanenqian on 2017/8/31.
 */

public class SnowFlake {
    // 雪花的角度
    private static final float ANGE_RANGE = 0.1f; // 角度范围
    private static final float HALF_ANGLE_RANGE = ANGE_RANGE / 2f; // 一般的角度
    private static final float HALF_PI = (float) Math.PI / 2f; // 半PI
    private static final float ANGLE_SEED = 25f; // 角度随机种子
    private static final float ANGLE_DIVISOR = 10000f; // 角度的分母
    // 雪花的移动速度
    private static final float INCREMENT_LOWER = 2f;
    private static final float INCREMENT_UPPER = 4f;

    // 雪花的大小
    private static final float FLAKE_SIZE_LOWER = 7f;
    private static final float FLAKE_SIZE_UPPER = 20f;
    private final RandomGenerator mRandom; // 随机控制器
    private final Point mPosition; // 雪花位置
    private float mAngle; // 角度
    private final float mIncrement; // 雪花的速度
    private final float mFlakeSize; // 雪花的大小
    private final Paint mPaint; // 画笔
    private Drawable flakeSrc;//雪花样式

    private SnowFlake(RandomGenerator random, Point position, float angle, float increment, float flakeSize, Paint paint, Drawable draSrc) {
        mRandom = random;
        mPosition = position;
        mIncrement = increment;
        mFlakeSize = flakeSize;
        mPaint = paint;
        mAngle = angle;
        flakeSrc = draSrc;
    }

    public static SnowFlake create(int width, int height, Paint paint, int minSize, int maxSize, Drawable flakeSrc, float spMin, float spMax) {
        RandomGenerator random = new RandomGenerator();
        int x = random.getRandom(width);
        int y = random.getRandom(height);
        Point position = new Point(x, y);
        float angle = random.getRandom(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE;
        float increment = random.getRandom(spMin, spMax);
        float flakeSize = random.getRandom(minSize, maxSize);
        return new SnowFlake(random, position, angle, increment, flakeSize, paint, flakeSrc);
    }

    //绘制雪花
    public void onDraw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        move(width, height);
        if (null != flakeSrc) {
            canvas.drawBitmap(drawable2Bitmap(flakeSrc), mPosition.x, mPosition.y, mPaint);
        } else {
            canvas.drawCircle(mPosition.x, mPosition.y, mFlakeSize, mPaint);
        }
    }

    //移动雪花
    private void move(int width, int height) {
        double x = mPosition.x + (mIncrement * Math.cos(mAngle));
        double y = mPosition.y + (mIncrement * Math.sin(mAngle));
        mAngle += mRandom.getRandom(-ANGLE_SEED, ANGLE_SEED) / ANGLE_DIVISOR; // 随机晃动
        mPosition.set((int) x, (int) y);
        // 移除屏幕, 重新开始
        if (!isInside(width, height)) {
            reset(width);
        }
    }

    // 判断是否在其中
    private boolean isInside(int width, int height) {
        int x = mPosition.x;
        int y = mPosition.y;
        return x >= -mFlakeSize - 1 && x + mFlakeSize <= width && y >= -mFlakeSize - 1 && y - mFlakeSize < height;
    }

    // 重置雪花
    private void reset(int width) {
        mPosition.x = mRandom.getRandom(width);
        mPosition.y = (int) (-mFlakeSize - 1); // 最上面
        mAngle = mRandom.getRandom(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE;
    }

    /**
     * Drawable 转 bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }

    }
}
