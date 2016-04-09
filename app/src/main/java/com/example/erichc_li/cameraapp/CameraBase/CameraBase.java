package com.example.erichc_li.cameraapp.CameraBase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class CameraBase {

    private static final String TAG = CameraBase.class.getName();

    private final Context mContext;

    protected TouchFocusListener mTouchFocusListener;

    protected CameraBase(Context context) {
        mContext = context;
    }

    public interface TouchFocusListener {
        public abstract void onTouchFocus(boolean success);
    }

    public void setTouchEventListener(CameraBaseV1.TouchFocusListener listener) {
        mTouchFocusListener = listener;
    }

    public abstract Object getCameraParameters();

    public abstract void setCameraParameters(Object parameters);

    public abstract Object getCamera();

    public abstract void releaseCamera();

    public abstract void startPreview();

    public abstract void stopPreview();

    public abstract void takePicture();

    public abstract void cancelAutoFocus();

    public abstract void autoFocus();

    public abstract void setSurface(Object surface);

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

    public abstract void setPictureSize();

    public abstract void setCameraPic(int i);

    public abstract void onPauseTasks();

    public abstract void onDestroyTasks();

}
