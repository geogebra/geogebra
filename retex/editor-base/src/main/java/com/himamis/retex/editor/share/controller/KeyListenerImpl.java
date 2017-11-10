package com.himamis.retex.editor.share.controller;

import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.util.JavaKeyCodes;

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
		case JavaKeyCodes.VK_A:
			if ((keyEvent.getKeyModifiers() & KeyEvent.CTRL_MASK) > 0) {
				editorState.selectAll();
				return true;
			}
			return false;
		case JavaKeyCodes.VK_V:
			if ((keyEvent.getKeyModifiers() & KeyEvent.CTRL_MASK) > 0) {
				inputController.paste();
				return inputController.getMathField().useCustomPaste();
			}
			return false;
		case JavaKeyCodes.VK_C:
			if ((keyEvent.getKeyModifiers() & KeyEvent.CTRL_MASK) > 0) {
				inputController.copy();
				return true;
			}
			return false;
		case JavaKeyCodes.VK_X:
			if ((keyEvent.getKeyModifiers() & KeyEvent.CTRL_MASK) > 0) {
				inputController.copy();
				InputController.deleteSelection(editorState);
				return true;
			}
			return false;
		case JavaKeyCodes.VK_ESCAPE:
			inputController.escSymbol(editorState);
			return true;
		case JavaKeyCodes.VK_HOME:
			CursorController.firstField(editorState);
			return true;
		case JavaKeyCodes.VK_END:
			CursorController.lastField(editorState);
			return true;
		case JavaKeyCodes.VK_LEFT:
			cursorController.prevCharacter(editorState);
			if ((keyEvent.getKeyModifiers() & KeyEvent.SHIFT_MASK) > 0) {
				editorState.extendSelection(true);
			} else {
				editorState.resetSelection();
			}
			return true;
		case JavaKeyCodes.VK_RIGHT:
			if (InputController.trySelectNext(editorState)) {
				return true;
			}
			cursorController.nextCharacter(editorState);
			if ((keyEvent.getKeyModifiers() & KeyEvent.SHIFT_MASK) > 0) {
				editorState.extendSelection(false);
			} else {
				editorState.resetSelection();
			}
			return true;
		case JavaKeyCodes.VK_UP:
			cursorController.upField(editorState);
			return true;
		case JavaKeyCodes.VK_DOWN:
			cursorController.downField(editorState);
			return true;
		case JavaKeyCodes.VK_DELETE:
			if (!InputController.deleteSelection(editorState)) {
				inputController.delCharacter(editorState);
			}
			return true;
		case JavaKeyCodes.VK_BACK_SPACE:
			if (!InputController.deleteSelection(editorState)) {
				inputController.bkspCharacter(editorState);
			}
			return true;
		case JavaKeyCodes.VK_SHIFT:
			return false;
		case JavaKeyCodes.VK_OPEN_BRACKET:
			return false;
		case JavaKeyCodes.VK_TAB:
			if (!InputController.trySelectNext(editorState)) {
				if (!InputController.trySelectFirst(editorState)) {
					onTab();
				};
			}
			return true;
		default:
			// InputController.deleteSelection(editorState);
			return false;
        }
    }

	public void onTab() {
		inputController.handleTab();
	}

	public boolean onKeyTyped(char ch) {
		return inputController.handleChar(editorState, ch);
	}
}
