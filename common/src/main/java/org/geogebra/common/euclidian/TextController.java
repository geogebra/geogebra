package org.geogebra.common.euclidian;

import org.geogebra.common.euclidian.draw.DrawText;

/**
 * Handling text editor in Euclidian View.
 * 
 * @author laszlo
 *
 */
public interface TextController {

	/**
	 * Creates text editor for in-place editing if needed
	 * in Euclidian View
	 */
	void initEditor();

	/**
	 * Updates the editor.
	 * 
	 * @param dT
	 *            the current text.
	 */
	void updateEditor(DrawText dT);
}
