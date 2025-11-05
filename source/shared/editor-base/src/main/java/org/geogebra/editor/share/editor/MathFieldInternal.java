/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.controller.CursorController;
import org.geogebra.editor.share.controller.EditorState;
import org.geogebra.editor.share.controller.ExpressionReader;
import org.geogebra.editor.share.controller.InputController;
import org.geogebra.editor.share.controller.KeyListenerImpl;
import org.geogebra.editor.share.controller.MathFieldController;
import org.geogebra.editor.share.event.ClickListener;
import org.geogebra.editor.share.event.FocusListener;
import org.geogebra.editor.share.event.KeyEvent;
import org.geogebra.editor.share.event.KeyListener;
import org.geogebra.editor.share.event.MathFieldListener;
import org.geogebra.editor.share.input.KeyboardInputAdapter;
import org.geogebra.editor.share.io.latex.ParseException;
import org.geogebra.editor.share.io.latex.Parser;
import org.geogebra.editor.share.serializer.GeoGebraSerializer;
import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.CharPlaceholderNode;
import org.geogebra.editor.share.tree.CharacterNode;
import org.geogebra.editor.share.tree.Formula;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.InternalNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.PlaceholderNode;
import org.geogebra.editor.share.tree.SequenceNode;
import org.geogebra.editor.share.util.AltKeys;
import org.geogebra.editor.share.util.FormulaConverter;
import org.geogebra.editor.share.util.JavaKeyCodes;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.renderer.share.CursorBox;
import com.himamis.retex.renderer.share.SelectionBox;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * This class is a Math Field. Displays and allows to edit single formula.
 */
public class MathFieldInternal
		implements KeyListener, FocusListener, ClickListener {
	public static final int PADDING_LEFT_SCROLL = 20;
	@Weak
	private MathField mathField;

	private final InputController inputController;
	private final MathFieldController mathFieldController;

	private final KeyListenerImpl keyListener;
	private EditorState editorState;

	private Formula formula;

	private int[] mouseDownPos;

	private boolean selectionDrag;

	private final List<MathFieldListener> listeners = new ArrayList<>();
	private UnhandledArrowListener unhandledArrowListener;

	private boolean scrollOccurred = false;

	private boolean longPressOccurred = false;

	private boolean selectionMode = false;

	private final Set<MathFieldInternalListener> mathFieldInternalListeners;

	private static final ArrayList<Integer> LOCKED_CARET_PATH
			= new ArrayList<>(Arrays.asList(0, 0, 0));

	private final FormulaConverter formulaConverter;

	/**
	 * @param mathField
	 *            editor component
	 */
	public MathFieldInternal(MathField mathField) {
		this.mathField = mathField;
		inputController = new InputController(mathField.getCatalog());
		keyListener = new KeyListenerImpl(inputController);
		formula = new Formula(mathField.getCatalog());
		mathFieldController = new MathFieldController(mathField);
		inputController.setMathField(mathField);
		mathFieldInternalListeners = new HashSet<>();
		formulaConverter = new FormulaConverter();
		setupMathField();
	}

	/**
	 * @param scrollLeft current scroll
	 * @param parentWidth parent container width
	 * @param cursorX cursor coordinate within formula
	 * @return new horizontal scroll value
	 */
	public static int getHorizontalScroll(int scrollLeft, int parentWidth, int cursorX) {
		int padding = Math.min(PADDING_LEFT_SCROLL, parentWidth / 3);
		if (parentWidth + scrollLeft - padding < cursorX) {
			return Math.max(cursorX - parentWidth + padding, 0);
		} else if (cursorX < scrollLeft + padding) {
			return Math.max(cursorX - padding, 0);
		}
		return scrollLeft;
	}

	/**
	 * @param syntaxAdapter syntax converter / function name checker
	 */
	public void setSyntaxAdapter(SyntaxAdapter syntaxAdapter) {
		mathFieldController.setSyntaxAdapter(syntaxAdapter);
		inputController.setSyntaxAdapter(syntaxAdapter);
	}

	private EditorFeatures getEditorFeatures() {
		return inputController.getEditorFeatures();
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
	public Formula getFormula() {
		return formula;
	}

	/**
	 * @param formula
	 *            formula
	 */
	public void setFormula(Formula formula) {
		this.formula = formula;
		editorState = new EditorState(mathField.getCatalog());
		editorState.setRootNode(formula.getRootNode());
		editorState.setCurrentNode(formula.getRootNode());
		editorState.setCurrentOffset(editorState.getCurrentNode().size());
		mathFieldController.update(formula, editorState, false);
		fireInputChangedEvent();

	}

	/**
	 * Move caret into the first element of the protected component.
	 */
	public void setLockedCaretPath() {
		setCaretPath(LOCKED_CARET_PATH);
	}

	/**
	 * @param formula
	 *            formula
	 * @param path
	 *            indices of subtrees that contain the cursor
	 */
	public void setFormula(Formula formula, ArrayList<Integer> path) {
		this.formula = formula;
		editorState = new EditorState(mathField.getCatalog());
		editorState.setRootNode(formula.getRootNode());
		CursorController.setPath(path, getEditorState());
		mathFieldController.update(this.formula, editorState, false);
		fireInputChangedEvent();
	}

	/**
	 * @param path
	 *            caret path
	 */
	public void setCaretPath(ArrayList<Integer> path) {
		CursorController.setPath(path, getEditorState());
		mathFieldController.updateWithCursor(formula, editorState);
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

	/**
	 * Update the content.
	 */
	public void update() {
		update(false);
	}

	private void update(boolean focusEvent) {
		mathFieldController.update(formula, editorState, focusEvent);
		fireInputChangedEvent();
	}

	/**
	 * @return icon without placeholder
	 */
	public TeXIcon buildIconNoPlaceholder() {
		return mathFieldController.buildIcon(formula, editorState.getCurrentNode());
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
			unhandledArrowListener.onArrow(keyEvent.getKeyCode(),
					keyEvent.getSourceKeyboard());
			return true;
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
				str = mathField.getCatalog().getPhiUnicode();
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
		boolean enter = keyEvent.getUnicodeKeyChar() == (char) 13
				|| keyEvent.getUnicodeKeyChar() == (char) 10;
		boolean handled = enter
				|| keyListener.onKeyTyped(keyEvent.getUnicodeKeyChar(),
						editorState);
		if (handled) {
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
			if (SelectionBox.isTouchSelection()) {
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

			mathFieldController.update(formula, editorState, false);
		}

	}

	private static double length(double d, double e) {
		return Math.sqrt(d * d + e * e);
	}

	@Override
	public void onPointerUp(int x, int y) {
		if (scrollOccurred) {
			scrollOccurred = false;
		} else if (longPressOccurred) {
			longPressOccurred = false;
		} else {
			if (this.selectionDrag) {
				selectionDrag = false;
				return;
			}
			Node cursor = editorState.getCursorField(
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
		longPressOccurred = true;
		selectCurrentEntry();
		mathField.showCopyPasteButtons();
		mathField.showKeyboard();
		mathField.requestViewFocus();
	}

	private void selectCurrentEntry() {
		if (!formula.isEmpty()) {
			editorState.selectAll();
		}
		mathFieldController.update(formula, editorState, false);
	}

	@Override
	public void onScroll(int dx, int dy) {
		if (!selectionMode) {
			mathField.scroll(dx, dy);
			scrollOccurred = true;
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
		SequenceNode closestComponent = null;
		int closestOffset = -1;
		do {
			mathFieldController.updateCursorPosition(formula,
					editorState.getCurrentNode(),
					editorState.getCurrentOffset());
			double currentDist = Math.abs(x - CursorBox.startX)
					+ Math.abs(y - CursorBox.startY);
			if (currentDist < dist) {
				dist = currentDist;
				closestComponent = editorState.getCurrentNode();
				closestOffset = editorState.getCurrentOffset();
			}
		} while (CursorController.nextCharacter(editorState, false));
		if (closestComponent != null) {
			moveCaretToClosestValidPoint(closestComponent, closestOffset);
			mathFieldController.updateCursorPosition(formula,
					editorState.getCurrentNode(),
					editorState.getCurrentOffset());
		}
	}

	private void moveCaretToClosestValidPoint(SequenceNode closestComponent, int closestOffset) {
		editorState.setCurrentNode(closestComponent);
		editorState.setCurrentOffset(closestOffset);
		if (closestComponent.getChild(closestOffset - 1) instanceof CharPlaceholderNode) {
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
			mathFieldController.update(formula, editorState, false);
			return;
		}
		Node cursor = editorState.getCursorField(
				editorState.getSelectionEnd() == null || selectionLeft(x));
		SequenceNode current = editorState.getCurrentNode();
		int offset = editorState.getCurrentOffset();
		moveToSelection(x, y);

		editorState.resetSelection();
		editorState.extendSelection(selectionLeft(x));
		editorState.setCurrentNode(current);
		editorState.setCurrentOffset(offset);
		editorState.extendSelection(cursor);

		mathFieldController.update(formula, editorState, false);

	}

	/**
	 * @return whether current formula is empty
	 */
	public boolean isEmpty() {
		return formula.isEmpty();
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
	 * Delete all the consecutive  letters left from the cursor.
	 */
	public void deleteCurrentWord() {
		deleteCurrentCharSequence(CharacterNode::isCharacter);
	}

	/**
	 * Delete characters left from the cursor that fit certain criteria.
	 * @param predicate decides which characters to delete
	 */
	public void deleteCurrentCharSequence(Predicate<CharacterNode> predicate) {
		SequenceNode sel = editorState.getCurrentNode();
		if (sel != null) {
			for (int i = Math.min(editorState.getCurrentOffset() - 1,
					sel.size() - 1); i >= 0; i--) {
				if (sel.getChild(i) instanceof CharacterNode) {
					if (!predicate.test((CharacterNode) sel.getChild(i))) {
						return;
					}
					sel.removeChild(i);
					editorState.decCurrentOffset();
				}
			}
		}
	}

	/**
	 * @return sequence of letters left from the cursor (or selection end).
	 */
	public @Nonnull String getCharactersLeftOfCursor() {
		return getCharactersLeftOfCursorMatching(CharacterNode::isCharacter);
	}

	/**
	 * @param predicate decides which characters to include
	 * @return sequence of characters left of the cursor (or selection end) matching the predicate
	 */
	public @Nonnull String getCharactersLeftOfCursorMatching(Predicate<CharacterNode> predicate) {
		StringBuilder str = new StringBuilder(" ");
		SequenceNode currentField = editorState.getCurrentNode();
		if (currentField != null) {
			int index = editorState.getCurrentOffset() - 1;
			if (editorState.getSelectionEnd() != null) {
				index = editorState.getSelectionEnd().getParentIndex();
			}
			for (int i = Math.min(index, currentField.size() - 1); i >= 0; i--) {
				if (!appendChar(str, currentField, i, predicate)) {
					break;
				}
			}
		}
		return str.reverse().toString().trim();
	}

	/**
	 * @param predicate Which characters to include.
	 * @return The characters in the current field around the cursor position that match the
	 * predicate, up until (but not including) the first characters (looking left / right) for
	 * which predicate returns false. Returns null if currentField is null.
	 */
	public @CheckForNull String getCharactersAroundCursorMatching(
			Predicate<CharacterNode> predicate) {
		SequenceNode currentField = editorState.getCurrentNode();
		if (currentField == null) {
			return null;
		}
		StringBuilder prefix = new StringBuilder();
		if (editorState.getCurrentOffset() > 0) {
			for (int i = editorState.getCurrentOffset() - 1; i >= 0; i--) {
				if (!appendChar(prefix, currentField, i, predicate)) {
					break;
				}
			}
		}
		StringBuilder suffix = new StringBuilder();
		for (int i = editorState.getCurrentOffset(); i < currentField.size(); i++) {
			if (!appendChar(suffix, currentField, i, predicate)) {
				break;
			}
		}
		return prefix.reverse().toString().trim() + suffix.toString().trim();
	}

	/**
	 * Recursively collect all sequences of consecutive {@code CharacterNode}s matching the given
	 * predicate.
	 * @param predicate A predicate for which {@code CharacterNode}s to include.
	 * @param characterSequences The result (out param).
	 */
	public void collectCharacterSequences(Predicate<CharacterNode> predicate,
			ArrayList<String> characterSequences) {
		collectCharacterSequences(editorState.getRootNode(), predicate, characterSequences);
	}

	private void collectCharacterSequences(InternalNode root,
			Predicate<CharacterNode> predicate,
			ArrayList<String> characterSequences) {
		ArrayList<CharacterNode> characterSequence = new ArrayList<>();
		for (int i = 0; i < root.size(); i++) {
			Node argument = root.getChild(i);
			if (argument instanceof CharacterNode && predicate.test((CharacterNode) argument)) {
				characterSequence.add((CharacterNode) argument);
			} else {
				if (!characterSequence.isEmpty()) {
					characterSequences.add(toString(characterSequence));
				}
				characterSequence = new ArrayList<>();
				if (argument instanceof InternalNode) {
					collectCharacterSequences((InternalNode) argument, predicate,
							characterSequences);
				}
			}
		}
		if (!characterSequence.isEmpty()) {
			characterSequences.add(toString(characterSequence));
		}
	}

	private @Nonnull String toString(List<CharacterNode> characters) {
		StringBuilder sb = new StringBuilder();
		for (CharacterNode character : characters) {
			sb.append(character.toString());
		}
		return sb.toString();
	}

	/**
	 * Append characters matching a predicate to a string builder.
	 * @param sb
	 *            string builder
	 * @param sequence
	 *            formula part
	 * @param argumentIndex
	 *            index
	 * @return true if {@code sequence.arguments[argumentIndex]} is a CharacterNode satisfying predicate
	 */
	public static boolean appendChar(StringBuilder sb, SequenceNode sequence,
			int argumentIndex, Predicate<CharacterNode> predicate) {
		if (sequence.getChild(argumentIndex) instanceof CharacterNode) {
			CharacterNode character = (CharacterNode) sequence.getChild(argumentIndex);
			if (predicate.test(character)) {
				sb.append(character.getUnicodeString());
				return true;
			}
		}
		return false;
	}

	/**
	 * @return serialized selection
	 */
	public String copy() {
		if (editorState.getSelectionStart() != null) {
			GeoGebraSerializer serializer = new GeoGebraSerializer(getEditorFeatures());
			serializer.setUseTemplates(false);
			InternalNode parent = editorState.getSelectionStart().getParent();
			if (parent == null) {
				// all the formula is selected
				return serializer.serialize(editorState.getRootNode(), new StringBuilder())
						.toString();
			}

			int start = parent.indexOf(editorState.getSelectionStart());
			int end = parent.indexOf(editorState.getSelectionEnd());

			if (end >= 0 && start >= 0) {
				StringBuilder sb = new StringBuilder();
				for (int i = start; i <= end; i++) {
					serializer.serialize(parent.getChild(i), sb);
				}
				return sb.toString();
			}
		}

		return "";
	}

	/**
	 * Convert to editor format and insert.
	 * @param text string in any supported syntax (LaTeX, MathML, ascii math)
	 */
	public void convertAndInsert(String text) {
		insertString(inputController.convert(text));
	}

	/**
	 * Interpret text as ascii math and insert it into the editor
	 * @param text ascii math string
	 */
	public void insertString(String text) {
		SequenceNode rootBefore = editorState.getRootNode();
		boolean allSelected = editorState.getSelectionStart() == rootBefore;
		boolean rootProtected = rootBefore.isProtected();
		boolean rootCommas = rootBefore.isKeepCommas();
		InputController.deleteSelection(editorState);

		if (editorState.isInsideQuotes() || inputController.getPlainTextMode()) {
			KeyboardInputAdapter.type(this, text);
		} else {
			try {
				SequenceNode root = new Parser(mathField.getCatalog()).parse(text)
						.getRootNode();

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
			editorState.getRootNode().setProtected();
		}
		if (rootCommas) {
			editorState.getRootNode().setKeepCommas();
		}
	}

	private void addToMathField(SequenceNode root, boolean filterCommas) {
		for (int i = 0; i < root.getArgumentCount(); i++) {
			Node argument = root.getChild(i);
			if (!filterCommas || (!",".equals(argument.toString())
					&& !argument.hasTag(Tag.CURLY))) {
				getEditorState().addArgument(argument);
			}
		}
	}

	private void replaceRoot(SequenceNode rootBefore, SequenceNode root) {
		rootBefore.clearChildren();
		for (int i = 0; i < root.getArgumentCount(); i++) {
			rootBefore.addChild(root.getChild(i));
		}
		editorState.setCurrentNode(((ArrayNode) root.getChild(0)).getChild(0, 0));
		editorState.setCurrentOffset(0);
	}

	private boolean isMatrixWithSameDimension(SequenceNode rootBefore, SequenceNode root) {
		ArrayNode matrix = asMatrix(root);
		return matrix != null && matrix.hasSameDimension(asMatrix(rootBefore));
	}

	private ArrayNode asMatrix(SequenceNode sequence) {
		Node argument1 = sequence.getChild(0);
		return argument1 instanceof ArrayNode && ((ArrayNode) argument1).isMatrix()
				? (ArrayNode) argument1
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
				- getEditorState().getCurrentNode().size());
		InternalNode field = getEditorState().getCurrentNode();
		while (field != null) {
			if (field.getParent() != null) {
				path.add(field.getParentIndex() - field.getParent().size());
			}
			field = field.getParent();
		}
		reverse(path);
		setFormula(GeoGebraSerializer.reparse(getFormula(), getEditorFeatures()));
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
		SequenceNode currentField = editorState.getCurrentNode();
		int jumpTo = editorState.getCurrentOffset();
		int dir = shiftDown ? -1 : 1;
		do {
			jumpTo += dir;
			if (currentField.getChild(jumpTo) instanceof PlaceholderNode) {
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
	 * inputs (paste)
	 */
	public void onDivisionInserted() {
		SequenceNode currentField = editorState.getCurrentNode();
		if (currentField.size() == 1
				&& currentField.getChild(0) instanceof CharacterNode
				&& ((CharacterNode) currentField.getChild(0)).isOperator()) {
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
			Formula formula = formulaConverter.buildFormula(text);
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
		GeoGebraSerializer s = new GeoGebraSerializer(getEditorFeatures());
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
		InternalNode container = editorState.getCurrentNode().getParent();
		if (container instanceof FunctionNode) {
			FunctionNode function = (FunctionNode) container;

			if (function.getName() != Tag.APPLY) {
				return function.getName().getKey();
			}

			StringBuilder str = new StringBuilder();
			SequenceNode name = function.getChild(0);
			for (int i = 0; i < name.getArgumentCount(); i++) {
				appendChar(str, name, i, CharacterNode::isCharacter);
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
		InternalNode container = editorState.getCurrentNode().getParent();
		if (container instanceof FunctionNode) {
			int commaCount = 0;
			for (int i = editorState.getCurrentOffset(); i >= 0; i--) {
				Node arg = editorState.getCurrentNode().getChild(i);
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
			@Nonnull MathFieldInternalListener mathFieldInternalListener) {
		mathFieldInternalListeners.add(mathFieldInternalListener);
	}

	/**
	 * Unregister a listener.
	 * @param mathFieldInternalListener a previously registered listener
	 */
	public void unregisterMathFieldInternalListener(
			@Nonnull MathFieldInternalListener mathFieldInternalListener) {
		mathFieldInternalListeners.remove(mathFieldInternalListener);
	}

	private void fireInputChangedEvent() {
		for (MathFieldInternalListener listener: mathFieldInternalListeners) {
			listener.inputChanged(this);
		}
	}

	/**
	 * Abs value not possible in fields that support austrian style point coordinates.
	 * @param b whether to allow abs value
	 */
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

	/**
	 * @param expressionReader expression reader
	 * @return editor state description serialized for screen reader
	 */
	public String getEditorStateDescription(ExpressionReader expressionReader) {
		return getEditorState().getDescription(expressionReader, getEditorFeatures());
	}

	public GeoGebraSerializer getSerializer() {
		return new GeoGebraSerializer(getEditorFeatures());
	}
}
