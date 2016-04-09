package com.example.erichc_li.cameraapp.CameraBase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraBaseV1 extends CameraBase {

    private static final String TAG = CameraBase.class.getName();

    Camera mCamera;
    Camera.Parameters parameters;
    private int mRotation;
    private boolean safeToTakePicture = false;

    public CameraBaseV1(Context context) {
        super(context);
        openCamera();
    }

    @Override
    public void openCamera() {
        mCamera = getCameraInstance();
    }

    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return c;
    }

    public void setCameraDisplayOrientation(int cameraId, Object parameters) {

        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        Camera.Parameters mParameters = (Camera.Parameters) parameters;

        mRotation = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation();

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
        mParameters.setRotation(result);

    }

    @Override
    public Camera.Parameters getCameraParameters() {
        parameters = mCamera.getParameters();
        return parameters;
    }

    @Override
    public void setCameraParameters(Object parameters) {
        if (!parameters.equals("") && parameters != null)
            mCamera.setParameters((Camera.Parameters) parameters);
    }

    @Override
    public Camera getCamera() {
        return mCamera;
    }

    @Override
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void startPreview() {
        mCamera.startPreview();
        safeToTakePicture = true;
    }

    @Override
    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
        }
    }

    @Override
    public void cancelAutoFocus() {
        mCamera.cancelAutoFocus();
    }

    @Override
    public void autoFocus() {
        mCamera.autoFocus(myAutoFocusCallback);
    }

    Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            // currently set to auto-focus on single touch
//            Log.i(TAG, "myAutoFocusCallback onAutoFocus() E");
            mTouchFocusListener.onTouchFocus(success);
//            Log.i(TAG, "myAutoFocusCallback onAutoFocus() X");
        }
    };

    @Override
    public void setSurface(Object surface) {

        if (surface instanceof SurfaceHolder) {
            try {
                mCamera.setPreviewDisplay((SurfaceHolder) surface);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mCamera.setPreviewTexture((SurfaceTexture) surface);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void takePicture() {
        if (safeToTakePicture) {
            mCamera.takePicture(shutterCallback, rawPictureCallback, photoCallback);
            safeToTakePicture = false;
        }
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public MediaPlayer mShootSound;

        @Override
        public void onShutter() {
            try {
                AudioManager meng = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
                if (volume != 0) {
                    if (mShootSound == null) {
                        mShootSound = MediaPlayer.create(getContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
                    }
                    if (mShootSound != null) {
                        mShootSound.start();
                    }
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    };

    private Camera.PictureCallback rawPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera arg1) {
            // TODO Auto-generated method stub
            Log.d("TAG", "onPictureTaken - raw");
        }
    };

    private Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("TAG", "onPictureTaken - jpeg");
            byte[] photo = data;
            new SavePhotoTask().execute(photo);
            mCamera.startPreview();
            safeToTakePicture = true;
        }
    };

    class SavePhotoTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... jpeg) {

            if (jpeg[0] == null) {
                Log.e(TAG, "jpeg[0] is null");
                return (null);
            }

            File photoPath = getOutputMediaFile();
            Bitmap pictureTaken = BitmapFactory.decodeByteArray(jpeg[0], 0, jpeg[0].length);
            Matrix matrix = new Matrix();
            matrix.preRotate(90);
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

    @Override
    public void setPictureSize(int i) {
        Camera.Parameters parameters = getCameraParameters();
        setCameraDisplayOrientation(0, parameters);
        List<Camera.Size> psSize = parameters.getSupportedPictureSizes();
        parameters.setPictureSize(psSize.get(0).width, psSize.get(0).height);
        setCameraParameters(parameters);
    }

    @Override
    public void setCameraPic(int i) {
        Camera.Parameters parameters = getCameraParameters();
        List<Camera.Size> psSize = parameters.getSupportedPictureSizes();
        int w = psSize.get(i).width;
        int h = psSize.get(i).height;
        parameters.setPictureSize(w, h);
        setCameraParameters(parameters);
        Toast.makeText(getContext().getApplicationContext(), w + "*" + h, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPauseTasks() {
        stopPreview();
        releaseCamera();
    }

    @Override
    public void onDestroyTasks() {

    }

}
