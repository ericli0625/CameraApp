package com.example.erichc_li.cameraapp.Preview;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.erichc_li.cameraapp.CameraBase.CameraManager;
import com.example.erichc_li.cameraapp.ViewProcessing.SurfaceViewProcessing;
import com.example.erichc_li.cameraapp.ViewProcessing.ViewProcessing;

public class SurfaceViewPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = SurfaceViewPreview.class.getName();

    private SurfaceHolder previewHolder = null;

    private final CameraManager mCameraManager;
    private final ViewProcessing mViewProcessing;

    public SurfaceViewPreview(Context context, CameraManager camera) {
        super(context);
        mCameraManager = camera;
        previewHolder = getHolder();
        previewHolder.addCallback(this);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mViewProcessing = new SurfaceViewProcessing(mCameraManager);
        mCameraManager.ShowWhatView("SurfaceView");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated...");
        mViewProcessing.viewCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged...");
        mViewProcessing.viewChanged(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed...");
        mViewProcessing.viewDestroyed();
    }
}
