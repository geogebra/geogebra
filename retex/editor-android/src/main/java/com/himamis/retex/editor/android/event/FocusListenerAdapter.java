package com.himamis.retex.editor.android.event;

import com.himamis.retex.editor.share.event.FocusListener;

import android.view.View;

public class FocusListenerAdapter implements View.OnFocusChangeListener {

    private FocusListener mFocusListener;

    public FocusListenerAdapter(FocusListener focusListener) {
        mFocusListener = focusListener;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mFocusListener.onFocusGained();
        } else {
            mFocusListener.onFocusLost();
        }
    }
}
