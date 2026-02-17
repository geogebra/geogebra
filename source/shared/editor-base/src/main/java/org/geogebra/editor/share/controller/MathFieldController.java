/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.editor.share.controller;

import java.util.ArrayList;

import org.geogebra.editor.share.editor.MathField;
import org.geogebra.editor.share.editor.SyntaxAdapter;
import org.geogebra.editor.share.serializer.TeXBuilder;
import org.geogebra.editor.share.serializer.TeXSerializer;
import org.geogebra.editor.share.tree.Formula;
import org.geogebra.editor.share.tree.InternalNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.SequenceNode;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.renderer.share.BoxPosition;
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

	private final GraphicsStub graphics;
	private final TeXBuilder texBuilder;

	/**
	 * @param mathField
	 *            editor
	 */
	public MathFieldController(MathField mathField) {
		this.mathField = mathField;
		texSerializer = new TeXSerializer();
		texBuilder = new TeXBuilder();
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
	 * @param simplePlaceholders Whether simple placeholders should be used for matrix editing
	 */
	public void useSimpleMatrixPlaceholders(boolean simplePlaceholders) {
		texSerializer.useSimpleMatrixPlaceholders(simplePlaceholders);
		texBuilder.useSimpleMatrixPlaceholders(simplePlaceholders);
	}

	/**
	 * @param formula
	 *            formula
	 * @param editorState
	 *            editor state
	 * @param focusEvent
	 *            whether this came from focus event (to avoid infinite focus
	 *            recursion)
	 */
	public void update(Formula formula, EditorState editorState,
			boolean focusEvent) {
		if (mathField.hasFocus()) {
			updateWithCursor(formula, editorState);
		} else {
			updateFormula(formula, null, 0, null, null);
		}
		updateMathField(focusEvent);
	}

	/**
	 * Update the field, render cursor without checking focus
	 * 
	 * @param formula
	 *            formula
	 * @param editorState
	 *            editor state
	 */
	public void updateWithCursor(Formula formula,
			EditorState editorState) {
		updateFormula(formula, editorState.getCurrentNode(),
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

	/**
	 * @param formula formula
	 * @param currentField field
	 * @return return icon without placeholders
	 */
	public TeXIcon buildIcon(Formula formula, SequenceNode currentField) {
		TeXFormula texFormula = new TeXFormula();
		boolean textMode = mathField.getInternal().getInputController().getPlainTextMode();
		texBuilder.enablePlaceholder(false);
		texFormula.root = texBuilder.build(formula.getRootNode(),
				currentField, -1, textMode);

		try {
			final TeXIcon renderer = texFormula.new TeXIconBuilder()
					.setStyle(TeXConstants.STYLE_DISPLAY).setSize(size)
					.setType(type).build();
			renderer.setInsets(new Insets(1, 1, 1, 1));

			texBuilder.enablePlaceholder(true);
			return renderer;
		} catch (Throwable t) {
			FactoryProvider.getInstance()
					.debug(t.getCause() != null ? t.getCause() : t);
		}
		return null;
	}

	private void updateFormula(Formula formula,
			SequenceNode currentField, int currentOffset,
			Node selectionStart, Node selectionEnd) {
		TeXFormula texFormula = new TeXFormula();
		boolean textMode = mathField.getInternal().getInputController().getPlainTextMode();
		texFormula.root = texBuilder.build(formula.getRootNode(),
				currentField, currentOffset, textMode);

		try {
			final TeXIcon renderer = texFormula.new TeXIconBuilder()
					.setStyle(TeXConstants.STYLE_DISPLAY).setSize(size)
					.setType(type).build();
			renderer.setInsets(new Insets(1, 1, 1, 1));
			Node input = selectionStart != null
					? selectionStart : currentField;
			while (input != null) {
				if (input.getParent() != null && input.getParent().isRenderingOwnPlaceholders()) {
					break;
				}
				input = input.getParent();
			}
			if (selectionStart == null) {
				CursorBoxConsumer consumer
						= new CursorBoxConsumer(texBuilder, currentField, currentOffset, input);
				renderer.getBox().inspect(consumer, new BoxPosition(0, 0, 1, 0));
				renderer.cursorPosition = consumer.getBounds();
			} else {
				SelectionBoxConsumer consumer
						= new SelectionBoxConsumer(texBuilder, selectionStart, selectionEnd, input);
				renderer.getBox().inspect(consumer, new BoxPosition(0, 0, 1, 0));
				renderer.selectionPosition = consumer.getBounds();
			}

			mathField.setTeXIcon(renderer);
			mathField.fireInputChangedEvent();
		} catch (Throwable t) {
			FactoryProvider.getInstance()
					.debug(t.getCause() != null ? t.getCause() : t);
		}
	}

	/**
	 * Updates x and y position of CursorBox
	 * @param formula
	 *            formula
	 * @param currentField
	 *            current field
	 * @param currentOffset
	 *            current offset
	 */
	public void updateCursorPosition(Formula formula,
			SequenceNode currentField,
			int currentOffset) {
		String serializedFormula = texSerializer.serialize(formula,
				currentField, currentOffset);

		TeXFormula texFormula = new TeXFormula(serializedFormula);
		TeXIcon renderer = texFormula.new TeXIconBuilder()
				.setStyle(TeXConstants.STYLE_DISPLAY).setSize(size)
				.setType(type).build();
		drawWithStub(renderer);
	}

	/**
	 * Update editor state to select node at given path.
	 * @param formula formula
	 * @param path selection path
	 * @param state editor state
	 */
	public void setSelectedPath(Formula formula,
			ArrayList<Integer> path, EditorState state) {
		setSelectedPath(formula.getRootNode(), path, state, 0);
	}

	private static void setSelectedPath(InternalNode rootComponent,
			ArrayList<Integer> path, EditorState state, int depth) {
		if (path.size() <= depth) {
			return;
		}
		int idx = path.get(depth) <= 0 ? path.get(depth) + rootComponent.size()
				: path.get(depth);
		if (rootComponent.getChild(idx) instanceof InternalNode
				&& path.size() > depth) {
			setSelectedPath((InternalNode) rootComponent.getChild(idx),
					path, state, depth + 1);
		} else if (rootComponent instanceof SequenceNode node) {
			state.setCurrentOffset(idx);
			state.setCurrentNode(node);
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

	/**
	 * @param syntaxAdapter syntax adapter
	 */
	public void setSyntaxAdapter(SyntaxAdapter syntaxAdapter) {
		texSerializer.setSyntaxAdapter(syntaxAdapter);
		texBuilder.setSyntaxAdapter(syntaxAdapter);
	}
}
