package org.geogebra.web.full.gui.components;

import org.geogebra.common.awt.GColor;

/**
 * Decorator for MathFieldEditor
 *
 * @author laszlo
 */
public interface MathFieldEditorDecorator {

	/**
	 * Sets basic styles of the editor
	 */
	void decorate();

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
	 * Update editor size if needed.
	 */
	void updateSize();

	/**
	 * Show the editor
	 */
	void show();

	/**
	 * Hide the editor
	 */
	void hide();

	/**
	 *
	 * @return the font size
	 */
	double getFontSize();
}
