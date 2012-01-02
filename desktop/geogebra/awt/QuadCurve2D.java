package geogebra.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;
import geogebra.common.awt.Shape;

public class QuadCurve2D extends geogebra.common.awt.QuadCurve2D implements Shape{

	private java.awt.geom.QuadCurve2D.Double impl;
	
	public QuadCurve2D(){
		impl = new java.awt.geom.QuadCurve2D.Double();
	}

	@Override
	public void setCurve(double[] parpoints, int i) {
		impl.setCurve(parpoints, i);
	}


	
	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}
	
	public boolean intersects(double x, double y, double lengthX,
			double lengthY) {
		return impl.intersects(x, y, lengthX, lengthY);
	}
	
	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i, j, k, l);
	}
	public boolean contains(int x, int y) {
		return impl.contains(x,y);
	}
	
	public geogebra.awt.Rectangle getBounds() {
		return new geogebra.awt.Rectangle(impl.getBounds());
	}
	public Rectangle2D getBounds2D() {
		return new geogebra.awt.GenericRectangle2D(impl.getBounds2D());
	}
	public boolean contains(Rectangle rectangle) {
		return impl.contains(geogebra.awt.Rectangle.getAWTRectangle(rectangle));
	}
	
	public PathIterator getPathIterator(AffineTransform affineTransform) {
		return new geogebra.awt.PathIterator(impl.getPathIterator(geogebra.awt.AffineTransform.getAwtAffineTransform(affineTransform)));
	}
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new geogebra.awt.PathIterator(impl.getPathIterator(geogebra.awt.AffineTransform.getAwtAffineTransform(at), flatness));
	}

	public boolean intersects(Rectangle2D r) {
		return impl.intersects(geogebra.awt.GenericRectangle2D.getAWTRectangle2D(r));
	}

	public java.awt.Shape getAwtShape() {
		return impl;
	}
	
}
