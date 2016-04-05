package com.example.erichc_li.cameraapp.Preview;

import android.content.Context;

public class PreviewFactory {
    public static final int PREVIEW_TYPE_SURFACE_TEXTURE = 0;
    public static final int PREVIEW_TYPE_SURFACE_VIEW = 1;
    public static final int PREVIEW_TYPE_GLSURFACE_VIEW = 2;

    public static Preview createTextureView(Context context) {
        return new TextureViewPreview(context);
    }

    public static Preview createSurfaceView(Context context) {
        return new SurfaceViewPreview(context);
    }

    public static Preview createGLSurfaceView(Context context) {
        return new GLSurfaceViewPreview(context);
    }
}
