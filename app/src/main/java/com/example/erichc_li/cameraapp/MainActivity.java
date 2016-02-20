package com.example.erichc_li.cameraapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.erichc_li.cameraapp.CameraBase.CameraManager;
import com.example.erichc_li.cameraapp.CameraBase.CameraSensorManager;
import com.example.erichc_li.cameraapp.Preview.GLSurfaceViewPreview;
import com.example.erichc_li.cameraapp.Preview.OthersPreview;
import com.example.erichc_li.cameraapp.Preview.SurfaceViewPreview;
import com.example.erichc_li.cameraapp.Preview.TextureViewPreview;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private CameraManager mCameraManager;
    private FrameLayout mFrameLayout;
    public static FrameLayout mFrameLayout2;
    private View mPreview;
    private Camera mCamera;

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
                mCameraManager.takePicture();
            }
        });

        mFrameLayout = (FrameLayout) findViewById(R.id.camera_textureview);
        mFrameLayout2 = (FrameLayout) findViewById(R.id.camera_textureview2);

        mCamera = getCameraInstance();

    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return c;
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

        mFrameLayout.removeAllViews();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.Pic_size1:
                mCameraManager = new CameraManager(this,mCamera);
                mPreview = new TextureViewPreview (this, mCameraManager,R.id.Pic_size1);
                mFrameLayout.addView(mPreview);
                return true;
            case R.id.Pic_size2:
                mCameraManager = new CameraManager(this,mCamera);
                mPreview = new SurfaceViewPreview(this, mCameraManager,R.id.Pic_size2);
                mFrameLayout.addView(mPreview);
                return true;
            case R.id.Pic_size3:
                mCameraManager = new CameraManager(this,mCamera);
                mPreview = new GLSurfaceViewPreview(this, mCameraManager,R.id.Pic_size3);
                mFrameLayout.addView(mPreview);
                return true;
            case R.id.Pic_size4:
                mCameraManager = new CameraSensorManager(this,mCamera);
                mPreview = new OthersPreview(this, (CameraSensorManager) mCameraManager,R.id.Pic_size4);
                mFrameLayout.addView(mPreview);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()...");

        mCameraManager = new CameraManager(this,mCamera);
        mPreview = new TextureViewPreview(this, mCameraManager, R.id.Pic_size1);
        mFrameLayout.addView(mPreview);

    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG, "onPause()...");
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.i(TAG, "onStart()...");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.i(TAG, "onRestart()...");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()...");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "onDestroy()...");
        mCameraManager.releaseCamera();
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

}