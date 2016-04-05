package com.example.erichc_li.cameraapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private static final int PREVIEW_TEXTUREVIEW = R.id.View1;
    private static final int PREVIEW_SURFACEVIEW = R.id.View2;
    private static final int PREVIEW_GLSURFACEVIEW = R.id.View3;

    private Controller mController;

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
//                mCameraManager.takePicture();
            }
        });

        mController = new Controller(this);

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

        mController.removeAllViews();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case PREVIEW_TEXTUREVIEW:
//                mController.configCameraView(PREVIEW_TEXTUREVIEW);
                break;
            case PREVIEW_SURFACEVIEW:
//                mController.configCameraView(PREVIEW_SURFACEVIEW);
                break;
            case PREVIEW_GLSURFACEVIEW:
//                mController.configCameraView(PREVIEW_GLSURFACEVIEW);
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
        mController.executeResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()...");
        mController.executePause();
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
        mController.executeStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()...");
        mController.executeDestroy();
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