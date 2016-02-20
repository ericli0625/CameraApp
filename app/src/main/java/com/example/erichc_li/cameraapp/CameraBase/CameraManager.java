package com.example.erichc_li.cameraapp.CameraBase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.example.erichc_li.cameraapp.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraManager {

    private static final String TAG = CameraManager.class.getName();
    private final Context mContext;

    Camera mCamera;
    Camera.Parameters parameters;
    private int mRotation;

    public CameraManager(Context context,Camera camera) {
        mContext = context;
        mCamera = camera;
    }

    public void setCameraDisplayOrientation(int cameraId, Camera.Parameters parameters) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);

        mRotation = ((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation();

        int degrees = 0;
        switch (mRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
        parameters.setRotation(result);

    }

    public Camera.Parameters getCameraParameters() {
        parameters = mCamera.getParameters();
        return parameters;
    }

    public void setCameraParameters(Camera.Parameters parameters) {
        mCamera.setParameters(parameters);
    }

    public Camera getCamera() {
        return mCamera;
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void setPreviewTexture(SurfaceTexture mSurface) throws IOException {
        mCamera.setPreviewTexture(mSurface);
    }

    public void startPreview() {
        mCamera.startPreview();
    }

    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
        }
    }

    public void cancelAutoFocus() {
        mCamera.cancelAutoFocus();
    }

    public void autoFocus() {
        mCamera.autoFocus(myAutoFocusCallback);
    }

    Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            // currently set to auto-focus on single touch
            if (success) {
                Log.i(TAG, "聚焦成功...");
                MainActivity.mFrameLayout2.removeAllViews();
            } else {
                Log.i(TAG, "聚焦失敗...");
                MainActivity.mFrameLayout2.removeAllViews();
            }

        }
    };

    public void setPreviewDisplay(SurfaceHolder previewDisplay) throws IOException {
        mCamera.setPreviewDisplay(previewDisplay);
    }

    public void takePicture() {
        mCamera.takePicture(null, null, photoCallback);
    }

    private Camera.PictureCallback photoCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            byte[] photo = data;
            new SavePhotoTask().execute(photo);
            mCamera.startPreview();
        }
    };

    class SavePhotoTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... jpeg) {
            File photoPath = getOutputMediaFile();

            Bitmap pictureTaken = BitmapFactory.decodeByteArray(jpeg[0], 0, jpeg[0].length);
            Matrix matrix = new Matrix();
            //matrix.preRotate(90);
            pictureTaken = Bitmap.createBitmap(pictureTaken, 0, 0, pictureTaken.getWidth(), pictureTaken.getHeight(), matrix, true);

            try {
                FileOutputStream fos = new FileOutputStream(photoPath.getPath());
                pictureTaken.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                pictureTaken.recycle();
                fos.write(jpeg[0]);
                fos.close();
                galleryAddPic(photoPath);
            } catch (java.io.IOException e) {
                Log.e(TAG, "Exception in photoCallback", e);
            }
            return (null);
        }
    }

    private void galleryAddPic(File photoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = photoPath;
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);
    }

    private File getOutputMediaFile() {

        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String photoPath = path.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
        Log.i(TAG, photoPath);
        File photo = new File(photoPath);

        return photo;
    }

    public void setCameraPic(int w, int h) {
        Camera.Parameters parameters = getCameraParameters();
        parameters.setPictureSize(w, h);
        setCameraParameters(parameters);
        Toast.makeText(mContext.getApplicationContext(), w + "*" + h, Toast.LENGTH_SHORT).show();
    }

    public void ShowWhatView(String name) {
        Toast.makeText(mContext.getApplicationContext(), name, Toast.LENGTH_SHORT).show();
    }
}
