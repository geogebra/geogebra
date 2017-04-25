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
import java.util.Collections;

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
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.renderer.share.CursorBox;
import com.himamis.retex.renderer.share.SelectionBox;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

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

	private boolean enterPressed;

	private Runnable enterCallback;

	private boolean directFormulaBuilder;

	public MathFieldInternal(MathField mathField) {
		this(mathField, false);
	}

	public MathFieldInternal(MathField mathField,
			boolean directFormulaBuilder) {
        this.mathField = mathField;
		this.directFormulaBuilder = directFormulaBuilder;
        cursorController = new CursorController();
		inputController = new InputController(mathField.getMetaModel());
        keyListener = new KeyListenerImpl(cursorController, inputController);
        mathFormula = MathFormula.newFormula(mathField.getMetaModel());
		mathFieldController = new MathFieldController(mathField,
				directFormulaBuilder);
		inputController.setMathField(mathField);
        setupMathField();
    }

    private void setupMathField() {
        mathField.setFocusListener(this);
        mathField.setClickListener(this);
        mathField.setKeyListener(this);
    }

    public void setSize(double size) {
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
        CursorController.setPath(path, getEditorState());
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
				if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
					listener.onUpKeyPressed();
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
					listener.onDownKeyPressed();
				}
			}
		}
		if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL) {
			return false;
		}
		if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (listener != null && listener.onEscape()) {
				return true;
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
			String str = listener.alt(keyEvent.getKeyCode(),
					(keyEvent.getKeyModifiers() & KeyEvent.SHIFT_MASK) > 0);
			for (int i = 0; str != null && i < str.length(); i++) {
				keyListener.onKeyTyped(str.charAt(i));
			}
			notifyAndUpdate();
		}
		return false;
    }

    @Override
    public boolean onKeyTyped(KeyEvent keyEvent) {
		boolean alt = (keyEvent.getKeyModifiers() & KeyEvent.ALT_MASK) > 0;
		boolean enter = keyEvent.getUnicodeKeyChar() == (char) 13
				|| keyEvent.getUnicodeKeyChar() == (char) 10;

		boolean handled = alt || enter
				|| ((keyEvent.getKeyModifiers()
				& KeyEvent.CTRL_MASK) > 0)
				|| keyListener.onKeyTyped(keyEvent.getUnicodeKeyChar());
        if (handled) {
			notifyAndUpdate();
        }
        return handled;
    }

	private void notifyAndUpdate() {
		if (listener != null) {
			listener.onKeyTyped();
		}
		update();

	}

	@Override
	public void onPointerDown(int x, int y) {
        if (selectionMode) {
            ArrayList<Integer> list = new ArrayList<Integer>();
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

            this.mouseDownPos = new int[]{x, y};

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
            ArrayList<Integer> list = new ArrayList<Integer>();
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
            if (!editorState.hasSelection()){
                mathField.hideCopyButton();
            }
            */

            mathFieldController.update(mathFormula, editorState, false);

			mathField.showKeyboard();
			mathField.requestViewFocus();
		}

		mouseDownPos = null;

    }

	private boolean selectionLeft(int x) {
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
	
	private void moveToSelectionDirect(int x, int y) {
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		EditorState mc = mathFieldController.getPath(mathFormula, x, y,
				list2);
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
		// System.out.println("SELECTION" + list);
		CursorController.firstField(editorState);
		double dist = Integer.MAX_VALUE;
		MathSequence closestComponent = null;
		int closestOffset = -1;
		do  {
			ArrayList<Integer> list2 = new ArrayList<Integer>();
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

			ArrayList<Integer> list2 = new ArrayList<Integer>();
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
		ArrayList<Integer> list = new ArrayList<Integer>();
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
				} else {
					break;
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
				if (InputController.doSelectNext(args, state, 0)) {
					update();
				}
			}
		}

	}

	public String copy() {
		if(listener!=null){
			getInputController();
			return (listener.serialize(
					InputController.getSelectionText(getEditorState())));
		}
		return "";

	}

	public void onInsertString() {
		ArrayList<Integer> path = new ArrayList<Integer>();
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
		for (int i : path) {
			FactoryProvider.getInstance().debug("" + i);
		}
		if (listener != null) {
			listener.onInsertString();
		}
		getMathFieldController().setSelectedPath(getFormula(), path,
				getEditorState());
		mathField.requestViewFocus();
		// do this as late as possible
		if (listener != null) {
			listener.onKeyTyped();
		}
	}

	public void insertFunction(String text) {
		inputController.newFunction(editorState, text, "frac".equals(text) ? 1
				: 0);
		if (listener != null) {
			listener.onKeyTyped();
		}
	}

	public void checkEnterReleased(Runnable r) {
		if (this.enterPressed) {
			this.enterCallback = r;
		} else {
			r.run();
		}

	}

}
