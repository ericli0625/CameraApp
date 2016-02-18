package com.example.erichc_li.cameraapp;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLSurfaceViewPreview extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = SurfaceViewPreview.class.getName();
    private Context mContext;
    private CameraManager mCameraManager;
    private SurfaceTexture mSurface;

    public GLSurfaceViewPreview(Context context,CameraManager camera) {
        super(context);
        mContext = context;
        mCameraManager = camera;

        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);

        mSurface = new SurfaceTexture(createTextureID());
        mSurface.setOnFrameAvailableListener(this);

    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "surfaceCreated...");


        if (mCameraManager.getCamera() != null) {
//            try {
//                Log.i(TAG, "StartPreview...");
//
//                Camera.Parameters parameters = mCameraManager.getCameraParameters();
//
//                mCameraManager.setCameraDisplayOrientation(0, parameters);
//
//                List<Camera.Size> psSize = parameters.getSupportedPictureSizes();
//
//                parameters.setPictureSize(psSize.get(0).width, psSize.get(0).height);
//
//                mCameraManager.setCameraParameters(parameters);
//
//                mCameraManager.setPreviewTexture(previewHolder);
//                mCameraManager.startPreview();
//
//            } catch (IOException ioe) {
//                ioe.printStackTrace();
//            }
        } else {
            Log.i(TAG, "surfaceCreated, mCamera is null");
        }
        mCameraManager.ShowWhatView("GLSurfaceViewPreview");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "surfaceChanged...");

        // stop preview before making changes

        try {
            mCameraManager.stopPreview();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

        try {
//            mCameraManager.setPreviewTexture(previewHolder);
            mCameraManager.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        this.requestRender();
    }

    private int createTextureID()
    {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }
}
