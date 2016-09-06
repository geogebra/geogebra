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
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.renderer.share.SelectionBox;

/**
 * This class is a Math Field. Displays and allows to edit single formula.
 *
 * @author Bea Petrovicova, Bencze Balazs
 */
public class MathFieldInternal implements KeyListener, FocusListener, ClickListener {

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

    public MathFieldInternal(MathField mathField) {
        this.mathField = mathField;
        cursorController = new CursorController();
        inputController = new InputController(mathField.getMetaModel(), cursorController);
        keyListener = new KeyListenerImpl(cursorController, inputController);
        mathFormula = MathFormula.newFormula(mathField.getMetaModel());
        mathFieldController = new MathFieldController(mathField);

        setupMathField();
    }

    private void setupMathField() {
        mathField.setFocusListener(this);
        mathField.setClickListener(this);
        mathField.setKeyListener(this);
    }

    public void setSize(float size) {
        mathFieldController.setSize(size);
    }

    public void setType(int type) {
        mathFieldController.setType(type);
    }

    public MathFormula getFormula() {
        return mathFormula;
    }

    public void setFormula(MathFormula formula) {
        mathFormula = formula;
        editorState = new EditorState(mathField.getMetaModel());
        editorState.setRootComponent(formula.getRootComponent());
        editorState.setCurrentField(formula.getRootComponent());
        editorState.setCurrentOffset(editorState.getCurrentField().size());
        keyListener.setEditorState(editorState);
        mathFieldController.update(formula, editorState, false);
    }

    public void setFormula(MathFormula formula, ArrayList<Integer> path) {
        mathFormula = formula;
        editorState = new EditorState(mathField.getMetaModel());
        editorState.setRootComponent(formula.getRootComponent());
        cursorController.setPath(path, getEditorState());
        keyListener.setEditorState(editorState);
        mathFieldController.update(formula, editorState, false);
    }

    public InputController getInputController() {
        return inputController;
    }

    public CursorController getCursorController() {
        return cursorController;
    }

    public MathFieldController getMathFieldController() {
        return mathFieldController;
    }

    public EditorState getEditorState() {
        return editorState;
    }

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
			}
		}
		boolean handled = keyListener.onKeyPressed(keyEvent);
        if (handled) {
            update();
			if (!arrow && listener != null) {
				listener.onKeyTyped();
			}
        }

        return handled;
    }

    @Override
    public boolean onKeyReleased(KeyEvent keyEvent) {
		return false;
    }

    @Override
    public boolean onKeyTyped(KeyEvent keyEvent) {
		boolean handled = ((keyEvent.getKeyModifiers() & 2) > 0)
				|| keyListener.onKeyTyped(keyEvent.getUnicodeKeyChar());
        if (handled) {
			if (listener != null) {
				listener.onKeyTyped();
			}
            update();
        }
        return handled;
    }

    @Override
	public void onPointerDown(int x, int y) {
        if (selectionMode) {
            ArrayList<Integer> list = new ArrayList<Integer>();

			if (length(SelectionBox.startX - x, SelectionBox.startY - y) < 10) {
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
			mathFieldController.getPath(mathFormula, x, y, list);
            editorState.resetSelection();
            cursorController.firstField(editorState);
            this.mouseDownPos = new int[]{x, y};

            moveToSelection(list);

            mathFieldController.update(mathFormula, editorState, false);
        }

        mathField.showKeyboard();
        mathField.requestViewFocus();

    }

	private double length(double d, double e) {
		return Math.sqrt(d * d + e * e);
	}

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
            ArrayList<Integer> list = new ArrayList<Integer>();
            mathFieldController.getPath(mathFormula, x, y, list);
            MathComponent cursor = editorState.getCursorField(false);

            cursorController.firstField(editorState);

            moveToSelection(list);

            editorState.resetSelection();

            if (selectionMode && mousePositionChanged(x, y)) {
                editorState.extendSelection(false);
                editorState.extendSelection(cursor);
            }

            // TODO only hide copy button only when no selection
            // (see commented below, MOB-567 and MOB-568)
            mathField.hideCopyPasteButtons();
            /*
            if (!editorState.hasSelection()){
                mathField.hideCopyButton();
            }
            */

            mathFieldController.update(mathFormula, editorState, false);

            mathField.requestViewFocus();
        }

		mouseDownPos = null;

    }

    public void onLongPress(int x, int y) {
        longPressOccured = true;
        if (!mathFormula.isEmpty()) {
            editorState.selectAll();
        }
        mathFieldController.update(mathFormula, editorState, false);
        mathField.showCopyPasteButtons();
    }

    public void onScroll(int dx, int dy) {
        if (!selectionMode) {
            mathField.scroll(dx, dy);
            scrollOccured = true;
        }
    }

    private boolean scrollOccured = false;

    private boolean longPressOccured = false;

    private boolean selectionMode = false;

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

	private void moveToSelection(ArrayList<Integer> list) {

		while (cursorController.nextCharacter(editorState)) {
			ArrayList<Integer> list2 = new ArrayList<Integer>();
			mathFieldController.getSelectedPath(mathFormula, list2,
					editorState.getCurrentField(),
					editorState.getCurrentOffset());
			for (int i = 0; i < list2.size() / 2; i++) {
				int tmp = list2.get(i);

				list2.set(i, list2.get(list2.size() - 1 - i));
				list2.set(list2.size() - 1 - i, tmp);
			}
			if (compare(list, list2)) {
				break;
			}
		}

	}

	public void onPointerMove(int x, int y) {
		if (!mousePositionChanged(x, y) && !selectionDrag) {
			editorState.resetSelection();
			mathFieldController.update(mathFormula, editorState, false);
			return;
		}
		ArrayList<Integer> list = new ArrayList<Integer>();
		mathFieldController.getPath(mathFormula, x, y, list);
		MathComponent cursor = editorState.getCursorField(false);
		MathSequence current = editorState.getCurrentField();
		int offset = editorState.getCurrentOffset();
		cursorController.firstField(editorState);
		moveToSelection(list);
		editorState.resetSelection();
		editorState.extendSelection(false);
		editorState.setCurrentField(current);
		editorState.setCurrentOffset(offset);
		editorState.extendSelection(cursor);


		mathFieldController.update(mathFormula, editorState, false);

	}

	private boolean compare(ArrayList<Integer> list, ArrayList<Integer> list2) {
		for (int i = 0; i < list.size() && i < list2.size(); i++) {
			if (list.get(i) > list2.get(i)) {
				return false;
			}
			if (list.get(i) < list2.get(i)) {
				return true;
			}
		}
		return list2.size() > list.size();
	}

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

	public String deleteCurrentWord() {
		StringBuilder str = new StringBuilder(" ");
		MathSequence sel = editorState.getCurrentField();
		if (sel != null) {
			for (int i = Math.min(editorState.getCurrentOffset() - 1,
					sel.size() - 1); i >= 0; i--) {
				if (sel.getArgument(i) instanceof MathCharacter) {
					if (!((MathCharacter) sel.getArgument(i)).isCharacter()) {
						return str.reverse().toString().trim() + ";"
								+ (sel.getArgument(i));
					}
					str.append(
							((MathCharacter) sel.getArgument(i)).getUnicode());
					sel.removeArgument(i);
					editorState.decCurrentOffset();

				}
			}
		}
		return str.reverse().toString().trim();
	}

	public String getCurrentWord() {
		// TODO Auto-generated method stub
		StringBuilder str = new StringBuilder(" ");
		MathSequence sel = editorState.getCurrentField();
		if (sel != null) {
			for (int i = Math.min(editorState.getCurrentOffset() - 1,
					sel.size() - 1); i >= 0; i--) {
				if (sel.getArgument(i) instanceof MathCharacter) {
					if (!((MathCharacter) sel.getArgument(i)).isCharacter()) {
						break;
					}
					str.append(
							((MathCharacter) sel.getArgument(i)).getUnicode());
				}
			}
		}
		return str.reverse().toString().trim();
	}

	public void selectNextArgument() {
		EditorState state = getEditorState();
		MathSequence seq = state.getCurrentField();
		if (seq != null && seq.size() > 0) {
			MathComponent last = seq.getArgument(state.getCurrentOffset() - 1);
			if (last instanceof MathArray && ((MathArray) last).size() > 0) {
				MathSequence args = ((MathArray) last).getArgument(0);
				int endchar = -1;
				for (int i = 1; i < args.size(); i++) {
					if (args.getArgument(i) instanceof MathCharacter
							&& ((MathCharacter) args.getArgument(i))
									.getUnicode() == '>') {
						endchar = i;
						break;
					}
				}
				if (endchar > 0) {
					state.setCurrentField(args);
					state.setSelectionStart(args.getArgument(0));
					state.setSelectionEnd(args.getArgument(endchar));
					state.setCurrentOffset(endchar);
					update();
				}
			}
		}

	}

}
