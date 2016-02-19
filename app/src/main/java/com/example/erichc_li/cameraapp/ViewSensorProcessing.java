package com.example.erichc_li.cameraapp;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.erichc_li.cameraapp.CameraBase.CameraSensorManager;

import java.io.IOException;
import java.util.List;

public class ViewSensorProcessing extends ViewProcessing {

    private static final String TAG = ViewSensorProcessing.class.getName();
    private CameraSensorManager mCameraManager;

    public ViewSensorProcessing(CameraSensorManager cameraManager) {
        super(cameraManager);
        mCameraManager = cameraManager;
    }

    public void viewCreated(int value, Object preview){
        if (mCameraManager.getCamera() != null) {
            try {
                Log.i(TAG, "StartPreview...");

                Camera.Parameters parameters = mCameraManager.getCameraParameters();

                mCameraManager.setCameraDisplayOrientation(0, parameters);

                List<Camera.Size> psSize = parameters.getSupportedPictureSizes();

                parameters.setPictureSize(psSize.get(0).width, psSize.get(0).height);

                mCameraManager.setCameraParameters(parameters);

                mCameraManager.setPreviewTexture((SurfaceTexture) preview);

                mCameraManager.startPreview();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            Log.i(TAG, "surfaceCreated, mCamera is null");
        }

    }

    public void viewChanged(int value, Object preview){

        try {
            mCameraManager.stopPreview();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

        try {

            mCameraManager.setPreviewTexture((SurfaceTexture) preview);

            mCameraManager.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void viewDestroyed(int value){
        mCameraManager.stopPreview();
        mCameraManager.releaseCamera();
        mCameraManager.unregisterSensorListener();
    }
}
