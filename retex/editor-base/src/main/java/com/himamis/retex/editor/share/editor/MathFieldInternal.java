/* JMathField.java
 * =========================================================================
 * This file is part of the Mirai Math TN - http://mirai.sourceforge.net
 *
 * Copyright (C) 2008-2009 Bea Petrovicova
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 */

package com.himamis.retex.editor.share.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.controller.CursorController;
import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.controller.InputController;
import com.himamis.retex.editor.share.controller.KeyListenerImpl;
import com.himamis.retex.editor.share.controller.MathFieldController;
import com.himamis.retex.editor.share.event.ClickListener;
import com.himamis.retex.editor.share.event.FocusListener;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.event.KeyListener;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.share.util.AltKeys;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.renderer.share.CursorBox;
import com.himamis.retex.renderer.share.SelectionBox;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * This class is a Math Field. Displays and allows to edit single formula.
 *
 * @author Bea Petrovicova, Bencze Balazs
 */
public class MathFieldInternal
		implements KeyListener, FocusListener, ClickListener {

	@Weak
	private MathField mathField;

	private CursorController cursorController;
	private InputController inputController;
	private MathFieldController mathFieldController;

	private KeyListenerImpl keyListener;
	private EditorState editorState;

	private MathFormula mathFormula;

	private int[] mouseDownPos;

	private boolean selectionDrag;

	private MathFieldListener listener;

	private boolean enterPressed;

	private Runnable enterCallback;

	private boolean directFormulaBuilder;
	private boolean scrollOccured = false;

	private boolean longPressOccured = false;

	private boolean selectionMode = false;

	private static final ArrayList<Integer> LOCKED_CARET_PATH
			= new ArrayList<>(Arrays.asList(0, 0, 0));

	/**
	 * @param mathField
	 *            editor component
	 */
	public MathFieldInternal(MathField mathField) {
		this(mathField, false);
	}

	/**
	 * @param mathField
	 *            editor component
	 * @param directFormulaBuilder
	 *            whether to create JLM atoms directly (experimental)
	 */
	public MathFieldInternal(MathField mathField, boolean directFormulaBuilder) {
		this.mathField = mathField;
		this.directFormulaBuilder = directFormulaBuilder;
		cursorController = new CursorController();
		inputController = new InputController(mathField.getMetaModel());
		keyListener = new KeyListenerImpl(cursorController, inputController);
		mathFormula = MathFormula.newFormula(mathField.getMetaModel());
		mathFieldController = new MathFieldController(mathField, directFormulaBuilder);
		inputController.setMathField(mathField);
		setupMathField();
	}

	public void setSyntaxAdapter(SyntaxAdapter syntaxAdapter) {
		mathFieldController.setSyntaxAdapter(syntaxAdapter);
	}

	private void setupMathField() {
		mathField.setFocusListener(this);
		mathField.setClickListener(this);
		mathField.setKeyListener(this);
	}

	/**
	 * @param size
	 *            font size
	 */
	public void setSize(double size) {
		mathFieldController.setSize(size);
	}

	/**
	 * Update font size and update UI if needed
	 * @param size font size in pixels
	 */
	public void setSizeAndUpdate(double size) {
		if (size != mathFieldController.getFontSize()) {
			mathFieldController.setSize(size);
			update();
		}
	}

	/**
	 * @param type
	 *            font type
	 */
	public void setType(int type) {
		mathFieldController.setType(type);
	}

	/**
	 * font type
	 * @param type font type
	 */
	public void setFontAndUpdate(int type) {
		if (type != mathFieldController.getFontType()) {
			mathFieldController.setType(type);
			update();
		}
	}

	/**
	 * @return edited formula
	 */
	public MathFormula getFormula() {
		return mathFormula;
	}

	/**
	 * @param formula
	 *            formula
	 */
	public void setFormula(MathFormula formula) {
		mathFormula = formula;
		editorState = new EditorState(mathField.getMetaModel());
		editorState.setRootComponent(formula.getRootComponent());
		editorState.setCurrentField(formula.getRootComponent());
		editorState.setCurrentOffset(editorState.getCurrentField().size());
		mathFieldController.update(formula, editorState, false);
	}

	public void setLockedCaretPath() {
		setCaretPath(LOCKED_CARET_PATH);
	}

	/**
	 * @param formula
	 *            formula
	 * @param path
	 *            indices of subtrees that contain the cursor
	 */
	public void setFormula(MathFormula formula, ArrayList<Integer> path) {
		mathFormula = formula;
		editorState = new EditorState(mathField.getMetaModel());
		editorState.setRootComponent(formula.getRootComponent());
		CursorController.setPath(path, getEditorState());
		mathFieldController.update(mathFormula, editorState, false);
	}

	/**
	 * @param path
	 *            caret path
	 */
	public void setCaretPath(ArrayList<Integer> path) {
		CursorController.setPath(path, getEditorState());
		mathFieldController.updateWithCursor(mathFormula, editorState);
	}

	/**
	 * @return input controller
	 */
	public InputController getInputController() {
		return inputController;
	}

	/**
	 * @return cursor controller
	 */
	public CursorController getCursorController() {
		return cursorController;
	}

	public MathFieldController getMathFieldController() {
		return mathFieldController;
	}

	/**
	 * @return editor state
	 */
	public EditorState getEditorState() {
		return editorState;
	}

	/**
	 * @return key listener
	 */
	public KeyListenerImpl getKeyListener() {
		return keyListener;
	}

	public void update() {
		update(false);
	}

	private void update(boolean focusEvent) {
		mathFieldController.update(mathFormula, editorState, focusEvent);
	}

	@Override
	public void onFocusGained() {
		update(true);
	}

	@Override
	public void onFocusLost() {
		update(true);
	}

	@Override
	public boolean onKeyPressed(KeyEvent keyEvent) {
		if (keyEvent.getKeyCode() == 13 || keyEvent.getKeyCode() == 10) {
			if (listener != null) {
				this.enterPressed = true;
				listener.onEnter();
				return true;
			}
		}
		boolean arrow = false;
		if (keyEvent.getKeyCode() >= 37 && keyEvent.getKeyCode() <= 40) {
			// move cursor
			arrow = true;
			if (listener != null) {
				listener.onCursorMove();
				if (keyEvent.getKeyCode() == JavaKeyCodes.VK_UP) {
					listener.onUpKeyPressed();
				} else if (keyEvent.getKeyCode() == JavaKeyCodes.VK_DOWN) {
					listener.onDownKeyPressed();
				}
			}
		}
		if (keyEvent.getKeyCode() == JavaKeyCodes.VK_CONTROL) {
			return false;
		}
		if (keyEvent.getKeyCode() == JavaKeyCodes.VK_ESCAPE) {
			if (listener != null && listener.onEscape()) {
				return true;
			}
		}
		boolean tab = keyEvent.getKeyCode() == JavaKeyCodes.VK_TAB;
		boolean handled = keyListener.onKeyPressed(keyEvent, editorState);
		if (handled && !tab) {
			update();
			if (!arrow && listener != null) {
				listener.onKeyTyped(null);
			}
		}

		return handled;
	}

	@Override
	public boolean onKeyReleased(KeyEvent keyEvent) {
		enterPressed = false;
		if (keyEvent.getKeyCode() == 13 || keyEvent.getKeyCode() == 10) {
			if (enterCallback != null) {
				enterCallback.run();
				enterCallback = null;
				return true;
			}
		}
		boolean alt = (keyEvent.getKeyModifiers() & KeyEvent.ALT_MASK) > 0
				&& (keyEvent.getKeyModifiers() & KeyEvent.CTRL_MASK) == 0;
		if (alt) {

			int keyCode = keyEvent.getKeyCode();

			// eg Alt-94 for ^ with NumLock On
			if (keyCode >= JavaKeyCodes.VK_NUMPAD0
					&& keyCode <= JavaKeyCodes.VK_NUMPAD9) {
				return false;
			}

			String str = AltKeys.getAltSymbols(keyCode,
					(keyEvent.getKeyModifiers() & KeyEvent.SHIFT_MASK) > 0,
					true);

			for (int i = 0; str != null && i < str.length(); i++) {
				keyListener.onKeyTyped(str.charAt(i), editorState);
			}
			notifyAndUpdate(str);
		}
		return false;
	}

	@Override
	public boolean onKeyTyped(KeyEvent keyEvent) {
		return onKeyTyped(keyEvent, true);
	}

	@Override
	public boolean onKeyTyped(KeyEvent keyEvent, boolean fire) {
		boolean alt = (keyEvent.getKeyModifiers() & KeyEvent.ALT_MASK) > 0;
		boolean enter = keyEvent.getUnicodeKeyChar() == (char) 13
				|| keyEvent.getUnicodeKeyChar() == (char) 10;

		boolean handled = alt || enter
				|| ((keyEvent.getKeyModifiers() & KeyEvent.CTRL_MASK) > 0)
				|| keyListener.onKeyTyped(keyEvent.getUnicodeKeyChar(),
						editorState);
		if (handled && fire) {
			notifyAndUpdate(String.valueOf(keyEvent.getUnicodeKeyChar()));
		}
		return handled;
	}

	/**
	 * @param plainText
	 *            whether to use this as plain text input
	 */
	public void setPlainTextMode(boolean plainText) {
		this.inputController.setCreateFrac(!plainText);
	}

	/**
	 * Notifies listener about key event and updates the view.
	 * @param key key name
	 */
	public void notifyAndUpdate(String key) {
		if (listener != null) {
			listener.onKeyTyped(key);
		}
		update();
	}

	@Override
	public void onPointerDown(int x, int y) {
		if (selectionMode) {
			ArrayList<Integer> list = new ArrayList<>();
			if (SelectionBox.touchSelection) {
				if (length(SelectionBox.startX - x,
						SelectionBox.startY - y) < 10) {
					editorState.cursorToSelectionEnd();
					selectionDrag = true;
					return;
				}
				if (length(SelectionBox.endX - x, SelectionBox.endY - y) < 10) {
					// editorState.anchor(true);
					selectionDrag = true;
					editorState.cursorToSelectionStart();
					return;
				}
			}
			mathFieldController.getPath(mathFormula, x, y, list);
			editorState.resetSelection();

			this.mouseDownPos = new int[] { x, y };

			moveToSelection(x, y);

			mathFieldController.update(mathFormula, editorState, false);
		}

	}

	private static double length(double d, double e) {
		return Math.sqrt(d * d + e * e);
	}

	@Override
	public void onPointerUp(int x, int y) {

		if (scrollOccured) {
			scrollOccured = false;
		} else if (longPressOccured) {
			longPressOccured = false;
		} else {
			if (this.selectionDrag) {
				selectionDrag = false;
				return;
			}
			ArrayList<Integer> list = new ArrayList<>();
			mathFieldController.getPath(mathFormula, x, y, list);
			MathComponent cursor = editorState.getCursorField(
					editorState.getSelectionEnd() != null && selectionLeft(x));

			moveToSelection(x, y);

			editorState.resetSelection();

			if (selectionMode && mousePositionChanged(x, y)) {
				editorState.extendSelection(selectionLeft(x));
				editorState.extendSelection(cursor);
			}

			// TODO only hide copy button only when no selection
			// (see commented below, MOB-567 and MOB-568)
			mathField.hideCopyPasteButtons();
			/*
			 * if (!editorState.hasSelection()){ mathField.hideCopyButton(); }
			 */

			mathFieldController.update(mathFormula, editorState, false);

			mathField.showKeyboard();
			mathField.requestViewFocus();
		}

		mouseDownPos = null;
	}

	private static boolean selectionLeft(int x) {
		return x > SelectionBox.startX;
	}

	@Override
	public void onLongPress(int x, int y) {
		longPressOccured = true;
		if (!mathFormula.isEmpty()) {
			editorState.selectAll();
		}
		mathFieldController.update(mathFormula, editorState, false);
		mathField.showCopyPasteButtons();
		mathField.showKeyboard();
		mathField.requestViewFocus();
	}

	@Override
	public void onScroll(int dx, int dy) {
		if (!selectionMode) {
			mathField.scroll(dx, dy);
			scrollOccured = true;
		}
		mathField.requestViewFocus();
	}

	/**
	 * says if dragging over should select or swype
	 * 
	 * @param flag
	 *            flag
	 */
	public void setSelectionMode(boolean flag) {
		selectionMode = flag;
	}

	private boolean mousePositionChanged(int x, int y) {
		return mouseDownPos != null && (Math.abs(x - mouseDownPos[0]) > 10
				|| Math.abs(y - mouseDownPos[1]) > 10);
	}

	private void moveToSelectionDirect(int x, int y) {
		ArrayList<Integer> list2 = new ArrayList<>();
		EditorState mc = mathFieldController.getPath(mathFormula, x, y, list2);
		if (mc != null && mc.getCurrentField() != null) {
			editorState.setCurrentField(mc.getCurrentField());
			editorState.setCurrentOffset(mc.getCurrentOffset());
		}
	}

	private void moveToSelection(int x, int y) {
		if (this.directFormulaBuilder) {
			this.moveToSelectionDirect(x, y);
		} else {
			this.moveToSelectionIterative(x, y);
		}
	}

	private void moveToSelectionIterative(int x, int y) {
		CursorController.firstField(editorState);
		double dist = Integer.MAX_VALUE;
		MathSequence closestComponent = null;
		int closestOffset = -1;
		do {
			ArrayList<Integer> list2 = new ArrayList<>();
			mathFieldController.getSelectedPath(mathFormula, list2,
					editorState.getCurrentField(),
					editorState.getCurrentOffset());
			reverse(list2);
			double currentDist = Math.abs(x - CursorBox.startX)
					+ Math.abs(y - CursorBox.startY);
			if (currentDist < dist) {
				dist = currentDist;
				closestComponent = editorState.getCurrentField();
				closestOffset = editorState.getCurrentOffset();
			}
		} while (CursorController.nextCharacter(editorState));
		if (closestComponent != null) {
			editorState.setCurrentField(closestComponent);
			editorState.setCurrentOffset(closestOffset);

			ArrayList<Integer> list2 = new ArrayList<>();
			mathFieldController.getSelectedPath(mathFormula, list2,
					editorState.getCurrentField(),
					editorState.getCurrentOffset());
		}
	}

	private static void reverse(ArrayList<Integer> list2) {
		Collections.reverse(list2);
	}

	@Override
	public void onPointerMove(int x, int y) {
		if (!mousePositionChanged(x, y) && !selectionDrag) {
			editorState.resetSelection();
			mathFieldController.update(mathFormula, editorState, false);
			return;
		}
		ArrayList<Integer> list = new ArrayList<>();
		mathFieldController.getPath(mathFormula, x, y, list);
		MathComponent cursor = editorState.getCursorField(
				editorState.getSelectionEnd() == null || selectionLeft(x));
		MathSequence current = editorState.getCurrentField();
		int offset = editorState.getCurrentOffset();
		moveToSelection(x, y);

		editorState.resetSelection();
		editorState.extendSelection(selectionLeft(x));
		editorState.setCurrentField(current);
		editorState.setCurrentOffset(offset);
		editorState.extendSelection(cursor);

		mathFieldController.update(mathFormula, editorState, false);

	}

	/**
	 * @return whwther current formula is empty
	 */
	public boolean isEmpty() {
		return mathFormula.isEmpty();
	}

	/**
	 * @param listener
	 *            listener
	 */
	public void setFieldListener(MathFieldListener listener) {
		this.listener = listener;

	}

	/**
	 * Delete the letters left from the cursor.
	 * 
	 */
	public void deleteCurrentWord() {
		MathSequence sel = editorState.getCurrentField();
		if (sel != null) {
			for (int i = Math.min(editorState.getCurrentOffset() - 1,
					sel.size() - 1); i >= 0; i--) {
				if (sel.getArgument(i) instanceof MathCharacter) {
					if (!((MathCharacter) sel.getArgument(i)).isCharacter()) {
						return;
					}
					sel.removeArgument(i);
					editorState.decCurrentOffset();
				}
			}
		}
	}

	/**
	 * @return sequence of letters left from the cursor (or selection end).
	 */
	public String getCurrentWord() {
		StringBuilder str = new StringBuilder(" ");
		MathSequence sel = editorState.getCurrentField();
		if (sel != null) {
			int wordEnd = editorState.getCurrentOffset() - 1;
			if (editorState.getSelectionEnd() != null) {
				wordEnd = editorState.getSelectionEnd().getParentIndex();
			}
			for (int i = Math.min(wordEnd, sel.size() - 1); i >= 0; i--) {

				if (!appendChar(str, sel, i)) {
					break;
				}
			}
		}
		return str.reverse().toString().trim();
	}

	/**
	 * @param str
	 *            string builder
	 * @param sel
	 *            formula part
	 * @param i
	 *            index
	 * @return whether char is a part of a word
	 */
	public static boolean appendChar(StringBuilder str, MathSequence sel,
			int i) {
		if (sel.getArgument(i) instanceof MathCharacter) {
			if (!((MathCharacter) sel.getArgument(i)).isCharacter()) {
				return false;
			}
			str.append(((MathCharacter) sel.getArgument(i)).getUnicode());
			return true;
		}
		return false;
	}

	/**
	 * Select next argument of a function.
	 */
	public void selectNextArgument() {
		EditorState state = getEditorState();
		MathSequence seq = state.getCurrentField();

		if (seq != null && seq.size() > 0) {
			MathComponent last = seq.getArgument(state.getCurrentOffset() - 1);
			if (last instanceof MathFunction
					&& ((MathFunction) last).size() > 0) {
				// log10: sizse 1, select 0 sin: size 2 select 1
				MathSequence args = ((MathFunction) last)
						.getArgument(((MathFunction) last).size() - 1);
				if (InputController.doSelectNext(args, state, 0)) {
					update();
				}
			}
		}
	}

	/**
	 * @return serialized selection
	 */
	public String copy() {
		return GeoGebraSerializer.serialize(
					InputController.getSelectionText(getEditorState()));
	}

	/**
	 * Insert string callback.
	 */
	public void onInsertString() {
		if (!this.getInputController().getCreateFrac()) {
			insertStringFinished();
			return;
		}
		ArrayList<Integer> path = new ArrayList<>();
		path.add(getEditorState().getCurrentOffset()
				- getEditorState().getCurrentField().size());
		MathContainer field = getEditorState().getCurrentField();
		while (field != null) {
			if (field.getParent() != null) {
				path.add(field.getParentIndex() - field.getParent().size());
			}
			field = field.getParent();
		}
		reverse(path);
		setFormula(GeoGebraSerializer.reparse(getFormula()));
		if (listener != null) {
			listener.onInsertString();
		}
		getMathFieldController().setSelectedPath(getFormula(), path,
				getEditorState());
		insertStringFinished();
	}

	private void insertStringFinished() {
		if (mathField instanceof MathFieldAsync) {
			((MathFieldAsync) mathField).requestViewFocus(new Runnable() {
				@Override
				public void run() {
					onKeyTyped();
				}
			});
		} else {
			mathField.requestViewFocus();
			// do this as late as possible
			onKeyTyped();
		}
	}

	/**
	 * Trigger the listener
	 */
	protected void onKeyTyped() {
		if (listener != null) {
			listener.onKeyTyped(null);
		}
	}

	/**
	 * Insert a function
	 * 
	 * @param text
	 *            function name
	 */
	public void insertFunction(String text) {
		inputController.newFunction(editorState, text, false, null);
		onKeyTyped();
	}

	/**
	 * Run callback after enter is released.
	 * 
	 * @param r
	 *            callback
	 */
	public void checkEnterReleased(Runnable r) {
		if (this.enterPressed) {
			this.enterCallback = r;
		} else {
			r.run();
		}
	}

	/**
	 * Handle the tab key.
	 * 
	 * @param shiftDown
	 *            whether shift is pressed
	 */
	public void onTab(boolean shiftDown) {
		if (listener != null) {
			listener.onTab(shiftDown);
		}
	}

	/**
	 * When division is the first character in current sequence (first in whole
	 * formula, first under square root...), jump before it. Needs to be called
	 * explicitly so that we can distinguish between keyboard input and other
	 * other inputs (paste)
	 */
	public void onDivisionInserted() {
		MathSequence currentField = editorState.getCurrentField();
		if (currentField.size() == 1 && currentField.isOperator(0)) {
			editorState.setCurrentOffset(0);
		}
	}

	/**
	 * Parse text to a formula and update content
	 *
	 * @param text
	 *            ASCII math input
	 */
	public void parse(String text) {
		Parser parser = new Parser(mathField.getMetaModel());
		try {
			MathFormula formula = parser.parse(text);
			setFormula(formula);
		} catch (ParseException e) {
			FactoryProvider.debugS("Problem parsing: " + text);
			e.printStackTrace();
		}
	}

	/**
	 * @return the contained formula serialized in the GeoGebra format
	 */
	public String getText() {
		GeoGebraSerializer s = new GeoGebraSerializer();
		return s.serialize(getFormula());
	}
}
