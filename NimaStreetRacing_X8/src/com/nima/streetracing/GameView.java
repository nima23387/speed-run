package com.nima.streetracing;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class GameView extends GLSurfaceView {
    public static final int LEFT=1, RIGHT=2, GAS=3, BRAKE=4, NITRO=5;
    private final StreetRenderer renderer;
    public GameView(Context c) {
        super(c);
        setEGLContextClientVersion(2);
        renderer = new StreetRenderer();
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
    public void setControl(int key, boolean down) { renderer.setControl(key, down); }
}
