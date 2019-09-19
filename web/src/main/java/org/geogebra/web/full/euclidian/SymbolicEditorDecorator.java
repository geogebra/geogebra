package org.geogebra.web.full.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.full.gui.components.MathFieldEditorDecoratorBase;

public class SymbolicEditorDecorator extends MathFieldEditorDecoratorBase {
	private static final double BORDER_WIDTH = 1;
	private int mainHeight;
	private int top;

	SymbolicEditorDecorator(MathFieldEditor editor) {
		super(editor);
	}

	@Override
	public void updateBounds(GRectangle bounds) {
		super.updateBounds(bounds);
		top = (int) bounds.getY();
		mainHeight = (int) bounds.getHeight();

	}

	@Override
	public void updateSize() {
		int height = mathField.
				getInputTextArea().getOffsetHeight();
		double diff = mainHeight - getHeight();
		setHeight(height - 2 * BORDER_WIDTH);
		top += (diff / 2);
		setTop(top);
		mainHeight = getHeight();
	}
}
