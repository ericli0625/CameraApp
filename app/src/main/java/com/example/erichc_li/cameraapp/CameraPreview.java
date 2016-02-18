package com.example.erichc_li.cameraapp;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraPreview extends TextureView implements TextureView.SurfaceTextureListener, SensorEventListener {

    private static final String TAG = CameraPreview.class.getName();
    private SensorManager mSensorManager;
    private Context mContext;
    private CameraManager mCameraManager;
    public boolean inPreview = false;
    private SurfaceTexture mSurface;
    private int maxZoomLevel;
    private float oldDis;
    private boolean zoom = false;
    private Sensor aSensor;

    Handler handler = new Handler();
    AutoFocusThread autofocusThread = new AutoFocusThread();
    private boolean mInitialized;
    private float mLastX,mLastY,mLastZ;

    public CameraPreview(Context context,CameraManager camera) {
        super(context);
        mContext = context;
        mCameraManager = camera;
        this.setSurfaceTextureListener(this);
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        aSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureAvailable...");

        mSurface = surface;

        if (mCameraManager.getCamera() != null) {
            try {
                Log.i(TAG, "StartPreview...");

                Camera.Parameters parameters = mCameraManager.getCameraParameters();

                mCameraManager.setCameraDisplayOrientation(0, parameters);

                //handler.post(autofocusThread);

                List<Camera.Size> psSize = parameters.getSupportedPictureSizes();
/*
                for (int i = 0; i < psSize.size(); i++) {
                    Log.i(TAG, String.valueOf(psSize.get(i).width) + "," + String.valueOf(psSize.get(i).height));
                }
*/
                parameters.setPictureSize(psSize.get(0).width, psSize.get(0).height);

                mCameraManager.setCameraParameters(parameters);

                mCameraManager.setPreviewTexture(mSurface);
                mCameraManager.startPreview();

                inPreview = true;

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            Log.i(TAG, "onSurfaceTextureAvailable, mCamera is null");
        }
        mCameraManager.ShowWhatView("CameraPreview");
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureSizeChanged...");

        // stop preview before making changes

        try {
            mCameraManager.stopPreview();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

        try {
            mCameraManager.setPreviewTexture(mSurface);
            mCameraManager.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());

        }
        Log.i(TAG, "getFocusMode() = " + mCameraManager.getCameraParameters().getFocusMode());

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.i(TAG, "onSurfaceTextureDestroyed...");
        mCameraManager.stopPreview();
        //handler.removeCallbacks(autofocusThread);
        mSensorManager.unregisterListener(this);
        inPreview = false;
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //Log.i(TAG, "onSurfaceTextureUpdated...");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.i(TAG, "onTouchEvent()...");
        Camera.Parameters parameters = mCameraManager.getCameraParameters();
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        //MainActivity.mFrameLayout2.removeAllViews();


        if (event.getPointerCount() > 1) {
                if (action == MotionEvent.ACTION_POINTER_DOWN) {
                        oldDis = getFingerSpacing(event);
                } else if (action == MotionEvent.ACTION_MOVE && parameters.isZoomSupported()) {
                        mCameraManager.cancelAutoFocus();
                        handleZoom(event);
                }
        } else {
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                handleFocus(event, action);
            }
        }

        return true;
    }

    private void handleZoom(MotionEvent event) {

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
//                MainActivity.mFrameLayout2.addView(mSquareView);
            } else if (action == MotionEvent.ACTION_DOWN && zoom == true){
                zoom = false;
            }

            mCameraManager.setCameraParameters(params);
            //mCameraManager.autoFocus();

        }

    }

    @Override
    public void onSensorChanged(SensorEvent arg0) {
        // TODO Auto-generated method stub

        float x = arg0.values[0];
        float y = arg0.values[1];
        float z = arg0.values[2];

        if (!mInitialized){
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        }

        float deltaX  = Math.abs(mLastX - x);
        float deltaY = Math.abs(mLastY - y);
        float deltaZ = Math.abs(mLastZ - z);

        if(deltaX > 1 || deltaY > 1 || deltaZ > 1){
            //Log.i(TAG, "deltaX = "+deltaX+", deltaY = "+deltaY+", deltaZ = "+deltaZ);
            //Log.i(TAG, "onSensorChanged 聚焦成功...");
            //mCameraManager.autoFocus();
        }

        mLastX = x;
        mLastY = y;
        mLastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class AutoFocusThread extends Thread{
        public void run(){
            Log.i(TAG, "AutoFocusThread 聚焦成功...");
            mCameraManager.autoFocus();
            handler.postDelayed(autofocusThread,2500);
        }
    }

    private Rect calculateTapArea(float x, float y, float coefficient) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int centerX = (int) ( ((x / this.getWidth()) * 2000) - 1000);
        int centerY = (int) ( ((y / this.getHeight()) * 2000) - 1000);

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
    private float getFingerSpacing(MotionEvent event) {
        //Log.i(TAG, "event.getX(0) = "+event.getX(0)+", event.getX(1) = "+event.getX(1));
        //Log.i(TAG, "event.getY(0) = "+event.getY(0)+", event.getY(1) = "+event.getY(1));
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

}
