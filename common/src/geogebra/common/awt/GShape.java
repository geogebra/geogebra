package geogebra.common.awt;


public interface GShape {

	boolean intersects(int i, int j, int k, int l);

	boolean contains(int x, int y);

	Rectangle getBounds();

	GRectangle2D getBounds2D();

	boolean contains(geogebra.common.awt.Rectangle rectangle);
	
	boolean contains(double xTry, double yTry);

	 GPathIterator getPathIterator(GAffineTransform affineTransform);
	
	 public GPathIterator getPathIterator(GAffineTransform at, double flatness);

	 public boolean intersects(double x, double y, double w, double h);

	 public boolean intersects(GRectangle2D r);

}
