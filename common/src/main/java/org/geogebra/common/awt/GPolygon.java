package org.geogebra.common.awt;

public interface GPolygon extends GShape {
	void reset();

	void invalidate();

	void translate(int deltaX, int deltaY);

	void addPoint(int x, int y);
	// GRectangle getBounds();

	// boolean contains(GPoint p);
	boolean contains(int x, int y);

	boolean contains(double x, double y);

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.2
	 */
	// boolean contains(GPoint2D p);
	boolean intersects(double x, double y, double w, double h);

	// boolean intersects(GRectangle2D r);
	boolean contains(double x, double y, double w, double h);
	// boolean contains(GRectangle2D r);
	GPathIterator getPathIterator(GAffineTransform at);

	// GPathIterator getPathIterator(GAffineTransform at, double flatness);
}
