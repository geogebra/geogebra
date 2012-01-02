package geogebra.awt;

import java.awt.Shape;

import org.python.core.imp;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;

public class Line2D extends geogebra.common.awt.Line2D implements geogebra.awt.Shape{
	private java.awt.geom.Line2D impl;
	public Line2D(){
		impl = new java.awt.geom.Line2D.Double();
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

	public Shape getAwtShape() {
		return impl;
	}

	@Override
	public void setLine(double x1, double y1, double x2, double y2) {
		impl.setLine(x1, y1, x2, y2);
		
	}
	public PathIterator getPathIterator(AffineTransform affineTransform) {
		// TODO Auto-generated method stub
		return new geogebra.awt.PathIterator(impl.getPathIterator(geogebra.awt.AffineTransform.getAwtAffineTransform(affineTransform)));
	}
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		// TODO Auto-generated method stub
		return new geogebra.awt.PathIterator(impl.getPathIterator(geogebra.awt.AffineTransform.getAwtAffineTransform(at), flatness));
	}
	public boolean intersects(double x, double y, double w, double h) {
		// TODO Auto-generated method stub
		return impl.intersects(x, y, w, h);
	}
	public boolean intersects(Rectangle2D r) {
		// TODO Auto-generated method stub
		return impl.intersects(geogebra.awt.GenericRectangle2D.getAWTRectangle2D(r));
	}
	@Override
	public geogebra.common.awt.Point2D getP1() {
		java.awt.geom.Point2D p1 = impl.getP1();
		if (p1==null) return null;
		return new Point2D(p1.getX(), p1.getY());
	}
	@Override
	public geogebra.common.awt.Point2D getP2() {
		java.awt.geom.Point2D p2 = impl.getP2();
		if (p2==null) return null;
		return new Point2D(p2.getX(), p2.getY());
	}

}
