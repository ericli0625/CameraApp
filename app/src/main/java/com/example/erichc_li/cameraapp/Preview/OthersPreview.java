package com.example.erichc_li.cameraapp.Preview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;

import com.example.erichc_li.cameraapp.CameraBase.CameraSensorManager;
import com.example.erichc_li.cameraapp.FocusMetering;
import com.example.erichc_li.cameraapp.MainActivity;
import com.example.erichc_li.cameraapp.ViewProcessing.SurfaceTextureSensorProcessing;
import com.example.erichc_li.cameraapp.ViewProcessing.ViewProcessing;


public class OthersPreview extends TextureView implements TextureView.SurfaceTextureListener {

    private static final String TAG = OthersPreview.class.getName();

    private CameraSensorManager mCameraManager;
    private final ViewProcessing mViewProcessing;

    private FocusMetering mFocusMetering;

    public OthersPreview(Context context, CameraSensorManager camera) {
        super(context);
        mCameraManager = camera;
        mFocusMetering = new FocusMetering(context,mCameraManager);
        this.setSurfaceTextureListener(this);
        mViewProcessing = new SurfaceTextureSensorProcessing(mCameraManager);
        mCameraManager.ShowWhatView("OthersPreview");
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureAvailable...");
        mViewProcessing.viewCreated(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureSizeChanged...");
        mViewProcessing.viewChanged(surface);
        Log.i(TAG, "getFocusMode() = " + mCameraManager.getCameraParameters().getFocusMode());

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.i(TAG, "onSurfaceTextureDestroyed...");
        mViewProcessing.viewDestroyed();
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
        MainActivity.mFrameLayout2.removeAllViews();

        if (event.getPointerCount() > 1) {
                if (action == MotionEvent.ACTION_POINTER_DOWN) {
                        mFocusMetering.oldDis = mFocusMetering.getFingerSpacing(event);
                } else if (action == MotionEvent.ACTION_MOVE && parameters.isZoomSupported()) {
                        mCameraManager.cancelAutoFocus();
                        mFocusMetering.handleZoom(event);
                }
        } else {
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                mFocusMetering.handleFocus(event, action);
            }
        }

        return true;
    }

}
