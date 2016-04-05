package com.example.erichc_li.cameraapp.UI;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.example.erichc_li.cameraapp.R;

public class SampleUI extends UI {

    private final AppCompatActivity mAppCompatActivity;
    private FrameLayout mFrameLayout;
    private FrameLayout mFrameLayout2;

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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Capture Success.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                handleCaptureButtonClick();
            }
        });

        mFrameLayout = (FrameLayout) getActivity().findViewById(R.id.camera_textureview);
        mFrameLayout2 = (FrameLayout) getActivity().findViewById(R.id.camera_textureview2);

    }

    @Override
    public void addCameraPreview(View view) {
        mFrameLayout.addView(view);
    }

}
