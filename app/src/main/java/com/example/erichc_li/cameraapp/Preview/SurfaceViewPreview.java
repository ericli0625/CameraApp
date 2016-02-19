package com.example.erichc_li.cameraapp.Preview;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.erichc_li.cameraapp.CameraBase.CameraManager;
import com.example.erichc_li.cameraapp.ViewProcessing;

public class SurfaceViewPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = SurfaceViewPreview.class.getName();

    private SurfaceHolder previewHolder = null;

    private final CameraManager mCameraManager;
    private final ViewProcessing mViewProcessing;
    private int viewCategoryNum = 0;

    public SurfaceViewPreview(Context context, CameraManager camera, int value) {
        super(context);
        mCameraManager = camera;
        previewHolder = getHolder();
        previewHolder.addCallback(this);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mViewProcessing = new ViewProcessing(mCameraManager);
        viewCategoryNum = value;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated...");
        mViewProcessing.viewCreated(viewCategoryNum, holder);
        mCameraManager.ShowWhatView("SurfaceView");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged...");
        mViewProcessing.viewChanged(viewCategoryNum,holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed...");
        mViewProcessing.viewDestroyed();
    }
}
