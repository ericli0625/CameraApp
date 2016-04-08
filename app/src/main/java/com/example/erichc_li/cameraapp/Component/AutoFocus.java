package com.example.erichc_li.cameraapp.Component;

import android.view.MotionEvent;

public interface AutoFocus {

    public void handleZoom(MotionEvent event);

    public void handleFocus(MotionEvent event, int action);

}
