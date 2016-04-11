package com.example.erichc_li.cameraapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private static final int PREVIEW_TEXTUREVIEW = R.id.View1;
    private static final int PREVIEW_SURFACEVIEW = R.id.View2;
    private static final int PREVIEW_GLSURFACEVIEW = R.id.View3;

    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private Controller mController;

    private FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()...");
        this.setContentView(R.layout.activity_main);
        mFrameLayout = (FrameLayout) findViewById(R.id.permission_view);

        showPermission();

    }

    private void showPermission() {
        // Check if Camera permissions have been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            // Permissions are already available, start camera preview and save photo.
            Snackbar.make(mFrameLayout,
                    "Camera permission is available. Starting Camera.",
                    Snackbar.LENGTH_SHORT).show();
            mController = new Controller(this);
        } else {
            // Permission was missing and must be requested.
            requestCameraPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CAMERA) {

            // Request for camera permissions.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions have been granted. Start camera preview Activity.
                Snackbar.make(mFrameLayout,
                        "Camera permissions was granted. Starting Camera and Save photo.",
                        Snackbar.LENGTH_SHORT)
                        .show();
                mController = new Controller(this);
            } else {
                // Permission request was denied.
                Snackbar.make(mFrameLayout,
                        "Camera permissions request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if permissions were not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request missing permissions.
            Snackbar.make(mFrameLayout,
                    "Camera access is required to start the camera.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CAMERA);
                }
            }).show();

        } else {
            Snackbar.make(mFrameLayout,
                    "Permission is not available. Requesting camera permission.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CAMERA);
        }
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

//        mController.removeAllViews();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case PREVIEW_TEXTUREVIEW:
//                mController.selectCameraPreview(PREVIEW_TEXTUREVIEW);
                break;
            case PREVIEW_SURFACEVIEW:
//                mController.selectCameraPreview(PREVIEW_SURFACEVIEW);
                break;
            case PREVIEW_GLSURFACEVIEW:
//                mController.selectCameraPreview(PREVIEW_GLSURFACEVIEW);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()...");
        if (mController != null) {
            mController.executeResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()...");
        if (mController != null) {
            mController.executePause();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()...");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart()...");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()...");
        if (mController != null) {
            mController.executeStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()...");
        if (mController != null) {
            mController.executeDestroy();
        }
    }

}