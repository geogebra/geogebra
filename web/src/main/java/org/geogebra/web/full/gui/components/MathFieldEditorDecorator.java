package org.geogebra.web.full.gui.components;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GRectangle;

/**
 * Decorator for MathFieldEditor
 *
 * @author laszlo
 */
public interface MathFieldEditorDecorator {

	/**
	 * Sets background color for the editor.
	 *
	 * @param backgroundColor  the color to set.
	 */
	void setBackgroundColor(GColor backgroundColor);

	/**
	 * Sets foreground color for the editor.
	 *
	 * @param foregroundColor  the color to set.
	 */
	void setForegroundColor(GColor foregroundColor);


	/**
	 * Sets editor font size.
	 *
	 * @param fontSize to set.
	 */
	void setFontSize(double fontSize);

	/**
	 * Update bounds of the editor.
	 *
	 * @param bounds to set.
	 */
	void updateBounds(GRectangle bounds);

	/**
	 * Update editor size if needed.
	 */
	void updateSize();
}
