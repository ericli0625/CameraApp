package com.example.erichc_li.cameraapp.CameraBase;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CameraSensorManager extends CameraManager implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor aSensor;
    private boolean mInitialized;
    private float mLastX, mLastY, mLastZ;

    public CameraSensorManager(Context mContext, Camera mCamera) {
        super(mContext, mCamera);
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        aSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        registerSensorListener();
    }

    public void registerSensorListener() {
        mSensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void unregisterSensorListener() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent arg0) {

        float x = arg0.values[0];
        float y = arg0.values[1];
        float z = arg0.values[2];

        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        }

        float deltaX = Math.abs(mLastX - x);
        float deltaY = Math.abs(mLastY - y);
        float deltaZ = Math.abs(mLastZ - z);

        if (deltaX > 1 || deltaY > 1 || deltaZ > 1) {
            //Log.i(TAG, "deltaX = "+deltaX+", deltaY = "+deltaY+", deltaZ = "+deltaZ);
            //Log.i(TAG, "onSensorChanged 聚焦成功...");
            this.autoFocus();
        }

        mLastX = x;
        mLastY = y;
        mLastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
