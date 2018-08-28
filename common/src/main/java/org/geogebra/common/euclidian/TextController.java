package org.geogebra.common.euclidian;

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
	 * @return the created GeoText object.
	 */
	GeoText createText(GeoPointND loc, boolean rw);

	// /**
	// * Updates the editor.
	// *
	// * @param text
	// * the current text.
	// * @param x
	// * coordinate to place editor.
	// * @param y
	// * coordinate to place editor.
	// */
	// void updateEditor(GeoText text, int x, int y);

	/**
	 * Edit text
	 * 
	 * @param geo
	 *            to edit
	 */
	void edit(GeoText geo);
}
