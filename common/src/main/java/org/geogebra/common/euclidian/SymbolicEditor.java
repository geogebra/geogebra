package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.geos.GeoInputBox;

/**
 * MathField-capable editor for input boxes on Euclidian View.
 *
 * @author Laszlo
 */
public abstract class SymbolicEditor {

	public SymbolicEditor()  {
		createMathField();
	}

	public void attach(GeoInputBox geoInputBox, GRectangle bounds) {
	}

	protected void createMathField() {}

	public void hide() {
	}
}
