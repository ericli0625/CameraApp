package com.example.erichc_li.cameraapp;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.erichc_li.cameraapp.CameraBase.CameraBase;
import com.example.erichc_li.cameraapp.CameraBase.CameraBaseV1;
import com.example.erichc_li.cameraapp.CameraBase.CameraFactory;
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

    private static final int CAMBASEV1 = 0;
    private static final int CAMBASEV2 = 1;

    private Activity mActivity;
    private Preview mPreview;

    private CameraBase mCameraBase;
    private UI mUI;

    //Component
    private FocusMetering mFocusMetering;

    public Controller(Activity context) {
        mActivity = context;
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
            if (mCameraBase != null) {
                mCameraBase.takePicture();
            }
        }
    };

    private void initCamera() {

        Log.i(TAG, "initCamera...");
        mCameraBase = createCameraBase(CAMBASEV2);
        if (mCameraBase.getCamera() != null) {
            mCameraBase.setPictureSize(ImageFormat.JPEG);
        } else {
            Log.i(TAG, "mCamera is null");
        }

    }

    private CameraBase createCameraBase(int id) {

        CameraBase mCameraBase = null;

        switch (id) {
            case CAMBASEV1:
                mCameraBase = CameraFactory.createCameraBaseV1(mActivity);
                break;
            case CAMBASEV2:
                mCameraBase = CameraFactory.createCameraBaseV2(mActivity);
                break;
            default:
                break;
        }

        return mCameraBase;
    }

    private void initPreview() {

        Log.i(TAG, "initPreview...");

        mPreview = createCameraPreview(PREVIEW_TEXTUREVIEW);

        mPreview.setCamera(mCameraBase);

        mPreview.setPreviewListener(mPreviewListener);

        mUI.addCameraPreview(mPreview.getView());

    }

    private Preview.PreviewListener mPreviewListener = new Preview.PreviewListener() {
        @Override
        public void onPreviewCreated(Object surface) {
            Log.i(TAG, "StartPreview...");
            mCameraBase.startPreview();
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

        if (false) {
            Log.i(TAG, "configComponent...");
            mFocusMetering = new FocusMetering(mActivity, mCameraBase, mUI);
            mUI.setGestureListener(mGestureListener);
            mCameraBase.setTouchEventListener(mTouchFocusListener);
        }

    }

    private CameraBase.TouchFocusListener mTouchFocusListener = new CameraBase.TouchFocusListener() {

        @Override
        public void onTouchFocus(boolean success) {

//            Log.i(TAG, "mTouchFocusListener onTouchFocus() E");
            Log.i(TAG, success ? "聚焦成功..." : "聚焦失敗...");

            //Dismiss FocusView when focused
            if (mUI.getFrameLayout().getChildCount() == 2 && mUI.getTouchEvent() == 1) {
                mUI.getFrameLayout().removeViewAt(1);
            }
            mUI.discreteTouchEvent();
//            Log.i(TAG, "mTouchFocusListener onTouchFocus() X");
        }

    };

    GestureDetector.OnGestureListener mGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent event) {
//            Log.i(TAG, "onDown");
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
//            Log.i(TAG, "onShowPress");

        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
//            Log.i(TAG, "onSingleTapUp");
            if (mUI.getFrameLayout().getChildCount() == 2) {
                mUI.getFrameLayout().removeViewAt(1);
            }
            mCameraBase.cancelAutoFocus();
            mFocusMetering.handleFocus(event);
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Log.i(TAG, "onScroll");
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
//            Log.i(TAG, "onLongPress");
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            Log.i(TAG, "onFling");
            return false;
        }
    };

    public void executeResume() {
        initUI();
        initCamera();
        initPreview();
        configComponent();
    }

    public void executePause() {

        if (mPreview != null) {
            mPreview.setCamera(null);
            mPreview.onPauseTasks();
        }

        if (mCameraBase != null) {
            mCameraBase.setSurface(null);
            mCameraBase.onPauseTasks();
        }

        if (mUI != null)
            mUI.onPauseTasks();

    }

    public void executeStop() {


    }

    public void executeDestroy() {

        if (mUI != null)
            mUI.onDestroyTasks();

        mPreview = null;
        mCameraBase = null;
        mUI = null;
        mFocusMetering = null;

    }

}
