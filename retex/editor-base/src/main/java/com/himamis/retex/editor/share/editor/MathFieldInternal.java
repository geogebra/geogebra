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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

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
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;
import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharPlaceholder;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathPlaceholder;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.share.util.AltKeys;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.editor.share.util.MathFormulaConverter;
import com.himamis.retex.renderer.share.CursorBox;
import com.himamis.retex.renderer.share.SelectionBox;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * This class is a Math Field. Displays and allows to edit single formula.
 *
 * @author Bea Petrovicova, Bencze Balazs
 */
public class MathFieldInternal
		implements KeyListener, FocusListener, ClickListener {
	public static final int PADDING_LEFT_SCROLL = 20;
	@Weak
	private MathField mathField;

	private InputController inputController;
	private MathFieldController mathFieldController;

	private KeyListenerImpl keyListener;
	private EditorState editorState;

	private MathFormula mathFormula;

	private int[] mouseDownPos;

	private boolean selectionDrag;

	private final List<MathFieldListener> listeners = new ArrayList<>();
	private UnhandledArrowListener unhandledArrowListener;

	private boolean scrollOccured = false;

	private boolean longPressOccured = false;

	private boolean selectionMode = false;

	private Set<MathFieldInternalListener> mathFieldInternalListeners;

	private static final ArrayList<Integer> LOCKED_CARET_PATH
			= new ArrayList<>(Arrays.asList(0, 0, 0));

	private MathFormulaConverter formulaConverter;

	/**
	 * @param mathField
	 *            editor component
	 */
	public MathFieldInternal(MathField mathField) {
		this.mathField = mathField;
		inputController = new InputController(mathField.getMetaModel());
		keyListener = new KeyListenerImpl(inputController);
		mathFormula = MathFormula.newFormula(mathField.getMetaModel());
		mathFieldController = new MathFieldController(mathField);
		inputController.setMathField(mathField);
		mathFieldInternalListeners = new HashSet<>();
		formulaConverter = new MathFormulaConverter();
		setupMathField();
	}

	/**
	 * @param scrollLeft current scroll
	 * @param parentWidth parent container width
	 * @param cursorX cursor coordinate within formula
	 * @return new horizontal scroll value
	 */
	public static int getHorizontalScroll(int scrollLeft, int parentWidth, int cursorX) {
		if (parentWidth + scrollLeft - PADDING_LEFT_SCROLL < cursorX) {
			return cursorX - parentWidth + PADDING_LEFT_SCROLL;
		} else if (cursorX < scrollLeft + PADDING_LEFT_SCROLL) {
			return Math.max(cursorX - PADDING_LEFT_SCROLL, 0);
		}
		return scrollLeft;
	}

	/**
	 * @param syntaxAdapter syntax converter / function name checker
	 */
	public void setSyntaxAdapter(SyntaxAdapter syntaxAdapter) {
		mathFieldController.setSyntaxAdapter(syntaxAdapter);
		inputController.setFormatConverter(syntaxAdapter);
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
		fireInputChangedEvent();

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
		fireInputChangedEvent();
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
		fireInputChangedEvent();
	}

	/**
	 * @return icon without placeholder
	 */
	public TeXIcon buildIconNoPlaceholder() {
		return mathFieldController.buildIcon(mathFormula, editorState.getCurrentField());
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
			if (!listeners.isEmpty()) {
				notifyListeners(l -> {
					l.onEnter();
					return true;
				});
				return true;
			}
		}
		boolean arrow = false;
		if (keyEvent.getKeyCode() >= 37 && keyEvent.getKeyCode() <= 40) {
			// move cursor
			arrow = true;
			if (notifyListeners(l -> l.onArrowKeyPressed(keyEvent.getKeyCode()))) {
				return true;
			}
		}
		if (keyEvent.getKeyCode() == JavaKeyCodes.VK_CONTROL) {
			return false;
		}
		if (keyEvent.getKeyCode() == JavaKeyCodes.VK_ESCAPE) {
			if (notifyListeners(MathFieldListener::onEscape)) {
				return true;
			}
		}
		boolean tab = keyEvent.getKeyCode() == JavaKeyCodes.VK_TAB;
		boolean handled = keyListener.onKeyPressed(keyEvent, editorState);
		if (handled && !tab) {
			update();
			if (!arrow) {
				for (MathFieldListener listener: listeners) {
					listener.onKeyTyped(null);
				}
			}
		}
		if (arrow && !handled && unhandledArrowListener != null) {
			unhandledArrowListener.onArrow(keyEvent.getKeyCode());
		}

		return handled;
	}

	@Override
	public boolean onKeyReleased(KeyEvent keyEvent) {
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
			// handle alt+f for correct phi unicode
			if (keyCode == 70 && (keyEvent.getKeyModifiers() & KeyEvent.SHIFT_MASK) <= 0) {
				str = mathField.getMetaModel().getPhiUnicode();
			}

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
		this.inputController.setPlainTextMode(plainText);
	}

	/**
	 * Notifies listener about key event and updates the view.
	 * @param key key name
	 */
	public void notifyAndUpdate(String key) {
		for (MathFieldListener listener: listeners) {
			listener.onKeyTyped(key);
		}
		update();
	}

	@Override
	public void onPointerDown(int x, int y) {
		if (selectionMode) {
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
			update(false);

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
		selectCurrentEntry();
		mathField.showCopyPasteButtons();
		mathField.showKeyboard();
		mathField.requestViewFocus();
	}

	private void selectCurrentEntry() {
		if (!mathFormula.isEmpty()) {
			editorState.selectAll();
		}
		mathFieldController.update(mathFormula, editorState, false);
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

	private void moveToSelection(int x, int y) {
		this.moveToSelectionIterative(x, y);
	}

	private void moveToSelectionIterative(int x, int y) {
		CursorController.firstField(editorState);
		double dist = Integer.MAX_VALUE;
		MathSequence closestComponent = null;
		int closestOffset = -1;
		do {
			mathFieldController.updateCursorPosition(mathFormula,
					editorState.getCurrentField(),
					editorState.getCurrentOffset());
			double currentDist = Math.abs(x - CursorBox.startX)
					+ Math.abs(y - CursorBox.startY);
			if (currentDist < dist) {
				dist = currentDist;
				closestComponent = editorState.getCurrentField();
				closestOffset = editorState.getCurrentOffset();
			}
		} while (CursorController.nextCharacter(editorState, false));
		if (closestComponent != null) {
			moveCaretToClosestValidPoint(closestComponent, closestOffset);
			mathFieldController.updateCursorPosition(mathFormula,
					editorState.getCurrentField(),
					editorState.getCurrentOffset());
		}
	}

	private void moveCaretToClosestValidPoint(MathSequence closestComponent, int closestOffset) {
		editorState.setCurrentField(closestComponent);
		editorState.setCurrentOffset(closestOffset);
		if (closestComponent.getArgument(closestOffset - 1) instanceof MathCharPlaceholder) {
			editorState.setCurrentOffset(closestOffset - 1);
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
	 * If listener is null, this removes all listeners.
	 * Otherwise, it replaces all listeners with the new one.
	 * @param listener
	 *            listener
	 * @deprecated use addMathFieldListener / removeMathFieldListener instead
	 */
	@Deprecated
	public void setFieldListener(MathFieldListener listener) {
		listeners.clear();
		if (listener != null) {
			listeners.add(listener);
		}
	}

	/**
	 * @param listener
	 *            listener
	 */
	public void addMathFieldListener(MathFieldListener listener) {
		listeners.add(listener);
	}

	/**
	 * @param listener
	 *            listener
	 */
	public void removeMathFieldListener(MathFieldListener listener) {
		listeners.remove(listener);
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
			str.append(((MathCharacter) sel.getArgument(i)).getUnicodeString());
			return true;
		}
		return false;
	}

	/**
	 * @return serialized selection
	 */
	public String copy() {
		if (editorState.getSelectionStart() != null) {
			MathContainer parent = editorState.getSelectionStart().getParent();
			if (parent == null) {
				// all the formula is selected
				return GeoGebraSerializer.serialize(editorState.getRootComponent());
			}

			int start = parent.indexOf(editorState.getSelectionStart());
			int end = parent.indexOf(editorState.getSelectionEnd());

			if (end >= 0 && start >= 0) {
				StringBuilder sb = new StringBuilder();
				for (int i = start; i <= end; i++) {
					sb.append(GeoGebraSerializer.serialize(parent.getArgument(i)));
				}
				return sb.toString();
			}
		}

		return "";
	}

	public void convertAndInsert(String text) {
		insertString(inputController.convert(text));
	}

	/**
	 * Interpret text as ascii math and insert it into the editor
	 * @param text ascii math string
	 */
	public void insertString(String text) {
		MathSequence rootBefore = editorState.getRootComponent();
		boolean allSelected = editorState.getSelectionStart() == rootBefore;
		boolean rootProtected = rootBefore.isProtected();
		boolean rootCommas = rootBefore.isKeepCommas();
		InputController.deleteSelection(editorState);

		if (editorState.isInsideQuotes() || inputController.getPlainTextMode()) {
			KeyboardInputAdapter.type(this, text);
		} else {
			try {
				MathSequence root = new Parser(mathField.getMetaModel()).parse(text)
						.getRootComponent();

				if (allSelected && isMatrixWithSameDimension(rootBefore, root)) {
					replaceRoot(rootBefore, root);
				} else {
					addToMathField(root, rootProtected);
				}
			} catch (ParseException parseException) {
				KeyboardInputAdapter.type(this, text);
			}

		}
		onInsertString();

		if (rootProtected) {
			editorState.getRootComponent().setProtected();
		}
		if (rootCommas) {
			editorState.getRootComponent().setKeepCommas();
		}
	}

	private void addToMathField(MathSequence root, boolean filterCommas) {
		for (int i = 0; i < root.getArgumentCount(); i++) {
			MathComponent argument = root.getArgument(i);
			if (!filterCommas || (!",".equals(argument.toString())
					&& !argument.hasTag(Tag.CURLY))) {
				getEditorState().addArgument(argument);
			}
		}
	}

	private void replaceRoot(MathSequence rootBefore, MathSequence root) {
		rootBefore.clearArguments();
		for (int i = 0; i < root.getArgumentCount(); i++) {
			rootBefore.addArgument(root.getArgument(i));
		}
		editorState.setCurrentField(((MathArray) root.getArgument(0)).getArgument(0, 0));
		editorState.setCurrentOffset(0);
	}

	private boolean isMatrixWithSameDimension(MathSequence rootBefore, MathSequence root) {
		MathArray matrix = asMatrix(root);
		return matrix != null && matrix.hasSameDimension(asMatrix(rootBefore));
	}

	private MathArray asMatrix(MathSequence sequence) {
		MathComponent argument1 = sequence.getArgument(0);
		return argument1 instanceof MathArray && ((MathArray) argument1).isMatrix()
				? (MathArray) argument1
				: null;
	}

	/**
	 * Insert string callback.
	 */
	private void onInsertString() {
		if (this.getInputController().getPlainTextMode()) {
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
		for (MathFieldListener listener: listeners) {
			listener.onInsertString();
		}
		getMathFieldController().setSelectedPath(getFormula(), path,
				getEditorState());
		insertStringFinished();
	}

	private void insertStringFinished() {
		if (mathField instanceof MathFieldAsync) {
			((MathFieldAsync) mathField).requestViewFocus(this::onKeyTyped);
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
		for (MathFieldListener listener: listeners) {
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
	 * Handle the tab key.
	 * 
	 * @param shiftDown
	 *            whether shift is pressed
	 *
	 * @return tab handling
	 */
	public boolean onTab(boolean shiftDown) {
		MathSequence currentField = editorState.getCurrentField();
		int jumpTo = editorState.getCurrentOffset();
		int dir = shiftDown ? -1 : 1;
		do {
			jumpTo += dir;
			if (currentField.getArgument(jumpTo) instanceof MathPlaceholder) {
				editorState.setCurrentOffset(jumpTo);
				update();
				return true;
			}
		} while (jumpTo < currentField.size() && jumpTo >= 0);
		return notifyListeners(l -> l.onTab(shiftDown));
	}

	private boolean notifyListeners(Predicate<MathFieldListener> eventDispatcher) {
		boolean handled = false;
		List<MathFieldListener> listenersCopy = new ArrayList<>(listeners);
		for (MathFieldListener listener: listenersCopy) {
			handled = eventDispatcher.test(listener) || handled;
		}
		return handled;
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
		try {
			MathFormula formula = formulaConverter.buildFormula(text);
			setFormula(formula);
		} catch (ParseException e) {
			FactoryProvider.debugS("Problem parsing: " + text);
			FactoryProvider.getInstance().debug(e);
		}
	}

	/**
	 * Set to plain mode and just fill with text (linear)
	 * @param text text
	 */
	public void setPlainText(String text) {
		parse("");
		setPlainTextMode(true);
		KeyboardInputAdapter.typeSilent(this, text);
		update();
	}

	/**
	 * @return the contained formula serialized in the GeoGebra format
	 */
	public String getText() {
		GeoGebraSerializer s = new GeoGebraSerializer();
		return s.serialize(getFormula());
	}

	public void setUnhandledArrowListener(UnhandledArrowListener arrowListener) {
		this.unhandledArrowListener = arrowListener;
	}

	/**
	 * Gets the name of the function around containing currently edited position
	 * @return function name, null if not in a function
	 */
	public String getCurrentFunction() {
		MathContainer container = editorState.getCurrentField().getParent();
		if (container instanceof MathFunction) {
			MathFunction function = (MathFunction) container;

			if (function.getName() != Tag.APPLY) {
				return function.getName().getFunction();
			}

			StringBuilder str = new StringBuilder();
			MathSequence name = function.getArgument(0);
			for (int i = 0; i < name.getArgumentCount(); i++) {
				appendChar(str, name, i);
			}

			return str.toString();
		}

		return null;
	}

	/**
	 * Computes the index of the currently edited function argument
	 * @return index of currently edited argument, -1 if not in a function
	 */
	public int getFunctionArgumentIndex() {
		MathContainer container = editorState.getCurrentField().getParent();
		if (container instanceof MathFunction) {
			int commaCount = 0;
			for (int i = editorState.getCurrentOffset(); i >= 0; i--) {
				MathComponent arg = editorState.getCurrentField().getArgument(i);
				if (arg != null && arg.isFieldSeparator()) {
					commaCount++;
				}
			}
			return commaCount;
		}

		return -1;
	}

	/**
	 * Register math field internal listener.
	 * @param mathFieldInternalListener listener
	 */
	public void registerMathFieldInternalListener(
			MathFieldInternalListener mathFieldInternalListener) {
		mathFieldInternalListeners.add(mathFieldInternalListener);
	}

	private void fireInputChangedEvent() {
		for (MathFieldInternalListener listener: mathFieldInternalListeners) {
			listener.inputChanged(this);
		}
	}

	public void setAllowAbs(boolean b) {
		inputController.setAllowAbs(b);
	}

	/**
	 * If content is a list/matrix/vector, select list/matrix/vector entry at given position,
	 * otherwise select everything.
	 * @param x relative x
	 * @param y relative y
	 */
	public void selectEntryAt(int x, int y) {
		onPointerUp(x, y);
		selectCurrentEntry();
	}
}
