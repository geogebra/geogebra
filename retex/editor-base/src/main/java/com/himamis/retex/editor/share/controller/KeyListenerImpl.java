package com.himamis.retex.editor.share.controller;

import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.event.KeyListener;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathContainer;

public class KeyListenerImpl implements KeyListener {

    private EditorState editorState;
    private CursorController cursorController;
    private InputController inputController;

    public KeyListenerImpl(CursorController cursorController, InputController inputController) {
        this.cursorController = cursorController;
        this.inputController = inputController;
    }

    public void setEditorState(EditorState editorState) {
        this.editorState = editorState;
    }

    @Override
    public boolean onKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                inputController.escSymbol(editorState);
                return true;
            case KeyEvent.VK_HOME:
                cursorController.firstField(editorState);
                return true;
            case KeyEvent.VK_END:
                cursorController.lastField(editorState);
                return true;
            case KeyEvent.VK_LEFT:
                cursorController.prevCharacter(editorState);
                return true;
            case KeyEvent.VK_RIGHT:
                cursorController.nextCharacter(editorState);
                return true;
            case KeyEvent.VK_UP:
                cursorController.upField(editorState);
                return true;
            case KeyEvent.VK_DOWN:
                cursorController.downField(editorState);
                return true;
            case KeyEvent.VK_DELETE:
                inputController.delCharacter(editorState);
                return true;
            case KeyEvent.VK_BACK_SPACE:
                inputController.bkspCharacter(editorState);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onKeyReleased(KeyEvent keyEvent) {
        return false;
    }

    @Override
    public boolean onKeyTyped(KeyEvent keyEvent) {
        char ch = keyEvent.getUnicodeKeyChar();
        MetaModel metaModel = editorState.getMetaModel();
        boolean handled = false;

        if (isArrayCloseKey(ch) || ch == InputController.FUNCTION_CLOSE_KEY) {
            inputController.endField(editorState, ch);
            handled = true;
        } else if (metaModel.isFunctionOpenKey(ch)) {
            inputController.newBraces(editorState, ch);
            handled = true;
        } else if (ch == '^') {
            inputController.newScript(editorState, "^");
            handled = true;
        } else if (ch == '_') {
            inputController.newScript(editorState, "_");
            handled = true;
        } else if (ch == '\\') {
            inputController.newFunction(editorState, "frac", 1);
            handled = true;
        } else if (metaModel.isArrayOpenKey(ch)) {
            inputController.newArray(editorState, 1, ch);
            handled = true;
        } else if (metaModel.isOperator("" + ch)) {
            inputController.newOperator(editorState, ch);
            handled = true;
        } else if (metaModel.isSymbol("" + ch)) {
            inputController.newSymbol(editorState, ch);
            handled = true;
        } else if (metaModel.isCharacter("" + ch)) {
            inputController.newCharacter(editorState, ch);
            handled = true;
        }
        return handled;
    }

    private boolean isArrayCloseKey(char key) {
        MathContainer parent = editorState.getCurrentField().getParent();
        if (parent != null && parent instanceof MathArray) {
            MathArray array = (MathArray) parent;
            return array.getCloseKey() == key;
        }
        return false;
    }
}
