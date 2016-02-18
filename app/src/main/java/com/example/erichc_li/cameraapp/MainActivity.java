package com.example.erichc_li.cameraapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;

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


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private CameraManager mCameraManager;
    private FrameLayout mFrameLayout;
    public static FrameLayout mFrameLayout2;
    private View mPreview;

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
//        mFrameLayout2 = (FrameLayout) findViewById(R.id.camera_textureview2);

        mCameraManager = new CameraManager(this);

        defaultView();

    }

    public void defaultView(){
        mPreview = new TextureViewPreview (this, mCameraManager);
        mFrameLayout.addView(mPreview);
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
                mPreview = new TextureViewPreview (this, mCameraManager);
                mFrameLayout.addView(mPreview);
                return true;
            case R.id.Pic_size2:
                mPreview = new SurfaceViewPreview (this, mCameraManager);
                mFrameLayout.addView(mPreview);
                return true;
            case R.id.Pic_size3:
                mPreview = new GLSurfaceViewPreview (this, mCameraManager);
                mFrameLayout.addView(mPreview);
                return true;
            case R.id.Pic_size4:
                mPreview = new CameraPreview(this, mCameraManager);
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