package com.himamis.retex.editor.share.input.adapter;

import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.controller.InputController;
import com.himamis.retex.editor.share.editor.MathFieldInternal;

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

    protected static void commitFunction(MathFieldInternal mfi, String function) {
        EditorState editorState = mfi.getEditorState();
        InputController inputController = mfi.getInputController();
        inputController.newFunction(editorState, function);
        mfi.notifyAndUpdate(function);
    }
}
