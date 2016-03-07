package com.himamis.retex.editor.share.controller;

import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;

public class MathFieldController {

    private MathField mathField;
    private TeXSerializer texSerializer;

    private float size = 16;
    private int type = TeXFormula.SERIF;

    public MathFieldController(MathField mathField) {
        this.mathField = mathField;
        texSerializer = new TeXSerializer(mathField.getMetaModel());
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void update(MathFormula mathFormula, EditorState editorState, boolean focusEvent) {
        if (mathField.hasFocus()) {
            updateEditor(mathFormula, editorState.getCurrentField(), editorState.getCurrentOffset(), focusEvent);
        } else {
            updateEditor(mathFormula, null, 0, focusEvent);
        }
    }

    private void updateEditor(MathFormula mathFormula, MathSequence currentField, int currentOffset, boolean focusEvent) {
        updateFormula(mathFormula, currentField, currentOffset);
        if (mathField.hasParent()) {
            if (!focusEvent) {
                // prevent infinite focusChanged <-> requestLayout event cycle
                mathField.requestLayout();
            }
            mathField.repaint();
        }
    }

    private void updateFormula(MathFormula mathFormula, MathSequence currentField, int currentOffset) {
        String serializedFormula = texSerializer.serialize(mathFormula, currentField, currentOffset);

        TeXFormula texFormula = new TeXFormula(serializedFormula);
        TeXIcon renderer = texFormula.new TeXIconBuilder()
                .setStyle(TeXConstants.STYLE_DISPLAY)
                .setSize(size)
                .setType(type)
                .build();

        mathField.setTeXIcon(renderer);
    }
}
