package geogebra.common.awt;


public interface Shape {

	boolean intersects(int i, int j, int k, int l);

	boolean contains(int x, int y);

	Rectangle getBounds();

	Rectangle2D getBounds2D();

	boolean contains(geogebra.common.awt.Rectangle rectangle);
	
	boolean contains(double xTry, double yTry);

	 PathIterator getPathIterator(AffineTransform affineTransform);
	
	 public PathIterator getPathIterator(AffineTransform at, double flatness);

	 public boolean intersects(double x, double y, double w, double h);

	 public boolean intersects(Rectangle2D r);

}
