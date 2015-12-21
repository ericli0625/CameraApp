package com.example.erichc_li.cameraapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private Camera mCamera;
    private Button mShutter;
    private FrameLayout mFrameLayout;
    public static FrameLayout mFrameLayout2;
    private CameraPreview mPreview;
    private SensorManager sm;
    private Sensor aSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Capture Success.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                takePicture();
            }
        });

        mCamera = getCameraInstance();

        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mPreview = new CameraPreview(this, mCamera, rotation, aSensor, sm);

        mFrameLayout = (FrameLayout) findViewById(R.id.camera_textureview);
        mFrameLayout.addView(mPreview);

        mFrameLayout2 = (FrameLayout) findViewById(R.id.camera_textureview2);

/*
        SquareView sqv = new SquareView(this);
        mFrameLayout.addView(sqv);
*/
/*
        mShutter = (Button)findViewById(R.id.button);
        mShutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takePicture();
            }
        });
*/
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return c;
    }

    private void takePicture() {

        if (mPreview.inPreview) {
            mCamera.takePicture(null, null, photoCallback);
        }

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
            pictureTaken = Bitmap.createBitmap(pictureTaken ,0,0, pictureTaken.getWidth(), pictureTaken.getHeight(),matrix,true);

            try {
                FileOutputStream fos = new FileOutputStream(photoPath.getPath());
                pictureTaken.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                pictureTaken.recycle();
                fos.write(jpeg[0]);
                fos.close();
            } catch (java.io.IOException e) {
                Log.e(TAG, "Exception in photoCallback", e);
            }
            return (null);
        }
    }

    private File getOutputMediaFile() {

        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String photoPath = path.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
        Log.i(TAG, photoPath);
        File photo = new File(photoPath);

        return photo;
    }

    private AlertDialog createAlertDialog(String title, String msg, String buttonText) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        AlertDialog msgDialog = dialogBuilder.create();
        msgDialog.setTitle(title);
        msgDialog.setMessage(msg);
        msgDialog.setButton(buttonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int idx) {
                return; // Nothing to see here...
            }
        });

        return msgDialog;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.Pic_size1:
                setCameraPic(4096,3072);
                return true;
            case R.id.Pic_size2:
                setCameraPic(1920,1080);
                return true;
            case R.id.Pic_size3:
                setCameraPic(800,480);
                return true;
            case R.id.Pic_size4:
                setCameraPic(320,240);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setCameraPic(int w,int h) {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureSize(w, h);
        mCamera.setParameters(parameters);
        Toast.makeText(getApplicationContext(), w+"*"+h, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()...");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG, "onPause()...");

    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i(TAG, "onStop()...");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "onDestroy()...");
        mCamera.release();
    }

}