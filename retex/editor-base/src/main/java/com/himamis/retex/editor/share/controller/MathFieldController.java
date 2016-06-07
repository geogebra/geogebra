package com.himamis.retex.editor.share.controller;

import java.util.ArrayList;

import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.model.MathComponent;
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
			updateFormula(mathFormula, editorState.getCurrentField(),
					editorState.getCurrentOffset(),
					editorState.getSelectionStart(),
					editorState.getSelectionEnd());
        } else {
			updateFormula(mathFormula, null, 0, null, null);
        }
		updateMathField(focusEvent);
    }

	private void updateMathField(boolean focusEvent) {
        if (mathField.hasParent()) {
            if (!focusEvent) {
                // prevent infinite focusChanged <-> requestLayout event cycle
                mathField.requestLayout();
            }
            mathField.repaint();
        }
    }

	private void updateFormula(MathFormula mathFormula,
			MathSequence currentField, int currentOffset,
			MathComponent selectionStart, MathComponent selectionEnd) {
		String serializedFormula = texSerializer.serialize(mathFormula,
				currentField, currentOffset, selectionStart, selectionEnd);

		try {
        TeXFormula texFormula = new TeXFormula(serializedFormula);
        TeXIcon renderer = texFormula.new TeXIconBuilder()
					.setStyle(TeXConstants.STYLE_DISPLAY).setSize(size)
					.setType(type).build();

			mathField.setTeXIcon(renderer);
			mathField.fireInputChangedEvent();
		} catch (Exception e) {
			System.out.println(selectionStart);
			System.out.println(selectionEnd);
			System.out.println(serializedFormula);
		}

    }

	public void getPath(MathFormula mathFormula, int x, int y,
			ArrayList<Integer> list) {
		String serializedFormula = texSerializer
				.serialize(mathFormula, null, 0);

		TeXFormula texFormula = new TeXFormula(serializedFormula);
		TeXIcon renderer = texFormula.new TeXIconBuilder()
				.setStyle(TeXConstants.STYLE_DISPLAY).setSize(size)
				.setType(type).build();
		renderer.getBox().getPath(x / size, y / size, list);
		mathField.setTeXIcon(renderer);
	}

	public void getSelectedPath(MathFormula mathFormula,
			ArrayList<Integer> list, MathSequence currentField,
			int currentOffset) {
		String serializedFormula = texSerializer.serialize(mathFormula,
				currentField, currentOffset);

		TeXFormula texFormula = new TeXFormula(serializedFormula);
		TeXIcon renderer = texFormula.new TeXIconBuilder()
				.setStyle(TeXConstants.STYLE_DISPLAY).setSize(size)
				.setType(type).build();
		renderer.getBox().getSelectedPath(list, 0);
		mathField.setTeXIcon(renderer);
	}

}
