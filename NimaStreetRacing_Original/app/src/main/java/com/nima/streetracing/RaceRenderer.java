package com.nima.streetracing;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.opengl.Matrix;
import java.nio.*;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RaceRenderer implements GLSurfaceView.Renderer {
    private final Context context; private final TouchControls controls;
    private final DrivingPhysics physics=new DrivingPhysics();
    private CarSpec selected=CarSpec.GARAGE[0];
    private int program, pos, color, mvp;
    private FloatBuffer cube;
    private final float[] projection=new float[16], view=new float[16], model=new float[16], temp=new float[16], mvpM=new float[16];
    private long last=0; private float roadScroll=0;
    private static final String VS="attribute vec3 aPosition; uniform mat4 uMVP; void main(){gl_Position=uMVP*vec4(aPosition,1.0);}";
    private static final String FS="precision mediump float; uniform vec4 uColor; void main(){gl_FragColor=uColor;}";
    public RaceRenderer(Context c,TouchControls t){context=c;controls=t; cube=makeCube();}
    public void onSurfaceCreated(GL10 gl,EGLConfig cfg){
        GLES20.glClearColor(0.28f,0.48f,0.68f,1f);GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        program=link(VS,FS);pos=GLES20.glGetAttribLocation(program,"aPosition");color=GLES20.glGetUniformLocation(program,"uColor");mvp=GLES20.glGetUniformLocation(program,"uMVP");
        last=System.nanoTime();
    }
    public void onSurfaceChanged(GL10 gl,int w,int h){GLES20.glViewport(0,0,w,h);Matrix.perspectiveM(projection,0,62f,(float)w/(float)Math.max(1,h),0.1f,140f);}
    public void onDrawFrame(GL10 gl){
        long now=System.nanoTime();float dt=Math.min(.05f,(now-last)/1000000000f);last=now;
        physics.update(dt,controls,selected);roadScroll+=physics.speed*dt*.055f;if(roadScroll>4)roadScroll-=4;
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(view,0,physics.lateral*.28f,4.4f,8.8f+physics.speed*.012f,physics.lateral*.1f,0.5f,-5f,0,1,0);
        drawRoad();drawCar(physics.lateral,0f,-2.0f,selected.red,selected.green,selected.blue,1f);
        // Simple distant traffic opponents; low draw-call count for Adreno 200.
        for(int i=0;i<3;i++){float z=-13f-i*13f;drawCar((i%2==0?1.55f:-1.55f),0,z,.72f,.12f,.08f,.72f);}
    }
    private void drawRoad(){
        box(0,-.18f,-34,7.2f,.18f,78,.18f,.19f,.21f,1);
        box(-4.0f,.0f,-34,.12f,.08f,78,.9f,.82f,.55f,1);
        box(4.0f,.0f,-34,.12f,.08f,78,.9f,.82f,.55f,1);
        for(int i=0;i<13;i++){float z=-i*6f+(roadScroll%6f);box(0,.012f,z,.10f,.02f,2.1f,.88f,.88f,.82f,1);}
        // Roadside barriers / scenery silhouettes.
        for(int i=0;i<9;i++){float z=-8-i*8;box(-5.2f,.65f,z,.65f,.65f,.65f,.18f,.26f,.30f,1);box(5.2f,.65f,z,.65f,.65f,.65f,.18f,.26f,.30f,1);}
    }
    private void drawCar(float x,float y,float z,float r,float g,float b,float scale){
        box(x,y+.48f,z,1.02f*scale,.34f*scale,2.05f*scale,r,g,b,1);
        box(x,y+.78f,z-.18f,.72f*scale,.32f*scale,.92f*scale,r*.72f,g*.72f,b*.72f,1);
        // wheels
        for(int side=-1;side<=1;side+=2)for(int axle=-1;axle<=1;axle+=2)
            box(x+side*.57f*scale,y+.23f*scale,z+axle*.65f*scale,.20f*scale,.28f*scale,.42f*scale,.035f,.035f,.04f,1);
        box(x-.36f*scale,y+.54f*scale,z+1.04f*scale,.20f*scale,.10f*scale,.05f*scale,1,.08f,.04f,1);
        box(x+.36f*scale,y+.54f*scale,z+1.04f*scale,.20f*scale,.10f*scale,.05f*scale,1,.08f,.04f,1);
    }
    private void box(float x,float y,float z,float sx,float sy,float sz,float r,float g,float b,float a){
        Matrix.setIdentityM(model,0);Matrix.translateM(model,0,x,y,z);Matrix.scaleM(model,0,sx,sy,sz);
        Matrix.multiplyMM(temp,0,view,0,model,0);Matrix.multiplyMM(mvpM,0,projection,0,temp,0);
        GLES20.glUseProgram(program);GLES20.glUniformMatrix4fv(mvp,1,false,mvpM,0);GLES20.glUniform4f(color,r,g,b,a);
        cube.position(0);GLES20.glEnableVertexAttribArray(pos);GLES20.glVertexAttribPointer(pos,3,GLES20.GL_FLOAT,false,0,cube);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,36);GLES20.glDisableVertexAttribArray(pos);
    }
    private FloatBuffer makeCube(){
        float[] v={
        -1,-1,1, 1,-1,1, 1,1,1, -1,-1,1, 1,1,1, -1,1,1,
        1,-1,-1, -1,-1,-1, -1,1,-1, 1,-1,-1, -1,1,-1, 1,1,-1,
        -1,-1,-1, -1,-1,1, -1,1,1, -1,-1,-1, -1,1,1, -1,1,-1,
        1,-1,1, 1,-1,-1, 1,1,-1, 1,-1,1, 1,1,-1, 1,1,1,
        -1,1,1, 1,1,1, 1,1,-1, -1,1,1, 1,1,-1, -1,1,-1,
        -1,-1,-1, 1,-1,-1, 1,-1,1, -1,-1,-1, 1,-1,1, -1,-1,1};
        ByteBuffer bb=ByteBuffer.allocateDirect(v.length*4).order(ByteOrder.nativeOrder());FloatBuffer fb=bb.asFloatBuffer();fb.put(v).position(0);return fb;
    }
    private int compile(int type,String src){int s=GLES20.glCreateShader(type);GLES20.glShaderSource(s,src);GLES20.glCompileShader(s);return s;}
    private int link(String v,String f){int p=GLES20.glCreateProgram();GLES20.glAttachShader(p,compile(GLES20.GL_VERTEX_SHADER,v));GLES20.glAttachShader(p,compile(GLES20.GL_FRAGMENT_SHADER,f));GLES20.glLinkProgram(p);return p;}
}
