package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.geos.GeoInputBox;

/**
 * MathField-capable editor for input boxes on Euclidian View.
 *
 * @author Laszlo
 */
public interface SymbolicEditor {

	public void attach(GeoInputBox geoInputBox, GRectangle bounds);

	public void hide();
}
