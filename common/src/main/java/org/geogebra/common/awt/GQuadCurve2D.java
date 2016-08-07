package org.geogebra.common.awt;

public interface GQuadCurve2D extends GShape {

	public abstract void setCurve(double[] parpoints, int i);

	public abstract void setCurve(double x1, double y1, double controlX,
			double controlY, double x2, double y2);

}
