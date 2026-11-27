package com.nima.streetracing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

public class StreetRenderer implements GLSurfaceView.Renderer {
    private static final String VERT =
        "uniform mat4 uMVP; attribute vec3 aPos; attribute vec3 aColor; varying vec3 vColor;" +
        "void main(){ gl_Position=uMVP*vec4(aPos,1.0); vColor=aColor; }";
    private static final String FRAG =
        "precision mediump float; varying vec3 vColor; void main(){ gl_FragColor=vec4(vColor,1.0); }";
    private int program, aPos, aColor, uMVP;
    private FloatBuffer vertices;
    private ShortBuffer indices;
    private int indexCount;
    private final float[] proj=new float[16], view=new float[16], model=new float[16], mvp=new float[16];
    private volatile boolean left,right,gas,brake,nitro;
    private float carX=0f, speed=0f, steer=0f, worldZ=0f;
    private long last=0;

    public void setControl(int key, boolean down) {
        if(key==GameView.LEFT) left=down;
        if(key==GameView.RIGHT) right=down;
        if(key==GameView.GAS) gas=down;
        if(key==GameView.BRAKE) brake=down;
        if(key==GameView.NITRO) nitro=down;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.38f,0.62f,0.82f,1f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        program=link(VERT,FRAG);
        aPos=GLES20.glGetAttribLocation(program,"aPos");
        aColor=GLES20.glGetAttribLocation(program,"aColor");
        uMVP=GLES20.glGetUniformLocation(program,"uMVP");
        buildWorld();
        last=System.nanoTime();
    }
    public void onSurfaceChanged(GL10 gl,int w,int h) {
        GLES20.glViewport(0,0,w,h);
        float ratio=(float)w/(float)Math.max(1,h);
        float near=0.5f;
        float top=(float)(near*Math.tan(Math.toRadians(58f)*0.5));
        float right=top*ratio;
        Matrix.frustumM(proj,0,-right,right,-top,top,near,180f);
    }
    public void onDrawFrame(GL10 gl) {
        long now=System.nanoTime();
        float dt=Math.min(0.05f,(now-last)/1000000000f); last=now;
        if(gas) speed+=1.9f*dt;
        if(brake) speed-=3.4f*dt;
        if(!gas && !brake) speed-=0.45f*dt;
        if(nitro && gas) speed+=2.4f*dt;
        speed=Math.max(0f,Math.min(speed, nitro?15f:10f));
        steer=(left?-1f:0f)+(right?1f:0f);
        carX+=steer*dt*(1.2f+speed*0.20f);
        carX=Math.max(-4.0f,Math.min(4.0f,carX));
        worldZ+=speed*dt;

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(view,0,carX*0.35f,4.2f,7.5f, carX*0.2f,0.7f,-4.0f, 0f,1f,0f);
        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(aPos);
        GLES20.glEnableVertexAttribArray(aColor);
        vertices.position(0); GLES20.glVertexAttribPointer(aPos,3,GLES20.GL_FLOAT,false,24,vertices);
        vertices.position(3); GLES20.glVertexAttribPointer(aColor,3,GLES20.GL_FLOAT,false,24,vertices);
        GLES20.glUniformMatrix4fv(uMVP,1,false,makeMvp(0,0,0),0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,indexCount,GLES20.GL_UNSIGNED_SHORT,indices);
        drawCar();
        GLES20.glDisableVertexAttribArray(aPos);
        GLES20.glDisableVertexAttribArray(aColor);
    }
    private float[] makeMvp(float x,float y,float z) {
        Matrix.setIdentityM(model,0);
        Matrix.translateM(model,0,x,y,z);
        Matrix.multiplyMM(mvp,0,view,0,model,0);
        float[] out=new float[16];
        Matrix.multiplyMM(out,0,proj,0,mvp,0);
        return out;
    }
    private void drawCar() {
        // A small angular coupe mesh, centered in front of the chase camera.
        float[] car={
          -0.85f,0.15f,-1.2f, 0.85f,0.08f,0.08f,  0.85f,0.15f,-1.2f, 0.85f,0.08f,0.08f,
          -0.62f,0.15f, 1.0f, 0.75f,0.02f,0.02f,  0.62f,0.15f, 1.0f, 0.75f,0.02f,0.02f,
          -0.48f,0.62f,-0.45f,0.95f,0.25f,0.06f, 0.48f,0.62f,-0.45f,0.95f,0.25f,0.06f,
          -0.42f,0.60f, 0.45f,0.95f,0.25f,0.06f, 0.42f,0.60f,0.45f,0.95f,0.25f,0.06f
        };
        short[] ix={0,1,4,1,5,4,4,5,6,5,7,6,0,4,2,2,4,6,1,3,5,3,7,5,6,7,4,7,5,4};
        FloatBuffer vb=ByteBuffer.allocateDirect(car.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for(int i=0;i<car.length;i++) vb.put(car[i]);
        vb.position(0);
        ShortBuffer ib=ByteBuffer.allocateDirect(ix.length*2).order(ByteOrder.nativeOrder()).asShortBuffer();
        for(int i=0;i<ix.length;i++) ib.put(ix[i]); ib.position(0);
        int oldV=vertices==null?0:0;
        GLES20.glVertexAttribPointer(aPos,3,GLES20.GL_FLOAT,false,24,vb);
        vb.position(3); GLES20.glVertexAttribPointer(aColor,3,GLES20.GL_FLOAT,false,24,vb);
        GLES20.glUniformMatrix4fv(uMVP,1,false,makeMvp(carX,0f,-2.0f),0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,ix.length,GLES20.GL_UNSIGNED_SHORT,ib);
        vertices.position(0);
        GLES20.glVertexAttribPointer(aPos,3,GLES20.GL_FLOAT,false,24,vertices);
        vertices.position(3); GLES20.glVertexAttribPointer(aColor,3,GLES20.GL_FLOAT,false,24,vertices);
    }
    private void buildWorld() {
        // Repeating road corridor and simple block buildings; geometry kept deliberately small.
        float[] v=new float[] {
          -40,-0.15f,-100, .12f,.38f,.16f, 40,-0.15f,-100, .12f,.38f,.16f,
          -40,-0.15f, 20, .12f,.38f,.16f, 40,-0.15f, 20, .12f,.38f,.16f,
          -5,0,-100, .18f,.18f,.19f, 5,0,-100, .18f,.18f,.19f,
          -5,0, 20, .18f,.18f,.19f, 5,0, 20, .18f,.18f,.19f,
          -0.12f,0.01f,-100, .92f,.86f,.55f, 0.12f,0.01f,-100, .92f,.86f,.55f,
          -0.12f,0.01f, 20, .92f,.86f,.55f, 0.12f,0.01f, 20, .92f,.86f,.55f
        };
        short[] ix={0,1,2,1,3,2,4,5,6,5,7,6,8,9,10,9,11,10};
        vertices=ByteBuffer.allocateDirect(v.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for(int i=0;i<v.length;i++) vertices.put(v[i]); vertices.position(0);
        indices=ByteBuffer.allocateDirect(ix.length*2).order(ByteOrder.nativeOrder()).asShortBuffer();
        for(int i=0;i<ix.length;i++) indices.put(ix[i]); indices.position(0);
        indexCount=ix.length;
    }
    private int link(String vs,String fs) {
        int v=shader(GLES20.GL_VERTEX_SHADER,vs), f=shader(GLES20.GL_FRAGMENT_SHADER,fs);
        int p=GLES20.glCreateProgram(); GLES20.glAttachShader(p,v); GLES20.glAttachShader(p,f);
        GLES20.glLinkProgram(p); return p;
    }
    private int shader(int type,String src) {
        int s=GLES20.glCreateShader(type); GLES20.glShaderSource(s,src); GLES20.glCompileShader(s); return s;
    }
}
