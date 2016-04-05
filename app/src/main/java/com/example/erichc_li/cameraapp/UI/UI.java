package com.example.erichc_li.cameraapp.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;

public abstract class UI {

    private Activity mActivity;
    private CaptureButtonListener mCaptureButtonListener = null;

    protected UI(Activity activity) {
        mActivity = activity;
    }

    public abstract void init();

    public abstract void addCameraPreview(View view);

    public interface CaptureButtonListener {
        public abstract void onCaptureButtonClick();
    }

    public void setCaptureButtonListener(CaptureButtonListener listener) {
        mCaptureButtonListener = listener;
    }

    protected Activity getActivity() {
        return mActivity;
    }

    protected void keepScreenOn(boolean on) {
        if (on == true)
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    protected void handleCaptureButtonClick() {
        if (mCaptureButtonListener != null)
            mCaptureButtonListener.onCaptureButtonClick();
    }

    private AlertDialog createAlertDialog(String title, String msg, String buttonText) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
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

    public void onPauseTasks() {
        mActivity = null;
    }

    public void onDestroyTasks() {
        mActivity = null;
    }

}
