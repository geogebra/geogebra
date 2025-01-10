package org.geogebra.web.full.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.full.gui.components.MathFieldEditorDecoratorBase;

public class SymbolicEditorDecorator extends MathFieldEditorDecoratorBase {

	public static final int LEFT_OFFSET = -1; // TODO: clean this up once...
	private final int fixMargin;

	/**
	 * @param editor to decorate.
	 * @param fixMargin to subtract from width.
	 */
	SymbolicEditorDecorator(MathFieldEditor editor, int fixMargin) {
		super(editor);
		this.fixMargin = fixMargin;
		editor.addStyleName("evInputEditor");
	}

	/**
	 * Updates editor bounds, colors amd font size
	 * according to the geoInputBox
	 *
	 * @param bounds to set.
	 * @param geoInputBox the currently edited geo.
	 * @param fontSize base font size.
	 */
	void update(GRectangle bounds, GeoInputBox geoInputBox, int fontSize) {
		updateBounds(bounds);
		setForegroundColor(geoInputBox.getObjectColor());
		setBackgroundColor(geoInputBox.getBackgroundColor());
		setFontSize(fontSize * geoInputBox.getFontSizeMultiplier() + 3);
	}

	private void updateBounds(GRectangle bounds) {
		double fieldWidth = bounds.getWidth() - fixMargin;
		setLeft(bounds.getX() + LEFT_OFFSET);
		setTop(bounds.getY());
		setWidth(fieldWidth);
		setHeight(bounds.getHeight());
	}
}
