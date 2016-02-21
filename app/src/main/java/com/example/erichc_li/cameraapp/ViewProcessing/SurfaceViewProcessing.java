package com.example.erichc_li.cameraapp.ViewProcessing;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.erichc_li.cameraapp.CameraBase.CameraManager;

import java.io.IOException;
import java.util.List;

public class SurfaceViewProcessing implements ViewProcessing {

    private static final String TAG = SurfaceViewProcessing.class.getName();
    private CameraManager mCameraManager;

    public SurfaceViewProcessing(CameraManager cameraManager){
        mCameraManager = cameraManager;
    }

    @Override
    public void viewCreated(Object preview){
        Log.i(TAG, "viewCreated...SurfaceViewProcessing");
        if (mCameraManager.getCamera() != null) {
            try {
                Log.i(TAG, "StartPreview...");

                Camera.Parameters parameters = mCameraManager.getCameraParameters();

                mCameraManager.setCameraDisplayOrientation(0, parameters);

                List<Camera.Size> psSize = parameters.getSupportedPictureSizes();
                parameters.setPictureSize(psSize.get(0).width, psSize.get(0).height);

                mCameraManager.setCameraParameters(parameters);

                mCameraManager.setPreviewDisplay((SurfaceHolder) preview);

                mCameraManager.startPreview();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            Log.i(TAG, "surfaceCreated, mCamera is null");
        }

    }

    @Override
    public void viewChanged(Object preview){
        Log.i(TAG, "viewChanged...SurfaceViewProcessing");
        try {
            mCameraManager.stopPreview();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

        try {
            mCameraManager.setPreviewDisplay((SurfaceHolder) preview);
            mCameraManager.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void viewDestroyed(){
        Log.i(TAG, "viewDestroyed...SurfaceViewProcessing");
        mCameraManager.stopPreview();
    }

}
