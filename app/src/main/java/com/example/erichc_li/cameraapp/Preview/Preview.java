package com.example.erichc_li.cameraapp.Preview;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.erichc_li.cameraapp.CameraBase.CameraManager;

public abstract class Preview {

    private Context mContext;
    private CameraManager mCameraManager;
    private PreviewListener mPreviewListener;

    protected Preview(Context context) {
        mContext = context;
    }

    public abstract View getView();

    public void setCamera(CameraManager camera) {
        mCameraManager = camera;
    }

    public interface PreviewListener {
        public abstract void onPreviewCreated(Object surface);
    }

    public void setPreviewListener(PreviewListener previewlistener) {
        mPreviewListener = previewlistener;
    }

    protected void setSurface(Object surface) {
        mCameraManager.setSurface(surface);
        mPreviewListener.onPreviewCreated(surface);
    }

    protected void ShowWhatView(String name) {
        Toast.makeText(mContext.getApplicationContext(), name, Toast.LENGTH_SHORT).show();
    }

    public void onPauseTasks() {
        mContext = null;
    }

    public void onDestroyTasks() {
        mContext = null;
        mCameraManager = null;
    }

}
