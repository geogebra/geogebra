package org.geogebra.common.euclidian;

import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Handling text editor in Euclidian View.
 * 
 * @author laszlo
 *
 */
public interface TextController {

	/**
	 * Creates in-place editable GeoText
	 * 
	 * @param loc
	 *            Text location.
	 * @param rw
	 *            specifies if RealWorld coordinates are used.
	 */
	GeoText createText(GeoPointND loc, boolean rw);

	/**
	 * Updates the editor.
	 * 
	 * @param dT
	 *            the current text.
	 */
	void updateEditor(DrawText dT);
}
