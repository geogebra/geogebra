package geogebra.awt;

import java.awt.geom.Rectangle2D;

public interface GRectangle2DD extends geogebra.common.awt.GRectangle2D,
		GRectangularShapeD {

	GRectangle2DD impl = null;

	Rectangle2D getImpl();
}