package com.example.erichc_li.cameraapp;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.erichc_li.cameraapp.CameraBase.CameraManager;
import com.example.erichc_li.cameraapp.Component.FocusMeter.FocusMetering;
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
    private UI mUI;

    //Component
    private FocusMetering mFocusMetering;

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

        mPreview = createCameraPreview(PREVIEW_TEXTUREVIEW);

        mPreview.setCamera(mCameraManager);

        mPreview.setPreviewListener(mPreviewListener);

        mUI.addCameraPreview(mPreview.getView());

    }

    private Preview.PreviewListener mPreviewListener = new Preview.PreviewListener() {
        @Override
        public void onPreviewCreated(Object surface) {
            Log.i(TAG, "StartPreview...");
            mCameraManager.startPreview();
        }
    };

    private Preview createCameraPreview(int id) {

        Preview preview = null;

        switch (id) {
            case PREVIEW_TEXTUREVIEW:
                preview = PreviewFactory.createTextureView(mActivity);
                break;
            case PREVIEW_SURFACEVIEW:
                preview = PreviewFactory.createSurfaceView(mActivity);
                break;
            case PREVIEW_GLSURFACEVIEW:
                preview = PreviewFactory.createGLSurfaceView(mActivity);
                break;
            default:
                break;
        }

        return preview;
    }

    public void configComponent() {

        Log.i(TAG, "configComponent...");
        mFocusMetering = new FocusMetering(mActivity, mCameraManager, mUI);
        mUI.setTouchEventListener(mTouchEventListener);
        mCameraManager.setTouchEventListener(mTouchFocusListener);

    }

    private CameraManager.TouchFocusListener mTouchFocusListener = new CameraManager.TouchFocusListener(){

        @Override
        public void onTouchFocus(boolean success) {

            Log.i(TAG, "mTouchFocusListener onTouchFocus() E");
            if (success) {
                Log.i(TAG, "聚焦成功...");
            } else {
                Log.i(TAG, "聚焦失敗...");
            }

            Log.i(TAG, "onTouchFocus getChildCount() = "+mUI.getFrameLayout().getChildCount() + " E");

            //Dismiss FocusView when focused
            if (mUI.getFrameLayout().getChildCount() == 2 && mCameraManager.getTouchEvent() == 1) {
                mUI.getFrameLayout().removeViewAt(1);
            }

            Log.i(TAG, "onTouchFocus getChildCount() = "+mUI.getFrameLayout().getChildCount() + " X");
            Log.i(TAG, "mTouchFocusListener onTouchFocus() X");
        }

    };

    private UI.TouchEventListener mTouchEventListener = new UI.TouchEventListener() {

        @Override
        public void onTouchEvent(View v, MotionEvent event) {

            Camera.Parameters parameters = mCameraManager.getCameraParameters();
            int action = event.getAction() & MotionEvent.ACTION_MASK;

            if (event.getPointerCount() > 1) {
                if (action == MotionEvent.ACTION_POINTER_DOWN) {
//                    Log.i(TAG, "ACTION_POINTER_DOWN");
                    mFocusMetering.setOldDis(mFocusMetering.getFingerSpacing(event));
                } else if (action == MotionEvent.ACTION_MOVE && parameters.isZoomSupported()) {
//                    Log.i(TAG, "ACTION_MOVE");
                    mCameraManager.cancelAutoFocus();
                    mFocusMetering.handleZoom(event);
                }
            } else {
                if (action == MotionEvent.ACTION_UP) {
//                    Log.i(TAG, "ACTION_UP");
                    mCameraManager.cancelAutoFocus();
                    mFocusMetering.handleFocus(event, action);
                }
            }
        }
    };

    public void executeResume() {
        initUI();
        initCamera();
        initPreview();
        configComponent();
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
        mFocusMetering = null;

    }

}
