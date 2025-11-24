package org.geogebra.desktop.awt;

import java.awt.Shape;

import org.geogebra.common.awt.GShape;

public interface GShapeD extends GShape {

	/**
	 * @return the wrapped shape
	 */
	Shape getAwtShape();

}
