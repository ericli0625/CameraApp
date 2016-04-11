package com.example.erichc_li.cameraapp.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

public abstract class UI {

    private Activity mActivity;
    private CaptureButtonListener mCaptureButtonListener = null;
    private int mCount = 0;

    protected UI(Activity activity) {
        mActivity = activity;
    }

    public abstract void init();

    public abstract void addCameraPreview(View view);

    public abstract FrameLayout getFrameLayout();

    public abstract void setGestureListener(OnGestureListener detector);

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

    public class FocusView extends View {

        private float mX, mY;

        public FocusView(Context context, PointF mPointF) {
            super(context);
            mX = mPointF.x;
            mY = mPointF.y;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint p = new Paint();
            p.setColor(Color.YELLOW);
            p.setAntiAlias(true);
            p.setStrokeWidth(7);
            p.setStyle(Paint.Style.STROKE);

            canvas.drawCircle(mX, mY, 80, p);

        }
    }

    public void increaseTouchEvent(PointF mPointF) {
        FocusView mFocusView = new FocusView(mActivity, mPointF);
        getFrameLayout().addView(mFocusView, 1);
        Log.i("Eric", "mTouchFocusListener increaseTouchEvent() X " + mCount);
        mCount++;
        Log.i("Eric", "mTouchFocusListener increaseTouchEvent() E " + mCount);
    }

    public void discreteTouchEvent() {
        mCount--;
    }

    public int getTouchEvent() {
        return mCount;
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
