package com.himamis.retex.editor.android.event;

import android.view.View;

import com.himamis.retex.editor.share.event.ClickListener;

public class ClickListenerAdapter implements View.OnClickListener {

    private ClickListener mClickListener;

    public ClickListenerAdapter(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        mClickListener.onClick(0,0);
    }
}
