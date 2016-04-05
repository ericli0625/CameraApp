package com.example.erichc_li.cameraapp;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.erichc_li.cameraapp.CameraBase.CameraManager;
import com.example.erichc_li.cameraapp.Preview.Preview;
import com.example.erichc_li.cameraapp.Preview.PreviewFactory;
import com.example.erichc_li.cameraapp.UI.SampleUI;
import com.example.erichc_li.cameraapp.UI.UI;


public class Controller {

    private static final String TAG = Controller.class.getName();

    private static final int PREVIEW_TEXTUREVIEW = R.id.View1;
    private static final int PREVIEW_SURFACEVIEW = R.id.View2;
    private static final int PREVIEW_GLSURFACEVIEW = R.id.View3;

    private Activity mActivity;
    private Preview mPreview;

    private CameraManager mCameraManager;
    private Camera mCamera;
    private SampleUI mUI;

    public Controller(Activity context) {
        mActivity = context;
        openCamera();
    }

    private void openCamera() {
        mCamera = getCameraInstance();
    }

    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return c;
    }

    private void initUI() {
        Log.i(TAG, "initUI...");
        mUI = new SampleUI(mActivity);
        mUI.init();
        mUI.setCaptureButtonListener(mCaptureButtonListener);
    }

    private UI.CaptureButtonListener mCaptureButtonListener = new UI.CaptureButtonListener() {
        @Override
        public void onCaptureButtonClick() {
            if (mCameraManager != null) {
                mCameraManager.takePicture();
            }
        }
    };

    private void initCamera() {

        Log.i(TAG, "initCamera...");
        mCameraManager = new CameraManager(mActivity, mCamera);
        if (mCameraManager.getCamera() != null) {
            mCameraManager.setPictureSize();
        } else {
            Log.i(TAG, "mCamera is null");
        }

    }

    private void initPreview() {

        Log.i(TAG, "initPreview...");
        createCameraPreview(PREVIEW_TEXTUREVIEW);

        mUI.addCameraPreview(mPreview.getView());

        if (!mCameraManager.equals("") && mCameraManager != null) {
            mPreview.setCamera(mCameraManager);
        } else {
            Log.i(TAG, "mCameraManager is null");
        }

        mPreview.setPreviewListener(mPreviewListener);

    }

    private Preview.PreviewListener mPreviewListener = new Preview.PreviewListener() {
        @Override
        public void onPreviewCreated(Object surface) {
            Log.i(TAG, "StartPreview...");
            mCameraManager.startPreview();
        }
    };

    private void createCameraPreview(int id) {

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

    public void executeResume() {
        initUI();
        initCamera();
        initPreview();
    }

    public void executePause() {

        if (mPreview != null)
            mPreview.onPauseTasks();

        if (mCameraManager != null)
            mCameraManager.onPauseTasks();

        if (mUI != null)
            mUI.onPauseTasks();

    }

    public void executeStop() {


    }

    public void executeDestroy() {

        if (mPreview != null)
            mPreview.onDestroyTasks();

        if (mCameraManager != null)
            mCameraManager.onDestroyTasks();

        if (mUI != null)
            mUI.onDestroyTasks();

        mPreview = null;
        mCameraManager = null;
        mUI = null;

    }

}
