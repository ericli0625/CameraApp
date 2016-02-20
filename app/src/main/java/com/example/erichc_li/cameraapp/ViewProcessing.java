package com.example.erichc_li.cameraapp;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.erichc_li.cameraapp.CameraBase.CameraManager;

import java.io.IOException;
import java.util.List;

public class ViewProcessing {

    private static final String TAG = ViewProcessing.class.getName();
    private CameraManager mCameraManager;

    public ViewProcessing(CameraManager cameraManager){
        mCameraManager = cameraManager;
    }

    public void viewCreated(int value, Object preview){
        Log.i(TAG, "viewCreated...");
        if (mCameraManager.getCamera() != null) {
            try {
                Log.i(TAG, "StartPreview...");

                Camera.Parameters parameters = mCameraManager.getCameraParameters();

                mCameraManager.setCameraDisplayOrientation(0, parameters);

                List<Camera.Size> psSize = parameters.getSupportedPictureSizes();
                parameters.setPictureSize(psSize.get(0).width, psSize.get(0).height);

                mCameraManager.setCameraParameters(parameters);

                switch (value){
                    case R.id.View1:
                    case R.id.View3:
                    case R.id.View4:
                        mCameraManager.setPreviewTexture((SurfaceTexture) preview);
                        break;
                    case R.id.View2:
                        mCameraManager.setPreviewDisplay((SurfaceHolder) preview);
                        break;
                }

                mCameraManager.startPreview();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            Log.i(TAG, "surfaceCreated, mCamera is null");
        }

    }

    public void viewChanged(int value, Object preview){
        Log.i(TAG, "viewChanged...");
        try {
            mCameraManager.stopPreview();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

        try {
            switch (value){
                case R.id.View1:
                case R.id.View3:
                case R.id.View4:
                    mCameraManager.setPreviewTexture((SurfaceTexture) preview);
                    break;
                case R.id.View2:
                    mCameraManager.setPreviewDisplay((SurfaceHolder) preview);
                    break;
            }
            mCameraManager.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void viewDestroyed(int value){
        Log.i(TAG, "viewDestroyed...");
        mCameraManager.stopPreview();
    }

}
