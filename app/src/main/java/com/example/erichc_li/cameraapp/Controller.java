package com.example.erichc_li.cameraapp;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.erichc_li.cameraapp.CameraBase.CameraManager;
import com.example.erichc_li.cameraapp.Preview.Preview;
import com.example.erichc_li.cameraapp.Preview.PreviewFactory;


public class Controller {

    private static final String TAG = Controller.class.getName();

    private static final int PREVIEW_TEXTUREVIEW = R.id.View1;
    private static final int PREVIEW_SURFACEVIEW = R.id.View2;
    private static final int PREVIEW_GLSURFACEVIEW = R.id.View3;

    private Activity mActivity;
    private FrameLayout mFrameLayout;
    public static FrameLayout mFrameLayout2;
    private Preview mPreview;

    private CameraManager mCameraManager;
    private Camera mCamera;

    public Controller(Activity context) {
        mActivity = context;
        openCamera();
    }

    public void openCamera() {
        mCamera = getCameraInstance();
    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return c;
    }

    public void initCamera() {

        Log.i(TAG, "initCamera...");
        mCameraManager = new CameraManager(mActivity, mCamera);
        if (mCameraManager.getCamera() != null) {
                mCameraManager.setPictureSize();
        } else {
            Log.i(TAG, "mCamera is null");
        }

    }

    public void initPreview() {

        Log.i(TAG, "initPreview...");
        mFrameLayout = (FrameLayout) mActivity.findViewById(R.id.camera_textureview);
        mFrameLayout2 = (FrameLayout) mActivity.findViewById(R.id.camera_textureview2);

        selectCameraPreview(PREVIEW_SURFACEVIEW);

        mFrameLayout.addView(mPreview.getView());

        mPreview.setCamera(mCameraManager);

        mPreview.setPreviewListener(new Preview.PreviewListener() {
            @Override
            public void onPreviewCreated(Object surface) {
                Log.i(TAG, "StartPreview...");
                mCameraManager.startPreview();
            }
        });

    }

    public void selectCameraPreview(int id) {

        Log.i(TAG, "configCameraView...");
        switch (id) {
            case PREVIEW_TEXTUREVIEW:
                mPreview = PreviewFactory.createTextureView(mActivity);
                break;
            case PREVIEW_SURFACEVIEW:
                mPreview = PreviewFactory.createSurfaceView(mActivity);
                break;
            case PREVIEW_GLSURFACEVIEW:
                mPreview = PreviewFactory.createGLSurfaceView(mActivity);
                break;
            default:
                break;
        }

    }

    public void executeResume(){
        initCamera();
        initPreview();
    }

    public void executePause(){

        if (mPreview != null)
            mPreview.onPauseTasks();

        mCameraManager.setSurface(null);
        mPreview.setCamera(null);

        if (mCameraManager != null)
            mCameraManager.onPauseTasks();

        mCameraManager = null;
    }

    public void executeStop(){



    }

    public void executeDestroy(){

        if (mPreview != null)
            mPreview.onDestroyTasks();

        if (mCameraManager != null)
            mCameraManager.onDestroyTasks();

        mPreview = null;
        mCameraManager = null;
    }

    public void removeAllViews() {
        mFrameLayout.removeAllViews();
    }

}
