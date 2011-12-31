package geogebra.awt;

import java.awt.Shape;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;
import geogebra.common.awt.RectangularShape;

public class Ellipse2DDouble extends geogebra.awt.RectangularShape implements geogebra.common.awt.Ellipse2DDouble{
	
		private java.awt.geom.Ellipse2D.Double impl;
	
	/*	public Double(java.awt.geom.Ellipse2D.Double ellipse2d) {
			impl = ellipse2d;
		}
	*/
		public Ellipse2DDouble() {
			impl = new java.awt.geom.Ellipse2D.Double();
		}

		public void setFrame(double xUL, double yUL, int diameter, int diameter2) {
			impl.setFrame(xUL, yUL, diameter, diameter2);	
		}

		public boolean intersects(int i, int j, int k, int l) {
			return impl.intersects(i,j,k,l);
		}

		public boolean contains(int x, int y) {
			return impl.contains(x,y);
		}

		public Rectangle getBounds() {
			return new geogebra.awt.Rectangle(impl.getBounds());
		}

		public Rectangle2D getBounds2D() {
			return new geogebra.awt.GenericRectangle2D(impl.getBounds2D());
		}

		public boolean contains(Rectangle rectangle) {
			return impl.contains(geogebra.awt.Rectangle.getAWTRectangle(rectangle));
		}

		public boolean contains(double xTry, double yTry) {
			return impl.contains(xTry, yTry);
		}

		public PathIterator getPathIterator(AffineTransform affineTransform) {
			return (PathIterator) impl.getPathIterator(geogebra.awt.AffineTransform.getAwtAffineTransform(affineTransform));
		}

		public PathIterator getPathIterator(AffineTransform at, double flatness) {
			return (PathIterator) impl.getPathIterator(geogebra.awt.AffineTransform.getAwtAffineTransform(at), flatness);
		}

		public boolean intersects(double x, double y, double w, double h) {
			return impl.intersects(x,y,w,h);
		}

		public boolean intersects(Rectangle2D r) {
			return impl.intersects(geogebra.awt.GenericRectangle2D.getAWTRectangle2D(r));
		}

		public Shape getAwtShape() {
			return impl;
		}


}
