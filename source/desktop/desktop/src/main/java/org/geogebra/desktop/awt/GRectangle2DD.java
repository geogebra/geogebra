package org.geogebra.desktop.awt;

import java.awt.geom.Rectangle2D;

import org.geogebra.common.awt.GRectangle2D;

public interface GRectangle2DD extends GRectangle2D, GRectangularShapeD {

	/**
	 * @return wrapped rectangle
	 */
	Rectangle2D getImpl();
}