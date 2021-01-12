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
import com.himamis.retex.renderer.share.Box;
import com.himamis.retex.renderer.share.BoxConsumer;
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
			final MathSequence currentField, final int currentOffset,
			final MathComponent selectionStart, MathComponent selectionEnd) {
		TeXFormula texFormula = new TeXFormula();
		texFormula.root = texBuilder.build(mathFormula.getRootComponent(),
				currentField, currentOffset, selectionStart, selectionEnd);

		MathComponent argumentTmp = currentField != null
				? currentField.getArgument(currentOffset == 0 ? 0 : currentOffset - 1)
				: null;
		final MathComponent argument = argumentTmp == null ? TeXBuilder.SELECTION : argumentTmp;

		try {
			final TeXIcon renderer = texFormula.new TeXIconBuilder()
					.setStyle(TeXConstants.STYLE_DISPLAY).setSize(size)
					.setType(type).build();
			renderer.setInsets(new Insets(1, 1, 1, 1));

			renderer.beforeFirst = currentOffset == 0;

			final MathComponent selectionParent = selectionStart == null ? null : selectionStart.getParent();
			final int selectionStartIndex = selectionStart == null ? 0 : selectionStart.getParentIndex();
			final int selectionEndIndex = selectionEnd == null ? 0 : selectionEnd.getParentIndex();

			renderer.selectionPosition = renderer.cursorPosition = null;

			renderer.getBox().inspect(new BoxConsumer() {
				@Override
				public void handle(Box box, BoxPosition position) {
					MathComponent component = texBuilder.getComponent(box.getAtom());

					if (component != null && component == selectionParent) {
						renderer.selectionBaseline = position.baseline;
					}

					if (component != null
							&& selectionParent != null
							&& component.getParent() == selectionParent
							&& selectionStartIndex <= component.getParentIndex()
							&& component.getParentIndex() <= selectionEndIndex) {
						if (renderer.selectionPosition == null) {
							renderer.selectionPosition = position;

							renderer.selectionX1 = position.x;
							renderer.selectionX2 = position.x + box.getWidth();

							renderer.boxWidth = box.getWidth();
							renderer.boxHeight = Math.max(box.getHeight(), position.scale);
							renderer.selectionDepth = box.getDepth();
						} else {
							renderer.selectionX1 = Math.min(renderer.selectionX1, position.x);
							renderer.selectionX2 = Math.max(renderer.selectionX2, position.x + box.getWidth());

							renderer.boxHeight =
									Math.max(position.scale, Math.max(box.getHeight(), renderer.boxHeight));
							renderer.selectionDepth = Math.max(box.getDepth(), renderer.selectionDepth);
						}
					} else if (component == argument) {
						renderer.cursorPosition = position;
						renderer.boxWidth = box.getWidth();
					}
				}
			}, new BoxPosition(0, 0, 1, 0));

			mathField.setTeXIcon(renderer);
			mathField.fireInputChangedEvent();
		} catch (Throwable t) {
			FactoryProvider
					.debugS("" + (t.getCause() != null ? t.getCause() : t));
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
		return null;
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
