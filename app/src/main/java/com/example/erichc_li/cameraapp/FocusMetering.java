package com.example.erichc_li.cameraapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;

import com.example.erichc_li.cameraapp.CameraBase.CameraSensorManager;

import java.util.ArrayList;
import java.util.List;

public class FocusMetering {

    private static final String TAG = FocusMetering.class.getName();

    private final Context mContext;
    private int maxZoomLevel;
    public float oldDis;
    private boolean zoom = false;

    private CameraSensorManager mCameraManager;

    public FocusMetering(Context context, CameraSensorManager camera){
        mContext = context;
        mCameraManager = camera;
    }

    public void handleZoom(MotionEvent event) {

        Camera.Parameters params = mCameraManager.getCameraParameters();
        maxZoomLevel = params.getMaxZoom();
        int zoomValue = params.getZoom();
        float newDis = getFingerSpacing(event);
        if (newDis > oldDis) {
            //zoom in
            Log.i(TAG, "zoom in...");
            if (zoomValue < maxZoomLevel) {
                zoomValue++;
            }
        } else if (newDis < oldDis) {
            //zoom out
            Log.i(TAG, "zoom out...");
            if (zoomValue > 0) {
                zoomValue--;
            }
        }

        zoom = true;
        oldDis = newDis;
        params.setZoom(zoomValue);
        mCameraManager.setCameraParameters(params);
    }

    public void handleFocus(MotionEvent event,int action) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        final float x = event.getX(pointerIndex);
        final float y = event.getY(pointerIndex);
        //Log.i(TAG, "x = " + x + ", y = " + y);

        Camera.Parameters params = mCameraManager.getCameraParameters();

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

            if (params.getMaxNumMeteringAreas() > 0){
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(meteringRect, 1000));

                params.setMeteringAreas(meteringAreas);
            }

            if (action == MotionEvent.ACTION_UP && zoom != true) {
                SquareView mSquareView = new SquareView(mContext, x, y);
                MainActivity.mFrameLayout2.addView(mSquareView);
            } else if (action == MotionEvent.ACTION_DOWN && zoom == true){
                zoom = false;
            }

            mCameraManager.setCameraParameters(params);
            mCameraManager.autoFocus();

        }

    }

    private Rect calculateTapArea(float x, float y, float coefficient) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int centerX = (int) ( ((x / ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth()) * 2000) - 1000);
        int centerY = (int) ( ((y / ((Activity) mContext).getWindowManager().getDefaultDisplay().getHeight()) * 2000) - 1000);

        //Log.i(TAG, "X = " +x+", Y = "+y);
        //Log.i(TAG, "(this.getWidth()) = " +  (this.getWidth()) + ", (this.getHeight()) = " + (this.getHeight()));
        //Log.i(TAG, "(x / this.getWidth()) = " +  (x / this.getWidth()) + ", (y / this.getHeight()) = " + (y / this.getHeight()));

        //Log.i(TAG, "centerX = " +centerX+", centerY = "+centerY);

        if (centerX < 1 && centerY < 1){
            centerY *= (-1);
            //Log.i(TAG, "1 centerX = " +centerX+", centerY = "+centerY);
        } else if (centerX > 1 && centerY < 1) {
            centerX *= (-1);
            //Log.i(TAG, "2 centerX = " +centerX+", centerY = "+centerY);
        } else if (centerX > 1 && centerY > 1) {
            centerY *= (-1);
            //Log.i(TAG, "3 centerX = " +centerX+", centerY = "+centerY);
        } else if (centerX < 1 && centerY > 1) {
            centerX *= (-1);
            //Log.i(TAG, "4 centerX = " +centerX+", centerY = "+centerY);
        }

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

    /** Determine the space between the first two fingers */
    public static float getFingerSpacing(MotionEvent event) {
        //Log.i(TAG, "event.getX(0) = "+event.getX(0)+", event.getX(1) = "+event.getX(1));
        //Log.i(TAG, "event.getY(0) = "+event.getY(0)+", event.getY(1) = "+event.getY(1));
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

}
