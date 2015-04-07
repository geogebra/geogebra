package org.geogebra.common.awt;

public interface GShape {

	boolean intersects(int i, int j, int k, int l);

	boolean contains(int x, int y);

	GRectangle getBounds();

	GRectangle2D getBounds2D();

	boolean contains(GRectangle2D rectangle);

	boolean contains(double xTry, double yTry);

	GPathIterator getPathIterator(GAffineTransform affineTransform);

	public boolean intersects(double x, double y, double w, double h);

	public boolean intersects(GRectangle2D r);

}
