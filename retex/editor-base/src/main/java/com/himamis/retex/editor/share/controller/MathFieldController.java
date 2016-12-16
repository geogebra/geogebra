package com.himamis.retex.editor.share.controller;

import java.util.ArrayList;

import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.graphics.stubs.GraphicsStub;

public class MathFieldController {

    private MathField mathField;
    private TeXSerializer texSerializer;

    private float size = 16;
    private int type = TeXFormula.SERIF;

	private GraphicsStub graphics;

    public MathFieldController(MathField mathField) {
        this.mathField = mathField;
        texSerializer = new TeXSerializer(mathField.getMetaModel());
		graphics = new GraphicsStub();
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
		drawWithStub(renderer);
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
		drawWithStub(renderer);
	}

	public void setSelectedPath(MathFormula mathFormula,
			ArrayList<Integer> path, EditorState state) {
		setSelectedPath(mathFormula.getRootComponent(), path, state, 0);
	}

	private void setSelectedPath(MathContainer rootComponent,
			ArrayList<Integer> path, EditorState state, int depth) {
		if (path.size() <= depth) {
			return;
		}
		int idx = path.get(depth) <= 0
				? path.get(depth) + rootComponent.size()
				: path.get(depth);
		if (rootComponent
				.getArgument(idx) instanceof MathContainer
				&& path.size() > depth) {
			setSelectedPath(
					(MathContainer) rootComponent.getArgument(idx),
					path,
					state, depth + 1);
		} else if (rootComponent instanceof MathSequence) {
			state.setCurrentOffset(idx);
			state.setCurrentField((MathSequence) rootComponent);
		}
	}

	public void drawWithStub(TeXIcon icon) {
		graphics.reset();
		icon.paintIcon(null, graphics, 0, 0);
	}
}
