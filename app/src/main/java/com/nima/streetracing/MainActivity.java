package com.nima.streetracing;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private FrameLayout root;
    private GameView game;
    private LinearLayout overlay;
    private TextView title, subtitle;
    private int selectedCar = 0;
    private String[] cars = {"NIMA GT-R", "NIMA MUSCLE", "NIMA SPORT"};
    private int screen = 0;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setFlags(1024, 1024);
        root = new FrameLayout(this);
        game = new GameView(this);
        root.addView(game);
        showMenu();
        setContentView(root);
    }

    private TextView text(String value, int size) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextColor(Color.WHITE); t.setTextSize(size);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        t.setGravity(Gravity.CENTER);
        return t;
    }

    private Button button(String label, View.OnClickListener click) {
        Button b = new Button(this);
        b.setText(label); b.setTextSize(14);
        b.setTextColor(Color.WHITE);
        b.setBackgroundColor(0xDDC94B16);
        b.setOnClickListener(click);
        return b;
    }

    private void clearOverlay() {
        root.removeAllViews();
        root.addView(game);
        overlay = new LinearLayout(this);
        overlay.setOrientation(LinearLayout.VERTICAL);
        overlay.setGravity(Gravity.CENTER);
        overlay.setPadding(18, 8, 18, 8);
        overlay.setBackgroundColor(0xB9101722);
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        root.addView(overlay, p);
    }

    private void showMenu() {
        screen = 0; game.pauseGame(); clearOverlay();
        title = text("NIMA STREET RACING", 26);
        subtitle = text("3D STREET DRIVING  •  X8 EDITION", 12);
        overlay.addView(title);
        overlay.addView(subtitle);
        overlay.addView(button("START DRIVING", new View.OnClickListener() {
            public void onClick(View v) { startDrive(false); }
        }));
        overlay.addView(button("STREET RACE", new View.OnClickListener() {
            public void onClick(View v) { startDrive(true); }
        }));
        overlay.addView(button("GARAGE  /  " + cars[selectedCar], new View.OnClickListener() {
            public void onClick(View v) { showGarage(); }
        }));
        overlay.addView(button("SETTINGS & INFO", new View.OnClickListener() {
            public void onClick(View v) { showInfo(); }
        }));
    }

    private void showGarage() {
        screen = 1; game.pauseGame(); clearOverlay();
        overlay.addView(text("GARAGE", 25));
        overlay.addView(text("Choose your ride", 13));
        for (int i=0;i<cars.length;i++) {
            final int n=i;
            overlay.addView(button((i==selectedCar ? "✓  " : "") + cars[i],
                new View.OnClickListener() {
                    public void onClick(View v) {
                        selectedCar=n; game.setCar(n); showGarage();
                    }
                }));
        }
        overlay.addView(button("BACK TO MAIN MENU", new View.OnClickListener() {
            public void onClick(View v) { showMenu(); }
        }));
    }

    private void showInfo() {
        screen=2; game.pauseGame(); clearOverlay();
        overlay.addView(text("NIMA STREET RACING", 22));
        overlay.addView(text("Prototype 1.0.0  |  OpenGL ES 2.0", 13));
        overlay.addView(text("Optimized 3D street driving for Android 2.3.7+", 12));
        overlay.addView(text("Use steering, GAS, BRAKE and N2O. This build uses lightweight placeholder art.", 11));
        overlay.addView(button("BACK", new View.OnClickListener() {
            public void onClick(View v) { showMenu(); }
        }));
    }

    private void startDrive(boolean race) {
        if (overlay != null) root.removeView(overlay);
        overlay=null; screen=3;
        game.setRace(race);
        game.resumeGame();
        addHudButton("MENU", Gravity.TOP|Gravity.LEFT, 72, 42, new View.OnClickListener() {
            public void onClick(View v) { showMenu(); }
        });
        addControl("◀", GameView.LEFT, Gravity.BOTTOM|Gravity.LEFT, 62, 58, 8, 8);
        addControl("▶", GameView.RIGHT, Gravity.BOTTOM|Gravity.LEFT, 62, 58, 78, 8);
        addControl("BRAKE", GameView.BRAKE, Gravity.BOTTOM|Gravity.RIGHT, 76, 58, 88, 8);
        addControl("GAS", GameView.GAS, Gravity.BOTTOM|Gravity.RIGHT, 76, 58, 8, 8);
        addControl("N₂O", GameView.NITRO, Gravity.TOP|Gravity.RIGHT, 68, 48, 8, 8);
    }

    private void addHudButton(String label, int gravity, int w, int h, View.OnClickListener l) {
        Button b=button(label,l);
        FrameLayout.LayoutParams p=new FrameLayout.LayoutParams(w,h,gravity);
        p.leftMargin=8; p.topMargin=8; root.addView(b,p);
    }

    private void addControl(String label, final int key, int gravity, int w, int h, int x, int y) {
        final Button b=button(label,null);
        FrameLayout.LayoutParams p=new FrameLayout.LayoutParams(w,h,gravity);
        if ((gravity & Gravity.RIGHT)==Gravity.RIGHT) p.rightMargin=x;
        else p.leftMargin=x;
        if ((gravity & Gravity.BOTTOM)==Gravity.BOTTOM) p.bottomMargin=y;
        else p.topMargin=y;
        b.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {
                int a=e.getAction();
                boolean down=(a==MotionEvent.ACTION_DOWN || a==MotionEvent.ACTION_MOVE);
                if (a==MotionEvent.ACTION_UP || a==MotionEvent.ACTION_CANCEL) down=false;
                game.setControl(key,down);
                return true;
            }
        });
        root.addView(b,p);
    }

    public void onBackPressed() {
        if (screen==3) showMenu(); else if(screen!=0) showMenu(); else super.onBackPressed();
    }
    protected void onPause() { super.onPause(); if(game!=null) game.onPause(); }
    protected void onResume() { super.onResume(); if(game!=null && screen==3) game.onResume(); }
}
