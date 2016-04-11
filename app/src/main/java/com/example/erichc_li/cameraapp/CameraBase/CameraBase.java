package com.example.erichc_li.cameraapp.CameraBase;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class CameraBase {

    private static final String TAG = CameraBase.class.getName();

    private final Context mContext;

    protected TouchFocusListener mTouchFocusListener;

    protected CameraBase(Context context) {
        mContext = context;
    }

    public final Context getContext() {
        return mContext;
    }

    public interface TouchFocusListener {
        public abstract void onTouchFocus(boolean success);
    }

    public void setTouchEventListener(CameraBaseV1.TouchFocusListener listener) {
        mTouchFocusListener = listener;
    }

    public abstract void openCamera();

    public abstract Object getCameraParameters();

    public abstract void setCameraParameters(Object parameters);

    public abstract Object getCamera();

    public abstract void releaseCamera();

    public abstract void startPreview();

    public abstract void stopPreview();

    public abstract void takePicture();

    public abstract PointF autoFocus(MotionEvent event);

    public abstract void setSurface(Object surface);

    public void galleryAddPic(File photoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = photoPath;
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);
    }

    public File getOutputMediaFile() {

        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String photoPath = path.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
        Log.i(TAG, photoPath);
        File photo = new File(photoPath);

        return photo;
    }

    protected abstract Rect calculateTapArea(float x, float y, float coefficient);

    protected int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    public abstract void setPictureSize(int i);

    public abstract void setCameraPic(int i);

    public abstract void onPauseTasks();

    public abstract void onDestroyTasks();

}
