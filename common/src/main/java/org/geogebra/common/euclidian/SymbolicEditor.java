package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GPoint;

/**
 * MathField-capable editor for input boxes on EuclidianView.
 *
 * @author Laszlo
 */
public interface SymbolicEditor {

	/**
	 * Hide the editor if it was attached.
	 */
	void hide();

	/**
	 * @param point
	 *            mouse coordinates
	 * @return if editor is clicked.
	 */
	boolean isClicked(GPoint point);
}
