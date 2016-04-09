package com.example.erichc_li.cameraapp.CameraBase;

import android.content.Context;

public class CameraFactory {

    public static CameraBase createCameraBaseV1(Context context) {
        return new CameraBaseV1(context);
    }

    public static CameraBase createCameraBaseV2(Context context) {
        return new CameraBaseV2(context);
    }
}
