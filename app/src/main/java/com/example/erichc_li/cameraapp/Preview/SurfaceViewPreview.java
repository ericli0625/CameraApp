package com.example.erichc_li.cameraapp.Preview;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class SurfaceViewPreview extends Preview implements SurfaceHolder.Callback {

    private static final String TAG = SurfaceViewPreview.class.getName();

    private SurfaceView mSurfaceView = null;
    private SurfaceHolder previewHolder = null;

    public SurfaceViewPreview(Context context) {
        super(context);
        mSurfaceView = new SurfaceView(context);
        previewHolder = mSurfaceView.getHolder();
        previewHolder.addCallback(this);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        ShowWhatView("SurfaceViewPreview");
    }

    @Override
    public View getView() {
        return mSurfaceView;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated...");
        if(holder!=null && !holder.equals("")) {
            previewHolder = holder;
            setSurface(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged...");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed...");
    }
}
