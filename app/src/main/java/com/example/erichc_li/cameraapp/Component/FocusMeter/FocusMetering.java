package com.example.erichc_li.cameraapp.Component.FocusMeter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.View;

import com.example.erichc_li.cameraapp.CameraBase.CameraBase;
import com.example.erichc_li.cameraapp.UI.UI;

import java.util.ArrayList;
import java.util.List;

public class FocusMetering {

    private static final String TAG = FocusMetering.class.getName();

    private Context mContext;
    private UI mUI;
    private CameraBase mCameraBase;

    public class FocusView extends View {

        private float mX, mY;

        public FocusView(Context context, float x, float y) {
            super(context);
            mX = x;
            mY = y;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint p = new Paint();
            p.setColor(Color.YELLOW);
            p.setAntiAlias(true);
            p.setStrokeWidth(7);
            p.setStyle(Paint.Style.STROKE);

            canvas.drawCircle(mX, mY, 80, p);

        }
    }

    public FocusMetering(Context context, CameraBase camera, UI ui) {
        mContext = context;
        mCameraBase = camera;
        mUI = ui;
    }

    public void handleFocus(MotionEvent event) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        final float x = event.getX(pointerIndex);
        final float y = event.getY(pointerIndex);

        Camera.Parameters params = (Camera.Parameters) mCameraBase.getCameraParameters();

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {

            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            Rect focusRect = calculateTapArea(x, y, 1f);
            Rect meteringRect = calculateTapArea(x, y, 1.5f);

            if (params.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
                focusAreas.add(new Camera.Area(focusRect, 1000));

                params.setFocusAreas(focusAreas);
            }

            if (params.getMaxNumMeteringAreas() > 0) {
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(meteringRect, 1000));

                params.setMeteringAreas(meteringAreas);
            }

            FocusView mFocusView = new FocusView(mContext, x, y);
            mUI.getFrameLayout().addView(mFocusView, 1);

            mCameraBase.setCameraParameters(params);
            mCameraBase.autoFocus();
            mUI.increaseTouchEvent();

        }

    }

    private Rect calculateTapArea(float x, float y, float coefficient) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int centerX = (int) (((x / ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth()) * 2000) - 1000);
        int centerY = (int) (((y / ((Activity) mContext).getWindowManager().getDefaultDisplay().getHeight()) * 2000) - 1000);

        //Log.i(TAG, "X = " +x+", Y = "+y);
        //Log.i(TAG, "(this.getWidth()) = " +  (this.getWidth()) + ", (this.getHeight()) = " + (this.getHeight()));
        //Log.i(TAG, "(x / this.getWidth()) = " +  (x / this.getWidth()) + ", (y / this.getHeight()) = " + (y / this.getHeight()));

        //Log.i(TAG, "centerX = " +centerX+", centerY = "+centerY);

        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int right = clamp(left + areaSize, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        int bottom = clamp(top + areaSize, -1000, 1000);

        //Log.i(TAG, "left = " +left + ", top = " +top+ ", right = "+right +", bottom = "+bottom);

        return new Rect(left, top, right, bottom);
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

}
