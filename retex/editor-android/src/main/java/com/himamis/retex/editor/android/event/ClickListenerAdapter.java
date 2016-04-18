package com.himamis.retex.editor.android.event;

import android.view.MotionEvent;
import android.view.View;

import com.himamis.retex.editor.share.event.ClickListener;

public class ClickListenerAdapter implements View.OnTouchListener {

    private ClickListener mClickListener;

    public ClickListenerAdapter(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            mClickListener.onPointerDown((int) event.getX(), (int) event.getY());
        }else if (event.getAction() == MotionEvent.ACTION_UP){
            mClickListener.onPointerUp((int) event.getX(), (int) event.getY());
        }
        return false;
    }
}
