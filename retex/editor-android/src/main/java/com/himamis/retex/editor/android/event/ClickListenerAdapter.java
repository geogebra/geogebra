package com.himamis.retex.editor.android.event;

import android.view.View;
import android.view.MotionEvent;

import com.himamis.retex.editor.share.event.ClickListener;

public class ClickListenerAdapter implements View.OnTouchListener {

    private ClickListener mClickListener;

    public ClickListenerAdapter(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public void onTouch(View v, MotionEvent event) {
    	if (event.getAction() == MotionEvent.ACTION_DOWN){
    		mClickListener.onPointerDown(event.getX(),event.getY());
    	}else if (event.getAction() == MotionEvent.ACTION_UP){{
    		mClickListener.onPointerUp(event.getX(),event.getY());
    	}
    }
}
