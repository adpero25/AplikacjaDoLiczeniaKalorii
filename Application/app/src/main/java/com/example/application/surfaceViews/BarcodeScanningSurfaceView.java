package com.example.application.surfaceViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class BarcodeScanningSurfaceView extends SurfaceView {
    public BarcodeScanningSurfaceView(Context context) {
        super(context);
        Init();
    }

    public BarcodeScanningSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init();
    }

    public BarcodeScanningSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init();
    }

    public BarcodeScanningSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Init();
    }

    private void Init() {
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int rectWidth = getWidth() - 260;
        int rectHeight = getHeight() - getHeight() / 2;

        int topLeftCornerX = getWidth() / 2 - rectWidth / 2;
        int topLeftCornerY = getHeight() / 2 - rectHeight / 2;

        // white corners around scan area
        Paint paintCorners = new Paint(Paint.DITHER_FLAG);
        paintCorners.setStyle(Paint.Style.STROKE);
        paintCorners.setColor(Color.WHITE);
        paintCorners.setStrokeWidth(40);
        paintCorners.setStrokeJoin(Paint.Join.MITER);
        canvas.drawPath(createCornersPath(topLeftCornerX, topLeftCornerY, topLeftCornerX + rectWidth, topLeftCornerY + rectHeight, 150), paintCorners);

        Paint paintBackground = new Paint();
        paintBackground.setColor(Color.argb(180, 0, 0, 0));

        //transparent black rectangles around scan area
        RectF topRectF = new RectF(0, 0, getWidth(), topLeftCornerY - 40);
        canvas.drawRect(topRectF, paintBackground);
        RectF bottomRectF = new RectF(0, topLeftCornerY + rectHeight + 40, getWidth(), getHeight());
        canvas.drawRect(bottomRectF, paintBackground);
        RectF leftRectF = new RectF(0, topLeftCornerY - 40, (getWidth() - rectWidth) / 2f - 40, topLeftCornerY + rectHeight + 40);
        canvas.drawRect(leftRectF, paintBackground);
        RectF rightRectF = new RectF(topLeftCornerX + rectWidth + 40, topLeftCornerY - 40, getWidth(), topLeftCornerY + rectHeight + 40);
        canvas.drawRect(rightRectF, paintBackground);
    }

    // creates a path to draw corners
    private Path createCornersPath(int left, int top, int right, int bottom, int cornerWidth) {
        Path path = new Path();

        path.moveTo(left, top + cornerWidth);
        path.lineTo(left, top);
        path.lineTo(left + cornerWidth, top);

        path.moveTo(right - cornerWidth, top);
        path.lineTo(right, top);
        path.lineTo(right, top + cornerWidth);

        path.moveTo(left, bottom - cornerWidth);
        path.lineTo(left, bottom);
        path.lineTo(left + cornerWidth, bottom);

        path.moveTo(right - cornerWidth, bottom);
        path.lineTo(right, bottom);
        path.lineTo(right, bottom - cornerWidth);

        return path;
    }
}
