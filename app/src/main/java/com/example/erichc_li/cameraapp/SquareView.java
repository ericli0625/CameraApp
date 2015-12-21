package com.example.erichc_li.cameraapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class SquareView extends View {

    private static final String TAG = "SquareView";
    private float mX,mY;

    public SquareView(Context context,float x , float y) {
        super(context);
        mX = x;
        mY = y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.d(TAG, "Draw Square");

        Paint p = new Paint();
        p.setColor(Color.YELLOW);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(mX, mY, 80, p);

    }
}
