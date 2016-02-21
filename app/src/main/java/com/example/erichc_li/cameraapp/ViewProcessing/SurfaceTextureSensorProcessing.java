package com.example.erichc_li.cameraapp.ViewProcessing;

import android.util.Log;

import com.example.erichc_li.cameraapp.CameraBase.CameraSensorManager;


public class SurfaceTextureSensorProcessing extends SurfaceTextureProcessing {

    private static final String TAG = SurfaceTextureSensorProcessing.class.getName();
    private CameraSensorManager mCameraManager;

    public SurfaceTextureSensorProcessing(CameraSensorManager cameraManager) {
        super(cameraManager);
        mCameraManager = cameraManager;
    }

    @Override
    public void viewDestroyed(){
        Log.i(TAG, "viewDestroyed...SurfaceTextureSensorProcessing");
        mCameraManager.stopPreview();
        mCameraManager.unregisterSensorListener();
    }

}
