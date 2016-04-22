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

import com.himamis.retex.editor.share.controller.CursorController;
import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.controller.InputController;
import com.himamis.retex.editor.share.controller.KeyListenerImpl;
import com.himamis.retex.editor.share.controller.MathFieldController;
import com.himamis.retex.editor.share.event.ClickListener;
import com.himamis.retex.editor.share.event.FocusListener;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.event.KeyListener;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathSequence;

import java.util.ArrayList;

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
        boolean handled = keyListener.onKeyPressed(keyEvent);
        if (handled) {
            update();
        }
        return handled;
    }

    @Override
    public boolean onKeyReleased(KeyEvent keyEvent) {
        boolean handled = keyListener.onKeyReleased(keyEvent);
        if (handled) {
            update();
        }
        return handled;
    }

    @Override
    public boolean onKeyTyped(KeyEvent keyEvent) {
        boolean handled = keyListener.onKeyTyped(keyEvent);
        if (handled) {
            update();
        }
        return handled;
    }

    @Override
	public void onPointerDown(int x, int y) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		mathFieldController.getPath(mathFormula, x, y, list);
		editorState.resetSelection();
		cursorController.firstField(editorState);
		this.mouseDownPos = new int[] { x, y };

		moveToSelection(list);

		mathFieldController.update(mathFormula, editorState, false);

        mathField.showKeyboard();
        mathField.hideCopyPasteButtons();
        mathField.requestViewFocus();

    }

	public void onPointerUp(int x, int y) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		mathFieldController.getPath(mathFormula, x, y, list);
		MathComponent cursor = editorState.getCursorField(false);

		cursorController.firstField(editorState);

		moveToSelection(list);

		editorState.resetSelection();
		if (mousePositionChanged(x, y)) {
			editorState.extendSelection(false);
			editorState.extendSelection(cursor);
		}
		mouseDownPos = null;
		mathFieldController.update(mathFormula, editorState, false);

        mathField.requestViewFocus();

    }

    public void onLongPress(int action, int x, int y) {
        mathField.showCopyPasteButtons();
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
		if (!mousePositionChanged(x, y)) {
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

}
