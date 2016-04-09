package com.example.erichc_li.cameraapp.Preview;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.erichc_li.cameraapp.CameraBase.CameraBase;

public abstract class Preview {

    private Context mContext;
    private CameraBase mCameraBase;
    private PreviewListener mPreviewListener;

    protected Preview(Context context) {
        mContext = context;
    }

    public abstract View getView();

    public void setCamera(CameraBase camera) {
        mCameraBase = camera;
    }

    public interface PreviewListener {
        public abstract void onPreviewCreated(Object surface);
    }

    public void setPreviewListener(PreviewListener previewlistener) {
        mPreviewListener = previewlistener;
    }

    protected void setSurface(Object surface) {
        mCameraBase.setSurface(surface);
        mPreviewListener.onPreviewCreated(surface);
    }

    protected void ShowWhatView(String name) {
        Toast.makeText(mContext.getApplicationContext(), name, Toast.LENGTH_SHORT).show();
    }

    public void onPauseTasks() {
        mContext = null;
        mCameraBase = null;
    }

    public void onDestroyTasks() {

    }

}
