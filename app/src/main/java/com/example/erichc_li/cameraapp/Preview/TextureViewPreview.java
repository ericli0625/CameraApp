package com.example.erichc_li.cameraapp.Preview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;

import com.example.erichc_li.cameraapp.CameraBase.CameraManager;
import com.example.erichc_li.cameraapp.ViewProcessing;


public class TextureViewPreview extends TextureView implements TextureView.SurfaceTextureListener {

    private static final String TAG = TextureViewPreview.class.getName();

    private final CameraManager mCameraManager;
    private final ViewProcessing mViewProcessing;
    private int viewCategoryNum = 0;

    public TextureViewPreview(Context context, CameraManager camera, int value) {
        super(context);
        mCameraManager = camera;
        this.setSurfaceTextureListener(this);
        mViewProcessing = new ViewProcessing(mCameraManager);
        viewCategoryNum = value;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureAvailable...");
        mViewProcessing.viewCreated(viewCategoryNum,surface);
        mCameraManager.ShowWhatView("TextureView");
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureSizeChanged...");
        mViewProcessing.viewCreated(viewCategoryNum,surface);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.i(TAG, "onSurfaceTextureDestroyed...");
        mViewProcessing.viewDestroyed();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

}
