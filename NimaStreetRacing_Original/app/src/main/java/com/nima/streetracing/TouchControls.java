package com.nima.streetracing;

import android.view.MotionEvent;
import java.util.HashMap;

public class TouchControls {
    public volatile float steer=0f, throttle=0f, brake=0f, handbrake=0f, nitro=0f;
    private final HashMap<Integer,Integer> roles = new HashMap<Integer,Integer>();
    public void onTouch(MotionEvent e,int w,int h) {
        int a=e.getActionMasked(), idx=e.getActionIndex(), id=e.getPointerId(idx);
        if(a==MotionEvent.ACTION_DOWN || a==MotionEvent.ACTION_POINTER_DOWN) {
            float x=e.getX(idx), y=e.getY(idx);
            int role;
            if(x < w*.42f) role=1;
            else if(y > h*.58f && x>w*.70f) role=2;
            else if(y < h*.58f && x>w*.70f) role=3;
            else role=4;
            roles.put(id,role);
        } else if(a==MotionEvent.ACTION_UP || a==MotionEvent.ACTION_POINTER_UP || a==MotionEvent.ACTION_CANCEL) {
            roles.remove(id);
        }
        float s=0,t=0,b=0,hb=0,n=0;
        for(int i=0;i<e.getPointerCount();i++) {
            int r=roles.containsKey(e.getPointerId(i))?roles.get(e.getPointerId(i)):0;
            float x=e.getX(i), y=e.getY(i);
            if(r==1) s=Math.max(-1f,Math.min(1f,(x-w*.22f)/(w*.20f)));
            if(r==2) t=1f;
            if(r==3) b=1f;
            if(r==4) { if(y<h*.40f)n=1f; else hb=1f; }
        }
        steer=s; throttle=t; brake=b; handbrake=hb; nitro=n;
    }
}
