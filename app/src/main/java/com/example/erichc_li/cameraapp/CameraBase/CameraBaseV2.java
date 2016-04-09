package com.example.erichc_li.cameraapp.CameraBase;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class CameraBaseV2 extends CameraBase {

    private static final String TAG = CameraBaseV2.class.getName();

    private CameraManager mCameraManager;

    private String mCameraId;

    private CameraCharacteristics mCameraCharacteristics;

    private StreamConfigurationMap mapScalerStreamConig;

    private Size mPreviewSize;

    private HandlerThread mCameraThread = null;

    private Handler mCameraHandler = null;

    private ImageReader mImageReader;

    private CameraCaptureSession mPreviewSession;

    private CameraDevice mCamera;

    private SurfaceTexture mSurface;

    private CaptureResult mCaptureResult;

    protected CameraBaseV2(Context context) {
        super(context);
        initCameraThread();
        openCamera();
    }

    private void initCameraThread() {
        mCameraThread = new HandlerThread("Camera Handler Thread");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
    }

    @Override
    public void openCamera() {
        mCameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);

        try {

            for (String cameraId : mCameraManager.getCameraIdList()) {

                CameraCharacteristics cameracharacteristics = mCameraManager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                Integer facingID = cameracharacteristics.get(CameraCharacteristics.LENS_FACING);
                StreamConfigurationMap map = cameracharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                if (facingID != null && facingID == CameraCharacteristics.LENS_FACING_BACK && map != null) {
                    mCameraId = cameraId;
                    mCameraCharacteristics = cameracharacteristics;
                    mapScalerStreamConig = map;
                    break;
                }
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        try {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mCameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mCameraHandler);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        setUpPreviewOutputs();

    }

    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            mCamera = camera;
        }

        @Override
        public void onClosed(CameraDevice camera) {
            mCamera = null;
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            mCamera.close();
            mCamera = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            mCamera.close();
            mCamera = null;
        }
    };

    @Override
    public Object getCameraParameters() {
        return null;
    }

    @Override
    public void setCameraParameters(Object parameters) {

    }

    @Override
    public Object getCamera() {
        return mCamera;
    }

    @Override
    public void releaseCamera() {

        if (mPreviewSession != null) {
            try {
                mPreviewSession.stopRepeating();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            mPreviewSession.close();
            mPreviewSession = null;
        }

        if (mCamera != null) {
            mCamera.close();
            mCamera = null;
        }

        releaseCameraThread();
    }

    @Override
    public void startPreview() {

        if (mSurface == null)
            return;

        createCameraPreviewSession();
    }

    private void setUpPreviewOutputs() {

        // Find out if we need to swap dimension to get the preview size relative to sensor coordinate.
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        int displayRotation = windowManager.getDefaultDisplay().getRotation();
        int sensorOrientation = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

        boolean swappedDimensions = false;
        switch (displayRotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                if (sensorOrientation == 90 || sensorOrientation == 270) {
                    swappedDimensions = true;
                }
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                if (sensorOrientation == 0 || sensorOrientation == 180) {
                    swappedDimensions = true;
                }
                break;
            default:
                Log.e(TAG, "Display rotation is invalid: " + displayRotation);
        }

        Log.i(TAG, "setUpPreviewOutputs()...sensorOrientation = " + sensorOrientation + ", displayRotation = " + displayRotation + ", swappedDimensions = " + swappedDimensions);

        Point displaySize = new Point();
        windowManager.getDefaultDisplay().getSize(displaySize);

        int width = ((Activity) getContext()).getWindow().getWindowManager().getDefaultDisplay().getWidth();
        int height = ((Activity) getContext()).getWindow().getWindowManager().getDefaultDisplay().getHeight();

        Log.i(TAG, "setUpPreviewOutputs()...TextureView width = " + width + ", TextureView height = " + height);
        Log.i(TAG, "setUpPreviewOutputs()...displaySize.x = " + displaySize.x + ", displaySize.y = " + displaySize.y);

        int rotatedPreviewWidth = width;
        int rotatedPreviewHeight = height;

        if (swappedDimensions) {
            rotatedPreviewWidth = height;
            rotatedPreviewHeight = width;
        }

        mPreviewSize = new Size(rotatedPreviewWidth, rotatedPreviewHeight);

        Log.i(TAG, "setUpPreviewOutputs()...rotatedPreviewWidth = " + rotatedPreviewWidth + ", rotatedPreviewHeight = " + rotatedPreviewHeight);

    }

    private void createCameraPreviewSession() {

        // We configure the size of default buffer to be the size of camera preview we want.
        mSurface.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        // This is the output Surface we need to start preview.

        List<Surface> outputSurfaces = new ArrayList<Surface>(2);
        Surface mTempSurface = new Surface(mSurface);
        outputSurfaces.add(mTempSurface);
        outputSurfaces.add(mImageReader.getSurface());

        // Here, we create a CameraCaptureSession for camera preview.
        try {
            mCamera.createCaptureSession(outputSurfaces, mSessionStateCallback, mCameraHandler);
        } catch (CameraAccessException e1) {
            e1.printStackTrace();
        }

    }

    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            try {
                updatePreview(session);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {

        }
    };

    private void updatePreview(CameraCaptureSession session) throws CameraAccessException {

        // When the session is ready, we start displaying the preview.
        mPreviewSession = session;

        // We set up a CaptureRequest.Builder with the output Surface.
        CaptureRequest.Builder mPreviewBuilder = getPreviewBuilder();

        try {
            // Auto focus should be continuous for camera preview.
            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
            mPreviewBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);

            // Finally, we start displaying the camera preview.
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private CaptureRequest.Builder getPreviewBuilder() {
        CaptureRequest.Builder mPreviewBuilder = null;
        try {
            mPreviewBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            Surface mTempSurface = new Surface(mSurface);
            mPreviewBuilder.addTarget(mTempSurface);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return mPreviewBuilder;
    }

    @Override
    public void stopPreview() {
        if (mPreviewSession != null) {
            try {
                mPreviewSession.stopRepeating();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }

    @Override
    public void takePicture() {
        try {
            // We set up a CaptureRequest.Builder to capture the pic.
            CaptureRequest.Builder mCaptureBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            mCaptureBuilder.addTarget(mImageReader.getSurface());
            mPreviewSession.capture(mCaptureBuilder.build(), mCaptureCallback, mCameraHandler);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            Log.i(TAG, "onCaptureCompleted()...");
            mCaptureResult = result;
        }

        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            Log.i(TAG, "onCaptureFailed()...");
        }
    };

    @Override
    public void cancelAutoFocus() {

    }

    @Override
    public void autoFocus() {

    }

    @Override
    public void setSurface(Object surface) {
        mSurface = (SurfaceTexture) surface;
    }

    @Override
    public void setPictureSize(int mFormat) {

        int mPicFormat = ImageFormat.UNKNOWN;

        switch (mFormat) {
            case ImageFormat.JPEG:

                for (Size mSize : mapScalerStreamConig.getOutputSizes(ImageFormat.JPEG)) {
                    Log.i(TAG, "mSize = " + mSize);
                }

                mPicFormat = ImageFormat.JPEG;
                break;
            case ImageFormat.RAW_SENSOR:

                if (contains(mCameraCharacteristics.get(
                        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES),
                        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW)) {

                    for (Size mSize : mapScalerStreamConig.getOutputSizes(ImageFormat.RAW_SENSOR)) {
                        Log.i(TAG, "mSize = " + mSize);
                    }
                    mPicFormat = ImageFormat.RAW_SENSOR;
                } else {
                    Log.i(TAG, "Not support RAW");
                    Toast.makeText(getContext(), "Not support RAW, so use the JPEG Format.", Toast.LENGTH_LONG).show();
                    mPicFormat = ImageFormat.JPEG;
                }
                break;
        }

        // create the photo size
        Size[] outputSize = mapScalerStreamConig.getOutputSizes(mPicFormat);

        // For still image captures, we use the largest available size.
        mImageReader = ImageReader.newInstance(outputSize[0].getWidth(), outputSize[0].getHeight(), mPicFormat, /*maxImages*/2);

        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mCameraHandler);

    }

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {

        public void onImageAvailable(ImageReader reader) {

            Image mImage = reader.acquireLatestImage();
            File mPicPath = getOutputMediaFile();

            ImageSaver mImageSaver = new ImageSaver(mImage, mPicPath, mCaptureResult, mCameraCharacteristics);

            mCameraHandler.post(mImageSaver);

        }

    };

    private class ImageSaver implements Runnable {

        private final Image mImage;
        private final File mPicPath;

        /**
         * The CaptureResult for this image capture.
         */
        private final CaptureResult mCaptureResult;

        /**
         * The CameraCharacteristics for this camera device.
         */
        private final CameraCharacteristics mCharacteristics;

        ImageSaver(Image image,File picPath, CaptureResult result, CameraCharacteristics characteristics){
            mImage = image;
            mPicPath = picPath;
            mCaptureResult = result;
            mCharacteristics = characteristics;
        }

        @Override
        public void run() {
            int format = mImage.getFormat();
            boolean success = false;

            switch (format) {
                case ImageFormat.JPEG: {
                    ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    FileOutputStream output = null;

                    Bitmap pictureTaken = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Matrix matrix = new Matrix();
                    matrix.preRotate(90);
                    pictureTaken = Bitmap.createBitmap(pictureTaken ,0,0, pictureTaken.getWidth(), pictureTaken.getHeight(),matrix,true);

                    try {
                        output = new FileOutputStream(mPicPath.getPath());
                        pictureTaken.compress(Bitmap.CompressFormat.JPEG, 50, output);
                        pictureTaken.recycle();
                        output.write(bytes);
                        output.close();
                        success = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        mImage.close();
                        if (null != output) {
                            try {
                                output.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                }
                case ImageFormat.RAW_SENSOR: {
                    DngCreator dngCreator = new DngCreator(mCharacteristics, mCaptureResult);
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(mPicPath.getPath());
                        dngCreator.writeImage(output, mImage);
                        success = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        mImage.close();
                        closeOutput(output);
                    }
                    break;
                }
                default: {
                    Log.e(TAG, "Cannot save image, unexpected image format:" + format);
                    break;
                }

            }

            if(success){
                galleryAddPic(mPicPath);
            }

        }
    }

    private static void closeOutput(OutputStream outputStream) {
        if (null != outputStream) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean contains(int[] modes, int mode) {
        if (modes == null) {
            return false;
        }
        for (int i : modes) {
            if (i == mode) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setCameraPic(int i) {

    }


    private void releaseCameraThread() {

        if (mCameraThread != null) {
            mCameraThread.quitSafely();
            try {
                mCameraThread.join();
                mCameraThread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (mCameraHandler != null) {
            mCameraHandler = null;
        }

    }


    @Override
    public void onPauseTasks() {
        stopPreview();
        releaseCamera();
    }

    @Override
    public void onDestroyTasks() {
        stopPreview();
    }

}
