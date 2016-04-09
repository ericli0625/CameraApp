package com.example.erichc_li.cameraapp.UI;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.erichc_li.cameraapp.R;

public class SampleUI extends UI {

    private static final String TAG = SampleUI.class.getName();

    private final AppCompatActivity mAppCompatActivity;
    private FrameLayout mFrameLayout;
    private GestureDetector mDetector;

    public SampleUI(Activity activity) {
        super(activity);
        mAppCompatActivity = (AppCompatActivity) activity;
    }

    @Override
    public void init() {
        keepScreenOn(true);
        getActivity().setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mAppCompatActivity.setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(mOnClickListener);

        mFrameLayout = (FrameLayout) getActivity().findViewById(R.id.camera_textureview);

        mFrameLayout.setOnTouchListener(mOnTouchListener);

    }

    @Override
    public void addCameraPreview(View view) {
        mFrameLayout.addView(view);
    }

    @Override
    public FrameLayout getFrameLayout() {
        return mFrameLayout;
    }

    @Override
    public void setGestureListener(OnGestureListener mListener) {
        mDetector = new GestureDetector(mAppCompatActivity, mListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Snackbar.make(view, "Capture Success.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            handleCaptureButtonClick();
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mDetector.onTouchEvent(event);
            return true;
        }
    };

}
