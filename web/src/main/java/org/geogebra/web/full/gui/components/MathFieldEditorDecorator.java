package org.geogebra.web.full.gui.components;

/**
 * Decorator for MathFieldEditor
 *
 * @author laszlo
 */
public interface MathFieldEditorDecorator {

	/**
	 * Update editor state.
	 */
	void update();

	/**
	 * Show the editor
	 */
	void show();

	/**
	 * Hide the editor
	 */
	void hide();
}
