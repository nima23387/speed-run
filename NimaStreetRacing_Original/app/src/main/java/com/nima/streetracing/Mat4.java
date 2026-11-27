package com.nima.streetracing;

public final class Mat4 {
    public static void identity(float[] m){for(int i=0;i<16;i++)m[i]=0;m[0]=m[5]=m[10]=m[15]=1;}
    public static void perspective(float[] m,float fovy,float aspect,float near,float far){
        for(int i=0;i<16;i++)m[i]=0;
        float f=(float)(1.0/Math.tan(fovy*Math.PI/360.0));
        m[0]=f/aspect;m[5]=f;m[10]=(far+near)/(near-far);m[11]=-1;m[14]=(2*far*near)/(near-far);
    }
    public static void look(float[] m,float ex,float ey,float ez,float cx,float cy,float cz){
        float fx=cx-ex,fy=cy-ey,fz=cz-ez;float fl=norm(fx,fy,fz);fx/=fl;fy/=fl;fz/=fl;
        float sx=fy*0-fz*1,sy=fz*0-fx*0,sz=fx*1-fy*0;float sl=norm(sx,sy,sz);sx/=sl;sy/=sl;sz/=sl;
        float ux=sy*fz-sz*fy,uy=sz*fx-sx*fz,uz=sx*fy-sy*fx;
        identity(m);m[0]=sx;m[4]=sy;m[8]=sz;m[1]=ux;m[5]=uy;m[9]=uz;m[2]=-fx;m[6]=-fy;m[10]=-fz;
        m[12]=-(sx*ex+sy*ey+sz*ez);m[13]=-(ux*ex+uy*ey+uz*ez);m[14]=fx*ex+fy*ey+fz*ez;
    }
    private static float norm(float x,float y,float z){return (float)Math.sqrt(x*x+y*y+z*z);}
    public static void multiply(float[] out,float[] a,float[] b){
        float[] t=new float[16];
        for(int r=0;r<4;r++)for(int c=0;c<4;c++)t[c*4+r]=a[r]*b[c*4]+a[4+r]*b[c*4+1]+a[8+r]*b[c*4+2]+a[12+r]*b[c*4+3];
        System.arraycopy(t,0,out,0,16);
    }
}
