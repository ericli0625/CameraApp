package com.example.erichc_li.cameraapp.Preview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

public class TextureViewPreview extends Preview implements TextureView.SurfaceTextureListener {

    private static final String TAG = TextureViewPreview.class.getName();

    private TextureView mTextureView = null;
    private SurfaceTexture mSurfaceTexture = null;

    public TextureViewPreview(Context context) {
        super(context);
        mTextureView = new TextureView(context);
        mTextureView.setSurfaceTextureListener(this);
        ShowWhatView("TextureViewPreview");
    }

    @Override
    public View getView() {
        return mTextureView;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureAvailable...");
        mSurfaceTexture = surface;
        setSurface(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureSizeChanged...");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.i(TAG, "onSurfaceTextureDestroyed...");
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

}
