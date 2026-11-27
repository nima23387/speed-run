package com.nima.streetracing;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class RaceView extends GLSurfaceView {
    private final RaceRenderer renderer;
    private final TouchControls controls = new TouchControls();
    public RaceView(Context c) {
        super(c);
        setEGLContextClientVersion(2);
        renderer = new RaceRenderer(c, controls);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
    @Override public boolean onTouchEvent(MotionEvent e) {
        controls.onTouch(e, getWidth(), getHeight());
        return true;
    }
}
