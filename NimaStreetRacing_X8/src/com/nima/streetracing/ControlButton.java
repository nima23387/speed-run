package com.nima.streetracing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.RectF;
import android.view.View;

public class ControlButton extends View {
    private final Paint p = new Paint(3);
    private final String label;
    private boolean pressed = false;
    public ControlButton(Context c, String text) { super(c); label = text; setFocusable(false); }
    @Override protected void onDraw(Canvas c) {
        super.onDraw(c);
        p.setColor(pressed ? 0xCCFF8A00 : 0x88505050);
        c.drawRoundRect(new RectF(2,2,getWidth()-2,getHeight()-2), 12, 12, p);
        p.setColor(Color.WHITE); p.setTextAlign(Paint.Align.CENTER); p.setTextSize(15);
        c.drawText(label, getWidth()/2, getHeight()/2 + 5, p);
    }
    @Override public boolean onTouchEvent(android.view.MotionEvent e) {
        pressed = e.getAction() != android.view.MotionEvent.ACTION_UP;
        invalidate();
        return true;
    }
}
