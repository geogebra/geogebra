/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.input.adapter;

import org.geogebra.editor.share.controller.EditorState;
import org.geogebra.editor.share.controller.InputController;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.input.KeyboardInputAdapter;

public abstract class StringInput implements KeyboardAdapter {

    private String keyboard;

    public StringInput() {
        this(null);
    }

    public StringInput(String keyboard) {
        this.keyboard = keyboard;
    }

    @Override
    public boolean test(String input) {
        return keyboard.equals(input);
    }

    protected static boolean plainTextMode(MathFieldInternal mfi) {
        return mfi.getInputController().getPlainTextMode()
                || mfi.getEditorState().isInsideQuotes();
    }

    protected static void commitFunction(MathFieldInternal mfi, String function) {
        if (plainTextMode(mfi)) {
            KeyboardInputAdapter.type(mfi, function + "()");
        } else {
            EditorState editorState = mfi.getEditorState();
            InputController inputController = mfi.getInputController();
            inputController.newFunction(editorState, function);
			mfi.notifyAndUpdate(function);
        }
    }
}
