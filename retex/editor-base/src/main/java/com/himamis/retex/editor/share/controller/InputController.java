package com.himamis.retex.editor.share.controller;

import java.util.ArrayList;

import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.meta.MetaArray;
import com.himamis.retex.editor.share.meta.MetaCharacter;
import com.himamis.retex.editor.share.meta.MetaFunction;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

public class InputController {

    public static final char FUNCTION_OPEN_KEY = '('; // probably universal
    public static final char FUNCTION_CLOSE_KEY = ')';
    public static final char DELIMITER_KEY = ';';

    private MetaModel metaModel;
    private CursorController cursorController;
    private ArgumentHelper argumentHelper;

    public InputController(MetaModel metaModel, CursorController cursorController) {
        this.metaModel = metaModel;
        this.cursorController = cursorController;
        this.argumentHelper = new ArgumentHelper();
    }

    final static private char getLetter(MathComponent component) throws Exception {
        if (!(component instanceof MathCharacter)) {
            throw new Exception("Math component is not a character");
        }

        MathCharacter mathCharacter = (MathCharacter) component;
        if (!mathCharacter.isCharacter()) {
            throw new Exception("Math component is not a character");
        }

        char c = mathCharacter.getUnicode();

        if (!Character.isLetter(c)) {
            throw new Exception("Math component is not a character");
        }

        return c;
    }

    /**
     * Insert array.
     */
    public void newArray(EditorState editorState, int size, char arrayOpenKey) {
        MathSequence currentField = editorState.getCurrentField();
        int currentOffset = editorState.getCurrentOffset();
        MetaArray meta = metaModel.getArray(arrayOpenKey);
        MathArray array = new MathArray(meta, size);
		ArrayList<MathComponent> removed = cut(currentField, currentOffset);
        currentField.addArgument(currentOffset, array);

        // add sequence
        MathSequence field = new MathSequence();
        array.setArgument(0, field);
		insertReverse(field, -1, removed);
		System.out.println(field);
        for (int i = 1; i < size; i++) {
            // add sequence
			array.setArgument(i, new MathSequence());
        }

        // set current
        editorState.setCurrentField(field);
		editorState.setCurrentOffset(field.size());
    }

    /**
     * Insert matrix.
     */
    public void newMatrix(EditorState editorState, int columns, int rows) {
        MathSequence currentField = editorState.getCurrentField();
        int currentOffset = editorState.getCurrentOffset();
        MetaArray meta = metaModel.getMatrix();
        MathArray matrix = new MathArray(meta, columns, rows);
        currentField.addArgument(currentOffset, matrix);

        // add sequence
        MathSequence field = new MathSequence();
        matrix.setArgument(0, field);

        for (int i = 1; i < matrix.size(); i++) {
            // add sequence
            matrix.setArgument(i, new MathSequence());
        }

        // set current
        editorState.setCurrentField(field);
        editorState.setCurrentOffset(0);
    }

    /**
     * Insert braces (), [], {}, "".
     */
    public void newBraces(EditorState editorState, char ch) {
        String casName = argumentHelper.readCharacters(editorState);
        if (ch == FUNCTION_OPEN_KEY && metaModel.isGeneral(casName)) {
            delCharacters(editorState, casName.length());
            newFunction(editorState, casName);

        } else if (ch == FUNCTION_OPEN_KEY && metaModel.isFunction(casName)) {
            delCharacters(editorState, casName.length());
            newFunction(editorState, casName);

        } else {
            // TODO brace type
            newArray(editorState, 1, ch);
        }
    }

    /**
     * Insert function by name.
     *
     * @param name function
     */
    public void newFunction(EditorState editorState, String name) {
        newFunction(editorState, name, 0);
    }

    /**
     * Insert function by name.
     *
     * @param name function
     */
    public void newFunction(EditorState editorState, String name, int initial) {
        MathSequence currentField = editorState.getCurrentField();
        int currentOffset = editorState.getCurrentOffset();
        // add extra braces for sqrt, nthroot and fraction
        if ("^".equals(name) && currentOffset > 0) {
            if (currentField.getArgument(currentOffset - 1) instanceof MathFunction) {
                MathFunction function = (MathFunction) currentField.getArgument(currentOffset - 1);
                if ("sqrt".equals(function.getName()) ||
                        "nroot".equals(function.getName()) ||
                        "frac".equals(function.getName())) {

                    currentField.delArgument(currentOffset - 1);
                    // add braces
                    MathArray array = new MathArray(metaModel.getArray(MetaArray.REGULAR), 1);
                    currentField.addArgument(currentOffset - 1, array);
                    // add sequence
                    MathSequence field = new MathSequence();
                    array.setArgument(0, field);
                    field.addArgument(0, function);
                }
            }
        }

        // add function
        MathFunction function;
        if (metaModel.isGeneral(name)) {
            MetaFunction meta = metaModel.getGeneral(name);
            function = new MathFunction(meta);

        } else {
            MetaFunction meta = metaModel.getFunction(name);
            function = new MathFunction(meta);
        }

        // add sequences
        for (int i = 0; i < function.size(); i++) {
            MathSequence field = new MathSequence();
            function.setArgument(i, field);
        }

        // pass characters for fraction and factorial only
        if ("frac".equals(name) /*|| "factorial".equals(name)*/) {
            argumentHelper.passArgument(editorState, function);
        }
        currentOffset = editorState.getCurrentOffset();
        currentField.addArgument(currentOffset, function);

        if (function.hasChildren()) {
            // set current sequence
            cursorController.firstField(editorState, function.getArgument(initial));
            editorState.setCurrentOffset(editorState.getCurrentField().size());
        } else {
            editorState.incCurrentOffset();
        }
    }

    public void newScript(EditorState editorState, String script) {
        MathSequence currentField = editorState.getCurrentField();
        int currentOffset = editorState.getCurrentOffset();

        int offset = currentOffset;
        while (offset > 0 && currentField.getArgument(offset - 1) instanceof MathFunction) {

            MathFunction function = (MathFunction) currentField.getArgument(offset - 1);
            if (script.equals(function.getName())) {
                return;
            }
            if (!"^".equals(function.getName()) && !"_".equals(function.getName())) {
                break;
            }
            offset--;
        }
        offset = currentOffset;
        while (offset < currentField.size() && currentField.getArgument(offset) instanceof MathFunction) {

            MathFunction function = (MathFunction) currentField.getArgument(offset);
            if (script.equals(function.getName())) {
                return;
            }
            if (!"^".equals(function.getName()) && !"_".equals(function.getName())) {
                break;
            }
            offset++;
        }
        if (currentOffset > 0 && currentField.getArgument(currentOffset - 1) instanceof MathFunction) {
            MathFunction function = (MathFunction) currentField.getArgument(currentOffset - 1);
            if ("^".equals(function.getName()) && "_".equals(script)) {
                currentOffset--;
            }
        }
        if (currentOffset < currentField.size() && currentField.getArgument(currentOffset) instanceof MathFunction) {
            MathFunction function = (MathFunction) currentField.getArgument(currentOffset);
            if ("_".equals(function.getName()) && "^".equals(script)) {
                currentOffset++;
            }
        }
        editorState.setCurrentOffset(currentOffset);
        newFunction(editorState, script);
    }

    /**
     * Insert operator.
     */
    public void newOperator(EditorState editorState, char op) {
        MetaCharacter meta = metaModel.getOperator("" + op);
        newCharacter(editorState, meta);
    }

    /**
     * Insert symbol.
     */
    public void newSymbol(EditorState editorState, char sy) {
        MetaCharacter meta = metaModel.getSymbol("" + sy);
        newCharacter(editorState, meta);
    }

    /**
     * Insert character.
     */
    public void newCharacter(EditorState editorState, char ch) {
        MetaCharacter meta = metaModel.getCharacter("" + ch);
        newCharacter(editorState, meta);
    }

    /**
     * Insert character.
     */
    public void newCharacter(EditorState editorState, MetaCharacter meta) {
        editorState.addArgument(new MathCharacter(meta));
    }

    /**
     * Insert field.
     */
    public void endField(EditorState editorState, char ch) {
        MathSequence currentField = editorState.getCurrentField();
        int currentOffset = editorState.getCurrentOffset();
        // first array specific ...
        if (currentField.getParent() instanceof MathArray) {
            MathArray parent = (MathArray) currentField.getParent();

            // if ',' typed within 1DArray or Vector ... add new field
            if (ch == parent.getFieldKey() && (parent.is1DArray() || parent.isVector())) {

                int index = currentField.getParentIndex();
                MathSequence field = new MathSequence();
                parent.addArgument(index + 1, field);
                while (currentField.size() > currentOffset) {
                    MathComponent component = currentField.getArgument(currentOffset);
                    currentField.delArgument(currentOffset);
                    field.addArgument(field.size(), component);
                }
                currentField = field;
                currentOffset = 0;

                // if ',' typed at the end of intermediate field of 2DArray or Matrix ... move to next field
            } else if (ch == parent.getFieldKey() && currentOffset == currentField.size() &&
                    parent.size() > currentField.getParentIndex() + 1 &&
                    (currentField.getParentIndex() + 1) % parent.columns() != 0) {

                currentField = parent.getArgument(currentField.getParentIndex() + 1);
                currentOffset = 0;

                // if ';' typed at the end of last field ... add new row
            } else if (ch == parent.getRowKey() && currentOffset == currentField.size() &&
                    parent.size() == currentField.getParentIndex() + 1) {

                parent.addRow();
                currentField = parent.getArgument(parent.size() - parent.columns());
                currentOffset = 0;

                // if ';' typed at the end of (not last) row ... move to next field
            } else if (ch == parent.getRowKey() && currentOffset == currentField.size() &&
                    (currentField.getParentIndex() + 1) % parent.columns() == 0) {

                currentField = parent.getArgument(currentField.getParentIndex() + 1);
                currentOffset = 0;

                // if ']' '}' typed at the end of last field ... move out of array
			} else if (ch == parent.getCloseKey() && parent.isArray()
					&& currentField.size() > 0) {

				ArrayList<MathComponent> removed = cut(currentField,
						currentOffset);
				insertReverse(parent.getParent(), parent.getParentIndex(),
						removed);
				
				
				currentOffset = parent.getParentIndex() + 1;
				currentField = (MathSequence) parent.getParent();
			} else if (
                    (ch == parent.getCloseKey() && parent.isMatrix()) &&
                            parent.size() == currentField.getParentIndex() + 1 &&
                            currentOffset == currentField.size()) {

                currentOffset = parent.getParentIndex() + 1;
                currentField = (MathSequence) parent.getParent();
            }

            // now functions, braces, apostrophes ...
        } else if (currentField.getParent() != null) {
            MathContainer parent = currentField.getParent();

            // if ',' typed at the end of intermediate field of function ... move to next field
            if (ch == ',' && currentOffset == currentField.size() &&
                    parent instanceof MathFunction &&
                    parent.size() > currentField.getParentIndex() + 1) {

                currentField = (MathSequence) parent.getArgument(currentField.getParentIndex() + 1);
                currentOffset = 0;

                // if ')' typed at the end of last field of function ... move after closing character
            } else if (ch == FUNCTION_CLOSE_KEY && currentOffset == currentField.size() &&
                    parent instanceof MathFunction &&
                    parent.size() == currentField.getParentIndex() + 1) {

                currentOffset = parent.getParentIndex() + 1;
                currentField = (MathSequence) parent.getParent();

                // if ')' typed at the end of last field of braces ... move after closing character
            } else {
                if (ch == ',') {
                    newCharacter(editorState, ch);
                    // return so that the old current field and offset are not set
                    return;
                }
            }

            // topmost container last ...
        } else {
            // if ';' typed and at the top level ... insert delimiter char
            if (ch == DELIMITER_KEY || ch == ',') {
                newCharacter(editorState, ch);
                // return so that the old current field and offset are not set
                return;
                //update();
            }
        }
        editorState.setCurrentField(currentField);
        editorState.setCurrentOffset(currentOffset);
    }

	private static void insertReverse(MathContainer parent, int parentIndex,
			ArrayList<MathComponent> removed) {
		for (int j = removed.size() - 1; j >= 0; j--) {
			MathComponent o = removed.get(j);
			int idx = parentIndex + (removed.size() - j);
			System.out.println(idx + ":" + o);
			parent.addArgument(idx, o);
		}

	}

	private static ArrayList<MathComponent> cut(MathSequence currentField,
			int currentOffset) {
		// TODO Auto-generated method stub
		ArrayList<MathComponent> removed = new ArrayList<MathComponent>();
		for (int i = currentField.size() - 1; i >= currentOffset; i--) {
			removed.add(currentField.getArgument(i));
			currentField.removeArgument(i);
		}

		return removed;
	}

	/**
	 * Insert symbol.
	 */
    public void escSymbol(EditorState editorState) {
        String name = argumentHelper.readCharacters(editorState);
        while (name.length() > 0) {
            if (metaModel.isSymbol(name)) {
                delCharacters(editorState, name.length());
                MetaCharacter meta = metaModel.getSymbol(name);
                newCharacter(editorState, meta);
                break;

            } else if (metaModel.isOperator(name)) {
                delCharacters(editorState, name.length());
                MetaCharacter meta = metaModel.getOperator(name);
                newCharacter(editorState, meta);
                break;

            } else {
                name = name.substring(1, name.length());
            }
        }
    }

    public void bkspContainer(EditorState editorState) {
        MathSequence currentField = editorState.getCurrentField();

        // if parent is function (cursor is at the beginning of the field)
        if (currentField.getParent() instanceof MathFunction) {
            MathFunction parent = (MathFunction) currentField.getParent();

            // fraction has operator like behavior
            if ("frac".equals(parent.getName())) {

                // if second operand is empty sequence
                if (currentField.getParentIndex() == 1 && currentField.size() == 0) {
                    int size = parent.getArgument(0).size();
                    delContaner(editorState, parent, parent.getArgument(0));
                    // move after included characters
                    editorState.addCurrentOffset(size);
                    // if first operand is empty sequence
                } else if (currentField.getParentIndex() == 1 && parent.getArgument(0).size() == 0) {
                    delContaner(editorState, parent, currentField);
                }

            } else if (metaModel.isGeneral(parent.getName())) {
                if (currentField.getParentIndex() == parent.getInsertIndex()) {
                    delContaner(editorState, parent, currentField);
                }

                // not a fraction, and cursor is right after the sign
            } else {
                if (currentField.getParentIndex() == 0) {
                    delContaner(editorState, parent, currentField);
                }
            }

            // if parent are empty array
        } else if (currentField.getParent() instanceof MathArray &&
				currentField.getParent().size() == 1) {

            MathArray parent = (MathArray) currentField.getParent();
			System.out.println(parent.getOpenKey());
            delContaner(editorState, parent, parent.getArgument(0));

            // if parent is 1DArray or Vector and cursor is at the beginning of intermediate the field
        } else if (currentField.getParent() instanceof MathArray &&
                (((MathArray) currentField.getParent()).is1DArray() ||
                        ((MathArray) currentField.getParent()).isVector()) &&
                currentField.getParentIndex() > 0) {

            int index = currentField.getParentIndex();
            MathArray parent = (MathArray) currentField.getParent();
            MathSequence field = parent.getArgument(index - 1);
            int size = field.size();
            editorState.setCurrentOffset(0);
            while (currentField.size() > 0) {

                MathComponent component = currentField.getArgument(0);
                currentField.delArgument(0);
                field.addArgument(field.size(), component);
            }
            parent.delArgument(index);
            editorState.setCurrentField(field);
            editorState.setCurrentOffset(size);
        }

        // we stop here for now
    }

    public void delContainer(EditorState editorState) {
        MathSequence currentField = editorState.getCurrentField();

        // if parent is function (cursor is at the end of the field)
        if (currentField.getParent() instanceof MathFunction) {
            MathFunction parent = (MathFunction) currentField.getParent();

            // fraction has operator like behavior
            if ("frac".equals(parent.getName())) {

                // first operand is current, second operand is empty sequence
                if (currentField.getParentIndex() == 0 && parent.getArgument(1).size() == 0) {
                    int size = parent.getArgument(0).size();
                    delContaner(editorState, parent, currentField);
                    // move after included characters
                    editorState.addCurrentOffset(size);

                    // first operand is current, and first operand is empty sequence
                } else if (currentField.getParentIndex() == 0 && (currentField).size() == 0) {
                    delContaner(editorState, parent, parent.getArgument(1));
                }
            }

            // if parent are empty braces
        } else if (currentField.getParent() instanceof MathArray &&
                currentField.getParent().size() == 1 &&
                currentField.size() == 0) {
            MathArray parent = (MathArray) currentField.getParent();
            int size = parent.getArgument(0).size();
            delContaner(editorState, parent, parent.getArgument(0));
            // move after included characters
            editorState.addCurrentOffset(size);

            // if parent is 1DArray or Vector and cursor is at the end of the field
        } else if (currentField.getParent() instanceof MathArray &&
                (((MathArray) currentField.getParent()).is1DArray() ||
                        ((MathArray) currentField.getParent()).isVector()) &&
                currentField.getParentIndex() + 1 < currentField.getParent().size()) {

            int index = currentField.getParentIndex();
            MathArray parent = (MathArray) currentField.getParent();
            MathSequence field = parent.getArgument(index + 1);
            int size = currentField.size();
            while (currentField.size() > 0) {

                MathComponent component = currentField.getArgument(0);
                currentField.delArgument(0);
                field.addArgument(field.size(), component);
            }
            parent.delArgument(index);
            editorState.setCurrentField(field);
            editorState.setCurrentOffset(size);
        }

        // we stop here for now
    }

    public void bkspCharacter(EditorState editorState) {
        int currentOffset = editorState.getCurrentOffset();
        if (currentOffset > 0) {
            editorState.getCurrentField().delArgument(currentOffset - 1);
            editorState.decCurrentOffset();
        } else {
            bkspContainer(editorState);
        }
    }

    public void delCharacter(EditorState editorState) {
        int currentOffset = editorState.getCurrentOffset();
        MathSequence currentField = editorState.getCurrentField();
        if (currentOffset < currentField.size()) {
            currentField.delArgument(currentOffset);
        } else {
            delContainer(editorState);
        }
    }

    private void delContaner(EditorState editorState, MathContainer container, MathSequence operand) {
        if (container.getParent() instanceof MathSequence) {
            // when parent is sequence
            MathSequence parent = (MathSequence) container.getParent();
            int offset = container.getParentIndex();
            // delete container
            parent.delArgument(container.getParentIndex());
            // add content of operand
            while (operand.size() > 0) {
                MathComponent element = operand.getArgument(operand.size() - 1);
                operand.delArgument(operand.size() - 1);
                parent.addArgument(offset, element);
            }
            editorState.setCurrentField(parent);
            editorState.setCurrentOffset(offset);
        }
    }

    private void delCharacters(EditorState editorState, int length) {
        int currentOffset = editorState.getCurrentOffset();
        MathSequence currentField = editorState.getCurrentField();
        while (length > 0 && currentOffset > 0 && currentField.getArgument(currentOffset - 1) instanceof MathCharacter) {

            MathCharacter character = (MathCharacter) currentField.getArgument(currentOffset - 1);
            if (character.isOperator() || character.isSymbol()) {
                break;
            }
            currentField.delArgument(currentOffset - 1);
            currentOffset--;
            length--;
        }
        editorState.setCurrentOffset(currentOffset);
    }

    /**
     * remove characters before and after cursor
     *
     * @param editorState
     * @param lengthBeforeCursor
     * @param lengthAfterCursor
     */
    public void removeCharacters(EditorState editorState, int lengthBeforeCursor, int lengthAfterCursor) {
        if (lengthBeforeCursor == 0 && lengthAfterCursor == 0) {
            return; // nothing to delete
        }
        MathSequence seq = editorState.getCurrentField();
        for (int i = 0; i < lengthBeforeCursor; i++) {
            editorState.decCurrentOffset();
            seq.delArgument(editorState.getCurrentOffset());
        }
        for (int i = 0; i < lengthAfterCursor; i++) {
            seq.delArgument(editorState.getCurrentOffset());
        }
    }

    /**
     * set ret to characters (no digit) around cursor
     *
     * @param ret
     * @return word length before cursor
     */
    public int getWordAroundCursor(EditorState editorState, StringBuilder ret) {
        int pos = editorState.getCurrentOffset();
        MathSequence seq = editorState.getCurrentField();

        StringBuilder before = new StringBuilder();
        int i;
        for (i = pos - 1; i >= 0; i--) {
            try {
                before.append(getLetter(seq.getArgument(i)));
            } catch (Exception e) {
                break;
            }
        }
        int lengthBefore = pos - i - 1;

        StringBuilder after = new StringBuilder();
        for (i = pos; i < seq.size(); i++) {
            try {
                after.append(getLetter(seq.getArgument(i)));
            } catch (Exception e) {
                break;
            }
        }
        before.reverse();
        ret.append(before);
        ret.append(after);

        return lengthBefore;

    }

	public boolean deleteSelection(EditorState editorState) {
		boolean nonempty = false;
		if (editorState.getSelectionStart() != null) {
			MathContainer parent = editorState.getSelectionStart().getParent();
            int end, start;
            if (parent == null) {
                // all the formula is selected
                parent = editorState.getRootComponent();
                start = 0;
                end = parent.size() - 1;
            } else {
                end = parent.indexOf(editorState.getSelectionEnd());
                start = parent.indexOf(editorState.getSelectionStart());
            }
            if (end >= 0 && start >= 0) {
				for (int i = end; i >= start; i--) {
					parent.delArgument(i);
					nonempty = true;
				}

				editorState.setCurrentOffset(start);
			}
		}
		editorState.resetSelection();
		return nonempty;

	}

    public boolean handleChar(EditorState editorState, char ch) {
        boolean handled = false;
        // backspace, delete and escape are handled for key down
        if (ch == 8 || ch == 127 || ch == 27) {
            return true;
        }
        deleteSelection(editorState);
        MetaModel meta = editorState.getMetaModel();
        if (isArrayCloseKey(ch, editorState) || ch == InputController.FUNCTION_CLOSE_KEY) {
            endField(editorState, ch);
            handled = true;
        } else if (meta.isFunctionOpenKey(ch)) {
            newBraces(editorState, ch);
            handled = true;
		} else if (mCreateFrac && ch == '^') {
            newScript(editorState, "^");
            handled = true;
        } else if (ch == '_') {
            newScript(editorState, "_");
            handled = true;
        } else if (mCreateFrac && ch == '/') { // slash used in android ggb keyboard
            newFunction(editorState, "frac", 1);
            handled = true;
        } else if (mCreateFrac && ch == 47) { // simple / char
            newFunction(editorState, "frac", 1);
            handled = true;
        } else if (ch == 8730) { // square root char
            newFunction(editorState, "sqrt", 0);
            handled = true;
        } else if (meta.isArrayOpenKey(ch)) {
            newArray(editorState, 1, ch);
            handled = true;
        } else if (ch == 8226) { // big dot char
            newOperator(editorState, '*');
            handled = true;
        } else if (ch == 215) { // multiplication cross char
            newOperator(editorState, '*');
            handled = true;
        } else if (meta.isOperator("" + ch)) {
            newOperator(editorState, ch);
            handled = true;
        } else if (meta.isSymbol("" + ch)) {
            newSymbol(editorState, ch);
            handled = true;
        } else if (meta.isCharacter("" + ch)) {
            newCharacter(editorState, ch);
            handled = true;
        }
        return handled;
    }

    private boolean isArrayCloseKey(char key, EditorState editorState) {
        MathContainer parent = editorState.getCurrentField().getParent();
        if (parent != null && parent instanceof MathArray) {
            MathArray array = (MathArray) parent;
            return array.getCloseKey() == key;
        }
        return false;
    }

    private boolean mCreateFrac = true;
	private MathField mathField;

    public void setCreateFrac(boolean createFrac) {
        this.mCreateFrac = createFrac;
    }

    public boolean getCreateFrac() {
        return mCreateFrac;
    }

	public void paste() {
		if (this.mathField != null) {
			this.mathField.paste();
		}

	}

	public void copy() {
		if (this.mathField != null) {
			this.mathField.copy();
		}

	}

	public void setMathField(MathField mathField) {
		this.mathField = mathField;

	}

	public MathSequence getSelectionText(EditorState editorState) {
		if (editorState.getSelectionStart() != null) {
			MathContainer parent = editorState.getSelectionStart().getParent();
			int end, start;
			if (parent == null) {
				// all the formula is selected
				return editorState.getRootComponent();

			}
			MathSequence seq = new MathSequence();
			end = parent.indexOf(editorState.getSelectionEnd());
			start = parent.indexOf(editorState.getSelectionStart());
			if (end >= 0 && start >= 0) {
				for (int i = start; i <= end; i++) {
					seq.addArgument(parent.getArgument(i).copy());
				}

				// editorState.setCurrentOffset(start);
			}
			return seq;
		}
		// editorState.resetSelection();
		return null;
	}
}
