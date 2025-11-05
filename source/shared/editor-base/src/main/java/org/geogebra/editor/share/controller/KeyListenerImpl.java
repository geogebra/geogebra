/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.controller;

import java.util.function.Function;

import org.geogebra.editor.share.event.KeyEvent;
import org.geogebra.editor.share.util.JavaKeyCodes;

/**
 * Key listener
 */
public class KeyListenerImpl {

	private final InputController inputController;
	private boolean macKeysEnabled;

	/**
	 * @param inputController
	 *            input controller
	 */
	public KeyListenerImpl(InputController inputController) {
		this.inputController = inputController;
	}

	/**
	 * @param keyEvent
	 *            event
	 * @param editorState
	 *            editor state
	 * @return whether event was handled
	 */
	public boolean onKeyPressed(KeyEvent keyEvent, EditorState editorState) {
		// Ctrl, not AltGr
		boolean ctrlPressed = ((keyEvent.getKeyModifiers()
				& KeyEvent.CTRL_MASK) > 0)
				&& ((keyEvent.getKeyModifiers() & KeyEvent.ALT_MASK) == 0);
		
		boolean shiftPressed = (keyEvent.getKeyModifiers() & KeyEvent.SHIFT_MASK) > 0;

		switch (keyEvent.getKeyCode()) {
		case JavaKeyCodes.VK_A:
			if (ctrlPressed) {
				editorState.selectAll();
				return true;
			}
			return false;
		case JavaKeyCodes.VK_V:
			if (ctrlPressed) {
				inputController.paste();
				return inputController.getMathField().useCustomPaste();
			}
			return false;
		case JavaKeyCodes.VK_C:
			if (ctrlPressed) {
				inputController.copy();
				return true;
			}
			return false;
		case JavaKeyCodes.VK_O:
			if (ctrlPressed) {
				if (!editorState.isInRecurringDecimal()) {
					inputController.newFunction(editorState, "recurringDecimal", false, null);
				}
				return true;
			}
			return false;
		case JavaKeyCodes.VK_X:
			if (ctrlPressed) {
				inputController.copy();
				InputController.deleteSelection(editorState);
				return true;
			}
			return false;
		case JavaKeyCodes.VK_M:
			if (ctrlPressed && inputController.supportsMixedNumbers()) {
				inputController.addMixedNumber(editorState);
				return true;
			}
			return false;
		case JavaKeyCodes.VK_ESCAPE:
			// if math field doesn't have its own escape handler, blur it
			inputController.getMathField().blur();
			return true;
		case JavaKeyCodes.VK_HOME:
			return handleHomeKey(editorState, shiftPressed);
		case JavaKeyCodes.VK_END:
			return handleEndKey(editorState, shiftPressed);
		case JavaKeyCodes.VK_LEFT:
			if (ctrlPressed && macKeysEnabled) {
				return handleHomeKey(editorState, shiftPressed);
			}
			return handleLeftRight(editorState, true, shiftPressed);
		case JavaKeyCodes.VK_RIGHT:
			if (ctrlPressed && macKeysEnabled) {
				return handleEndKey(editorState, shiftPressed);
			}
			return handleLeftRight(editorState, false, shiftPressed);
		case JavaKeyCodes.VK_UP:
			return CursorController.upField(editorState);
		case JavaKeyCodes.VK_DOWN:
			return CursorController.downField(editorState);
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
		case JavaKeyCodes.VK_OPEN_BRACKET:
			return false;
		case JavaKeyCodes.VK_TAB:
			return onTab(shiftPressed);
		default:
			// InputController.deleteSelection(editorState);
			return false;
		}
	}

	private boolean handleEndKey(EditorState editorState, boolean shiftPressed) {
		if (shiftPressed) {
			editorState.selectToEnd();
		} else {
			CursorController.lastField(editorState);
		}
		return true;
	}

	private boolean handleHomeKey(EditorState editorState, boolean shiftPressed) {
		if (shiftPressed) {
			editorState.selectToStart();
		} else {
			CursorController.firstField(editorState);
		}
		return true;
	}

	private boolean handleLeftRight(EditorState editorState,
			boolean left, boolean shiftPressed) {
		boolean ret;
		Function<EditorState, Boolean>
				navigate = left ? CursorController::prevCharacter : CursorController::nextCharacter;
		if (shiftPressed) {
			ret = navigate.apply(editorState);
			editorState.extendSelection(left);
		} else {
			ret = editorState.updateCursorFromSelection(left) || navigate.apply(editorState);
			editorState.resetSelection();
		}
		return ret;
	}

	/**
	 * @param shiftDown
	 *            whether shift is pressed
	 * @return tab handling
	 */
	public boolean onTab(boolean shiftDown) {
		return inputController.handleTab(shiftDown);
	}

	/**
	 * @param ch
	 *            key
	 * @param editorState
	 *            current state
	 * @return whether event was handled
	 */
	public boolean onKeyTyped(char ch, EditorState editorState) {
		return inputController.handleChar(editorState, ch);
	}

	/**
	 * By default, Cmd+[keys] on Mac is handled the same as Ctrl+[keys] on Windows/Linux.
	 * This method may turn on additional handling of keyboards that use Cmd and their Ctrl-based
	 * counterpart is not needed. Pass `true` when Mac platform is detected.
	 * @param macKeysEnabled whether Mac-specific keyboard shortcuts should be enabled
	 */
	public void setMacKeysEnabled(boolean macKeysEnabled) {
		this.macKeysEnabled = macKeysEnabled;
	}
}
