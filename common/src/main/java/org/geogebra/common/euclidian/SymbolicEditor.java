package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.geos.GeoInputBox;

/**
 * MathField-capable editor for input boxes on EuclidianView.
 *
 * @author Laszlo
 */
public interface SymbolicEditor {

	/**
	 * Attach the symbolic editor to the specified input box
	 * for editing it.
	 *
	 * @param geoInputBox
	 * 			GeoInputBox to edit.
	 *
	 * @param bounds
	 * 			place to attach the editor to.
	 */
	void attach(GeoInputBox geoInputBox, GRectangle bounds);

	/**
	 * Hide the editor if it was attached.
	 */
	void hide();

	/**
	 *
	 * @return if editor is clicked.
	 */
	boolean isClicked(GPoint point);
}
