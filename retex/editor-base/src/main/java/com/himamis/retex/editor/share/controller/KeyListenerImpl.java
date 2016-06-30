package com.himamis.retex.editor.share.controller;

import com.himamis.retex.editor.share.event.KeyEvent;

public class KeyListenerImpl {

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

    public boolean onKeyPressed(KeyEvent keyEvent) {
		switch (keyEvent.getKeyCode()) {
		case KeyEvent.VK_A:
			if ((keyEvent.getKeyModifiers() & KeyEvent.CTRL_MASK) > 0) {
				editorState.selectAll();
				return true;
			}
			return false;
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
			if ((keyEvent.getKeyModifiers() & KeyEvent.SHIFT_MASK) > 0) {
				editorState.extendSelection(true);
			} else {
				editorState.resetSelection();
			}
			return true;
		case KeyEvent.VK_RIGHT:
			cursorController.nextCharacter(editorState);
			if ((keyEvent.getKeyModifiers() & KeyEvent.SHIFT_MASK) > 0) {
				editorState.extendSelection(false);
			} else {
				editorState.resetSelection();
			}
			return true;
		case KeyEvent.VK_UP:
			cursorController.upField(editorState);
			return true;
		case KeyEvent.VK_DOWN:
			cursorController.downField(editorState);
			return true;
		case KeyEvent.VK_DELETE:
			if (!inputController.deleteSelection(editorState)) {
				inputController.delCharacter(editorState);
			}
			return true;
		case KeyEvent.VK_BACK_SPACE:
			if (!inputController.deleteSelection(editorState)) {
				inputController.bkspCharacter(editorState);
			}
			return true;
		case KeyEvent.VK_SHIFT:
			return false;
		default:
			inputController.deleteSelection(editorState);
			return false;
        }
    }

	public boolean onKeyTyped(char ch) {
		return inputController.handleChar(editorState, ch);
	}

}
