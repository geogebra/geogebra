package org.geogebra.desktop.awt;

import java.awt.geom.Rectangle2D;

public interface GRectangle2DD extends org.geogebra.common.awt.GRectangle2D,
		GRectangularShapeD {

	GRectangle2DD impl = null;

	Rectangle2D getImpl();
}