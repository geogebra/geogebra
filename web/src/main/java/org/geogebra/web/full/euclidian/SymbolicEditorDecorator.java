package org.geogebra.web.full.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.full.gui.components.MathFieldEditorDecoratorBase;

public class SymbolicEditorDecorator extends MathFieldEditorDecoratorBase {
	private int baseline;
	private double fontSize;

	/**
	 *
	 * @param editor to decorate.
	 * @param fontSize to use.
	 */
	SymbolicEditorDecorator(MathFieldEditor editor, int fontSize) {
		super(editor);
		this.fontSize = fontSize;
		editor.addStyleName("evInputEditor");
		editor.setFontSize(fontSize);
	}

	@Override
	public void update() {
		updateSize();
	}

	private void updateSize() {
		if (getHeight() > 0) {
			setTop(baseline - getHeight() / 2d);
		}
	}

	/**
	 * Updates editor bounds, colors amd font size
	 * according to the geoInputBox
	 *
	 * @param bounds to set.
	 * @param geoInputBox the currently edited geo.
	 */
	void update(GRectangle bounds, GeoInputBox geoInputBox) {
		updateBounds(bounds);
		setForegroundColor(geoInputBox.getObjectColor());
		setBackgroundColor(geoInputBox.getBackgroundColor());
		setFontSize(fontSize * geoInputBox.getFontSizeMultiplier());

	}

	private void updateBounds(GRectangle bounds) {
		double fieldWidth = bounds.getWidth() - PADDING_LEFT;
		setLeft(bounds.getX());
		setTop(bounds.getY());
		setWidth(fieldWidth);
		setHeight(bounds.getHeight());

		baseline = (int) (bounds.getY() + bounds.getHeight() / 2);
	}
}
