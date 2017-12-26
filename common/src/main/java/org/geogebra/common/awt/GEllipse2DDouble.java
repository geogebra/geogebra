package org.geogebra.common.awt;

public interface GEllipse2DDouble extends GRectangularShape {

	public void setFrame(double xUL, double yUL, double d, double e);

	public void setFrameFromCenter(double centerX, double centerY,
			double cornerX, double cornerY);

}
