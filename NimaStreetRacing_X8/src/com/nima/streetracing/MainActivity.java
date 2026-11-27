package com.nima.streetracing;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;

public class MainActivity extends Activity {
    private GameView game;
    private TextView info;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        game = new GameView(this);
        FrameLayout root = new FrameLayout(this);
        root.addView(game);

        info = new TextView(this);
        info.setText("NIMA STREET RACING  |  ALPHA 0.1");
        info.setTextColor(Color.WHITE);
        info.setTextSize(10);
        info.setTypeface(Typeface.DEFAULT_BOLD);
        info.setShadowLayer(3, 1, 1, Color.BLACK);
        FrameLayout.LayoutParams ip = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.TOP | Gravity.LEFT);
        ip.setMargins(8, 5, 0, 0);
        root.addView(info, ip);

        addControl(root, "◀", Gravity.LEFT | Gravity.BOTTOM, GameView.LEFT);
        addControl(root, "▶", Gravity.LEFT | Gravity.BOTTOM, GameView.RIGHT);
        addControl(root, "BRAKE", Gravity.RIGHT | Gravity.BOTTOM, GameView.BRAKE);
        addControl(root, "GAS", Gravity.RIGHT | Gravity.BOTTOM, GameView.GAS);
        addControl(root, "N₂O", Gravity.RIGHT | Gravity.TOP, GameView.NITRO);

        setContentView(root);
    }

    private void addControl(FrameLayout root, String label, int gravity, final int control) {
        final ControlButton b = new ControlButton(this, label);
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
            control == GameView.LEFT || control == GameView.RIGHT ? 58 : 68,
            58, gravity);
        if (control == GameView.LEFT) { p.leftMargin = 12; p.bottomMargin = 12; }
        if (control == GameView.RIGHT) { p.leftMargin = 78; p.bottomMargin = 12; }
        if (control == GameView.BRAKE) { p.rightMargin = 84; p.bottomMargin = 12; }
        if (control == GameView.GAS) { p.rightMargin = 12; p.bottomMargin = 12; }
        if (control == GameView.NITRO) { p.rightMargin = 12; p.topMargin = 28; }
        b.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {
                boolean down = e.getAction() == MotionEvent.ACTION_DOWN ||
                               e.getAction() == MotionEvent.ACTION_MOVE;
                if (e.getAction() == MotionEvent.ACTION_UP ||
                    e.getAction() == MotionEvent.ACTION_CANCEL) down = false;
                game.setControl(control, down);
                return true;
            }
        });
        root.addView(b, p);
    }

    @Override protected void onPause() {
        super.onPause();
        if (game != null) game.onPause();
    }
    @Override protected void onResume() {
        super.onResume();
        if (game != null) game.onResume();
    }
}
