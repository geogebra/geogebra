package com.himamis.retex.editor.share.controller;

import java.util.ArrayList;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.editor.SyntaxAdapter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.TeXBuilder;
import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.Box;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFont;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Insets;
import com.himamis.retex.renderer.share.platform.graphics.stubs.GraphicsStub;

/**
 * Controller for equation editor
 */
public class MathFieldController {

	@Weak
	private MathField mathField;

	private TeXSerializer texSerializer;

	private double size = 16;
	private int type = TeXFont.SERIF;

	private GraphicsStub graphics;
	private TeXBuilder texBuilder;

	/**
	 * @param mathField
	 *            editor
	 * @param directFormulaBuilder
	 *            whether to create JLM atoms without reparsing (experimental)
	 */
	public MathFieldController(MathField mathField, boolean directFormulaBuilder) {
		this.mathField = mathField;
		texSerializer = new TeXSerializer();
		if (directFormulaBuilder) {
			texBuilder = new TeXBuilder();
		}
		graphics = new GraphicsStub();
	}

	/**
	 * @param size
	 *            font size
	 */
	public void setSize(double size) {
		this.size = size;
	}

	/**
	 * @param type
	 *            font type
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return font type
	 */
	public int getFontType() {
		return type;
	}

	/**
	 * @param mathFormula
	 *            formula
	 * @param editorState
	 *            editor state
	 * @param focusEvent
	 *            whether this came from focus event (to avoid infinite focus
	 *            recursion)
	 */
	public void update(MathFormula mathFormula, EditorState editorState,
			boolean focusEvent) {
		if (mathField.hasFocus()) {
			updateWithCursor(mathFormula, editorState);
		} else {
			updateFormula(mathFormula, null, 0, null, null);
		}
		updateMathField(focusEvent);
	}

	/**
	 * Update the field, render cursor without checking focus
	 * 
	 * @param mathFormula
	 *            formula
	 * @param editorState
	 *            editor state
	 */
	public void updateWithCursor(MathFormula mathFormula,
			EditorState editorState) {
		updateFormula(mathFormula, editorState.getCurrentField(),
				editorState.getCurrentOffset(), editorState.getSelectionStart(),
				editorState.getSelectionEnd());

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
		TeXFormula texFormula = null;
		if (texBuilder != null) {
			texFormula = new TeXFormula();
			Atom root = texBuilder.build(mathFormula.getRootComponent(),
					currentField, currentOffset, selectionStart, selectionEnd);
			texFormula.root = root;
		}

		try {
			if (texFormula == null) {
				texFormula = new TeXFormula(serializedFormula);
			}
			TeXIcon renderer = texFormula.new TeXIconBuilder()
					.setStyle(TeXConstants.STYLE_DISPLAY).setSize(size)
					.setType(type).build();
			renderer.setInsets(new Insets(1, 1, 1, 1));
			mathField.setTeXIcon(renderer);
			mathField.fireInputChangedEvent();
		} catch (Exception e) {
			FactoryProvider.debugS(serializedFormula + ", selection"
					+ selectionStart + ":" + selectionEnd);
		} catch (Error e) {
			FactoryProvider
					.debugS("" + (e.getCause() != null ? e.getCause() : e));
		}

	}

	/**
	 * @param mathFormula
	 *            formula
	 * @param x
	 *            pointer x-coord
	 * @param y
	 *            pointer y-coord
	 * @param list
	 *            output list for path
	 * @return editor state after selection
	 */
	public EditorState getPath(MathFormula mathFormula, int x, int y,
			ArrayList<Integer> list) {
		if (texBuilder == null) {
			return null;
		}
		Atom root = texBuilder.build(mathFormula.getRootComponent(), null, 0,
				null, null);

		TeXFormula texFormula = new TeXFormula();
		texFormula.root = root;
		TeXIcon renderer = texFormula.new TeXIconBuilder()
				.setStyle(TeXConstants.STYLE_DISPLAY).setSize(size)
				.setType(type).build();
		renderer.getBox().getPath(x / size, y / size, list);
		Box current = renderer.getBox();
		EditorState es = new EditorState(mathFormula.getMetaModel());
		for (int i = 0; i < list.size() && current.getCount() > 0
				&& list.get(i) >= 0; i++) {
			if (list.get(i) == current.getCount()) {
				current = current.getChild(list.get(i) - 1);
				MathComponent comp = texBuilder.getComponent(current.getAtom());
				if (comp != null) {
					es.setCurrentField((MathSequence) comp.getParent());
					es.setCurrentOffset(comp.getParentIndex() + 1);
				}
				return es;
			}

			current = current.getChild(list.get(i));

		}

		MathComponent comp = texBuilder.getComponent(current.getAtom());
		es.setCurrentField((MathSequence) comp.getParent());
		es.setCurrentOffset(comp.getParentIndex());
		return es;
	}

	/**
	 * 
	 * @param mathFormula
	 *            formula
	 * @param list
	 *            output list for subtree indices
	 * @param currentField
	 *            current field
	 * @param currentOffset
	 *            current offset
	 */
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
		int idx = path.get(depth) <= 0 ? path.get(depth) + rootComponent.size()
				: path.get(depth);
		if (rootComponent.getArgument(idx) instanceof MathContainer
				&& path.size() > depth) {
			setSelectedPath((MathContainer) rootComponent.getArgument(idx),
					path, state, depth + 1);
		} else if (rootComponent instanceof MathSequence) {
			state.setCurrentOffset(idx);
			state.setCurrentField((MathSequence) rootComponent);
		}
	}

	/**
	 * Draws icon into stub graphics.
	 * 
	 * @param icon
	 *            rendered formula
	 */
	public void drawWithStub(TeXIcon icon) {
		graphics.reset();
		icon.paintIcon(null, graphics, 0, 0);
	}

	/**
	 * @return font size
	 */
	public double getFontSize() {
		return size;
	}

	/**
	 * Enables or disables line break in the editor.
	 * 
	 * @param breakLines
	 *            whether to enable break lines
	 */
	public void setLineBreakEnabled(boolean breakLines) {
		texSerializer.setLineBeakEnabled(breakLines);

	}

	public void setSyntaxAdapter(SyntaxAdapter syntaxAdapter) {
		this.texSerializer = new TeXSerializer(syntaxAdapter);
	}
}
