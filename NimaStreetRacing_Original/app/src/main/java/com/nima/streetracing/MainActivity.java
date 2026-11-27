package com.nima.streetracing;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
    private RaceView race;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
        getWindow().setFlags(1024,1024);
        race = new RaceView(this);
        setContentView(race);
    }
    @Override protected void onPause(){ super.onPause(); if(race!=null)race.onPause(); }
    @Override protected void onResume(){ super.onResume(); if(race!=null)race.onResume(); }
}
