package com.himamis.retex.editor.android;

import android.os.Bundle;

import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathSequence;

import java.util.ArrayList;

/**
 * Created by mathieu on 06/06/17.
 */

public class FormulaEditorBundleController {

    private static final String FORMULA_EDITOR_OFFSET = "formulaEditorOffset";
    private static final String FORMULA_EDITOR_PATH = "formulaEditorPath";
    private static final String FORMULA_EDITOR_ROOT = "formulaEditorRoot";

    static final public void addToBundle(FormulaEditor editor, Bundle bundle) {

        if (editor == null) {
            return;
        }

        EditorState editorState = editor.getEditorState();

        int currentOffset = editorState.getCurrentOffset();
        ArrayList<Integer> currentPath = editor.getCurrentPath(editorState.getCurrentField());
        MathComponent rootComponent = editorState.getRootComponent();

        bundle.putInt(FORMULA_EDITOR_OFFSET, currentOffset);
        bundle.putIntegerArrayList(FORMULA_EDITOR_PATH, currentPath);
        bundle.putSerializable(FORMULA_EDITOR_ROOT, rootComponent);
    }

    private int currentOffset;
    private ArrayList<Integer> currentPath;
    private MathSequence rootComponent;

    public FormulaEditorBundleController(Bundle bundle) {
        currentOffset = bundle.getInt(FORMULA_EDITOR_OFFSET);
        currentPath = bundle.getIntegerArrayList(FORMULA_EDITOR_PATH);
        rootComponent = (MathSequence) bundle.getSerializable(FORMULA_EDITOR_ROOT);
    }

    public void setFormula(FormulaEditor formulaEditor) {
        formulaEditor.setFormula(currentOffset, currentPath, rootComponent);
    }

}
