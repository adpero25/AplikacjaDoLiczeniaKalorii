package com.example.application.surfaceViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class CaloriesEatenSurfaceView extends SurfaceView {

    private int caloriesTarget = 2000;
    private int currentCalories = 0;
    private void init() {
        setWillNotDraw(false);
    }

    public CaloriesEatenSurfaceView(Context context) {
        super(context);
        init();
    }

    public CaloriesEatenSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CaloriesEatenSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CaloriesEatenSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setTarget (int target) {
        this.caloriesTarget = target;
    }

    public void setProgress (int caloriesEaten) {
        this.currentCalories = caloriesEaten;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.setBackgroundColor(Color.TRANSPARENT);
        this.setZOrderOnTop(true); //necessary

        int strokeWidth = 50;
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float big_radius = getWidth() / 2f - strokeWidth;
        float small_radius = getWidth() / 2f - strokeWidth;
        Paint mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAntiAlias(true);

        RectF oval = new RectF(cx - big_radius, cy - big_radius, cx + big_radius, cy + big_radius);
        mPaint.setARGB(255, 0, 240, 255);
        canvas.drawArc(oval, -90, (int)(360 * currentCalories / caloriesTarget), false, mPaint);

        Paint small_paint = new Paint(Color.BLACK);
        canvas.drawCircle(cx, cy, small_radius - strokeWidth/2f, small_paint);

        invalidate();
    }
}
