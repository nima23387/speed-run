package com.nima.streetracing;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class GameView extends GLSurfaceView {
    public static final int LEFT=1, RIGHT=2, GAS=3, BRAKE=4, NITRO=5;
    private final StreetRenderer renderer;
    public GameView(Context c) {
        super(c);
        setEGLContextClientVersion(2);
        renderer=new StreetRenderer();
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
    public void setControl(int k, boolean d) { renderer.setControl(k,d); }
    public void setRace(boolean r) { renderer.setRace(r); }
    public void setCar(int c) { renderer.setCar(c); }
    public void pauseGame() { renderer.setPaused(true); }
    public void resumeGame() { renderer.setPaused(false); }
}
