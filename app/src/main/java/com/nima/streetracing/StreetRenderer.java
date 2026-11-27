package com.nima.streetracing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

public class StreetRenderer implements GLSurfaceView.Renderer {
    private static final String VS =
        "uniform mat4 uMVP; attribute vec3 aPos; attribute vec3 aColor; varying vec3 vColor;" +
        "void main(){ gl_Position=uMVP*vec4(aPos,1.0); vColor=aColor; }";
    private static final String FS =
        "precision mediump float; varying vec3 vColor; void main(){ gl_FragColor=vec4(vColor,1.0); }";
    private int program, posLoc, colorLoc, mvpLoc;
    private float[] projection=new float[16], view=new float[16], model=new float[16], temp=new float[16], mvp=new float[16];
    private volatile boolean left,right,gas,brake,nitro,paused=true,race=false;
    private float carX=0, speed=0, roadScroll=0, distance=0, elapsed=0;
    private int carType=0;
    private long lastTime;
    private FloatBuffer cubeV;
    private ShortBuffer cubeI;
    private static final short[] CUBE_IDX={0,1,2,0,2,3,4,6,5,4,7,6,0,4,5,0,5,1,3,2,6,3,6,7,1,5,6,1,6,2,0,3,7,0,7,4};

    public void setControl(int k, boolean d) {
        if(k==1) left=d; if(k==2) right=d; if(k==3) gas=d; if(k==4) brake=d; if(k==5) nitro=d;
    }
    public void setRace(boolean r) { race=r; distance=0; elapsed=0; }
    public void setCar(int c) { carType=c; }
    public void setPaused(boolean p) { paused=p; }

    public void onSurfaceCreated(GL10 gl,EGLConfig cfg) {
        GLES20.glClearColor(0.42f,0.66f,0.86f,1f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        program=link(VS,FS);
        posLoc=GLES20.glGetAttribLocation(program,"aPos");
        colorLoc=GLES20.glGetAttribLocation(program,"aColor");
        mvpLoc=GLES20.glGetUniformLocation(program,"uMVP");
        makeCube();
        lastTime=System.nanoTime();
    }
    public void onSurfaceChanged(GL10 gl,int w,int h) {
        GLES20.glViewport(0,0,w,h);
        float ratio=(float)w/(float)Math.max(1,h);
        float near=0.5f, top=(float)(near*Math.tan(Math.toRadians(62)*0.5));
        Matrix.frustumM(projection,0,-top*ratio,top*ratio,-top,top,near,140f);
    }
    public void onDrawFrame(GL10 gl) {
        long now=System.nanoTime();
        float dt=Math.min(0.05f,(now-lastTime)/1000000000f); lastTime=now;
        if(!paused) {
            if(gas) speed+=2.6f*dt;
            if(brake) speed-=4.5f*dt;
            if(!gas && !brake) speed-=0.55f*dt;
            if(nitro && gas) speed+=3.2f*dt;
            speed=Math.max(0,Math.min(speed,nitro?17:12));
            float steer=(left?-1:0)+(right?1:0);
            carX+=steer*dt*(1.4f+speed*0.16f);
            carX=Math.max(-4.2f,Math.min(4.2f,carX));
            roadScroll+=speed*dt;
            distance+=speed*dt;
            elapsed+=dt;
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        float camX=carX*0.20f;
        Matrix.setLookAtM(view,0,camX,4.4f,8.5f,camX*0.55f,0.5f,-5.0f,0,1,0);
        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(posLoc);
        GLES20.glEnableVertexAttribArray(colorLoc);

        // Ground and broad asphalt road.
        drawBox(0,-0.35f,-38,42,0.25f,90,0.15f,0.38f,0.16f);
        drawBox(0,-0.16f,-38,5.5f,0.12f,90,0.16f,0.17f,0.19f);

        // Dashed center lane markings, repeated down the road.
        for(int i=0;i<16;i++) {
            float z=-75+(i*6)+(roadScroll%6);
            drawBox(0,-0.085f,z,0.10f,0.025f,2.5f,0.95f,0.86f,0.58f);
        }

        // Sidewalks and stylized city blocks, alternating height and colors.
        for(int i=0;i<10;i++) {
            float z=-72+(i*9)+(roadScroll%9);
            float h1=3.0f+(i%3)*1.1f;
            drawBox(-8.3f,h1*0.5f,z,2.2f,h1,5.8f,0.34f,0.39f,0.45f);
            drawBox(8.3f,(h1+1)*0.5f,z-4,2.4f,h1+1,6.0f,0.40f,0.42f,0.48f);
            // bright shop/window bands
            drawBox(-8.3f,1.4f,z-0.1f,2.23f,0.12f,4.8f,0.92f,0.57f,0.20f);
            drawBox(8.3f,1.6f,z-4.1f,2.43f,0.12f,5.0f,0.20f,0.58f,0.72f);
        }

        // A few AI traffic cars moving toward the player in a loop.
        for(int i=0;i<3;i++) {
            float z=-32+i*19+(roadScroll*0.45f%60);
            if(z>8) z-=60;
            drawCarMesh((i%2==0?-2.0f:2.0f),z, i%2==0?0.08f:0.12f,0.25f,0.70f);
        }

        // Player car visible in front of chase camera.
        drawCarMesh(carX,-1.7f, carType==0?0.85f:(carType==1?0.12f:0.08f),
                    carType==1?0.32f:0.12f, carType==2?0.85f:0.08f);

        GLES20.glDisableVertexAttribArray(posLoc);
        GLES20.glDisableVertexAttribArray(colorLoc);
    }

    private void drawCarMesh(float x,float z,float r,float g,float b) {
        // Body, cabin, bumpers and four dark wheels.
        drawBox(x,0.22f,z,0.92f,0.38f,1.85f,r,g,b);
        drawBox(x,0.51f,z-0.18f,0.62f,0.30f,0.82f,0.12f,0.20f,0.25f);
        drawBox(x,0.14f,z+0.94f,0.82f,0.10f,0.12f,0.12f,0.12f,0.13f);
        drawBox(x-0.72f,0.12f,z-0.56f,0.18f,0.22f,0.36f,0.04f,0.04f,0.05f);
        drawBox(x+0.72f,0.12f,z-0.56f,0.18f,0.22f,0.36f,0.04f,0.04f,0.05f);
        drawBox(x-0.72f,0.12f,z+0.56f,0.18f,0.22f,0.36f,0.04f,0.04f,0.05f);
        drawBox(x+0.72f,0.12f,z+0.56f,0.18f,0.22f,0.36f,0.04f,0.04f,0.05f);
    }

    private void makeCube() {
        cubeV=ByteBuffer.allocateDirect(48*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        cubeI=ByteBuffer.allocateDirect(CUBE_IDX.length*2).order(ByteOrder.nativeOrder()).asShortBuffer();
        cubeI.put(CUBE_IDX).position(0);
    }

    private void drawBox(float x,float y,float z,float sx,float sy,float sz,float r,float g,float b) {
        float[] verts={
            -1,-1,-1,r,g,b,  1,-1,-1,r,g,b,  1,1,-1,r,g,b, -1,1,-1,r,g,b,
            -1,-1,1,r*0.82f,g*0.82f,b*0.82f, 1,-1,1,r*0.82f,g*0.82f,b*0.82f,
            1,1,1,r*0.82f,g*0.82f,b*0.82f, -1,1,1,r*0.82f,g*0.82f,b*0.82f
        };
        cubeV.position(0);
        cubeV.put(verts);
        cubeV.position(0);
        GLES20.glVertexAttribPointer(posLoc,3,GLES20.GL_FLOAT,false,24,cubeV);
        cubeV.position(3);
        GLES20.glVertexAttribPointer(colorLoc,3,GLES20.GL_FLOAT,false,24,cubeV);
        Matrix.setIdentityM(model,0);
        Matrix.translateM(model,0,x,y,z);
        Matrix.scaleM(model,0,sx,sy,sz);
        Matrix.multiplyMM(temp,0,view,0,model,0);
        Matrix.multiplyMM(mvp,0,projection,0,temp,0);
        GLES20.glUniformMatrix4fv(mvpLoc,1,false,mvp,0);
        cubeI.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,CUBE_IDX.length,GLES20.GL_UNSIGNED_SHORT,cubeI);
    }

    private int link(String vs,String fs) {
        int v=shader(GLES20.GL_VERTEX_SHADER,vs), f=shader(GLES20.GL_FRAGMENT_SHADER,fs);
        int p=GLES20.glCreateProgram();
        GLES20.glAttachShader(p,v); GLES20.glAttachShader(p,f); GLES20.glLinkProgram(p);
        return p;
    }
    private int shader(int type,String src) {
        int s=GLES20.glCreateShader(type);
        GLES20.glShaderSource(s,src); GLES20.glCompileShader(s);
        return s;
    }
}
