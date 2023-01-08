package com.example.application.SurfaceViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class StepProgressSurfaceView extends SurfaceView {

    private int MaxValue = 5000;
    private float Progress = 0;

    private void init() {
        setWillNotDraw(false);
    }

    public StepProgressSurfaceView(Context context) {
        super(context);
        init();
    }

    public StepProgressSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StepProgressSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public StepProgressSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setMax(int val) {
        this.MaxValue = val;
    }

    public void setProgress(int val) {
        this.Progress = val;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRGB(0,0,0);
        float height = (getHeight() * Progress) / MaxValue;
        int width = getWidth();

        Paint paint = new Paint();
        paint.setARGB(255, 0, 240, 255);

        RectF rectFProgres = new RectF(0, height, width ,0);

        canvas.drawRoundRect(rectFProgres, 10, 10, paint);

        invalidate();
    }
}
