package com.himamis.retex.editor.share.input.adapter;

import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.controller.InputController;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;

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
