package com.himamis.retex.editor.android.event;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.himamis.retex.editor.android.FormulaEditor;
import com.himamis.retex.editor.share.event.ClickListener;

public class ClickListenerAdapter implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private ClickListener mClickListener;

    private GestureDetector mGestureDetector;

    int shiftX = 0;

    public ClickListenerAdapter(ClickListener clickListener, Context context) {
        mClickListener = clickListener;
        mGestureDetector = new GestureDetector(context, this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v instanceof FormulaEditor){
            shiftX = ((FormulaEditor) v).getShiftX();
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            mClickListener.onPointerDown((int)(event.getX()-shiftX), (int) event.getY());
        }else if (event.getAction() == MotionEvent.ACTION_UP){
            mClickListener.onPointerUp((int) event.getX()-shiftX, (int) event.getY());
        }

        mGestureDetector.onTouchEvent(event);

        return true;
    }


    /////////////////////////////
    // GestureDetector
    /////////////////////////////

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mClickListener.onScroll((int) distanceX, (int) distanceY);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        mClickListener.onLongPress((int) event.getX(), (int) event.getY());
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
