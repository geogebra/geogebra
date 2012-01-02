package geogebra.awt;

import java.awt.Shape;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.main.Application;

public class Area implements geogebra.common.awt.Area, geogebra.awt.Shape{
	private java.awt.geom.Area impl;
	
	/*
	public Area(GeneralPathClipped boundingPath) {
		impl = new java.awt.geom.Area(geogebra.awt.GenericShape.getAwtShape(boundingPath));
	}*/
	
	public Area() {
		impl = new java.awt.geom.Area();
	}
	public Area(Shape shape) {
		impl = new java.awt.geom.Area(shape);
	}
	public Area(geogebra.common.awt.Shape shape) {
		impl = new java.awt.geom.Area(geogebra.awt.GenericShape.getAwtShape(shape));
	}
	public static java.awt.geom.Area getAWTArea(geogebra.common.awt.Area a){
		if(!(a instanceof Area)){
			if(a!=null) Application.debug("other type");
			return null;
		}
		return ((Area)a).impl;
	}
	public void subtract(geogebra.common.awt.Area a) {
		if(!(a instanceof Area))
			return;
		impl.subtract(((Area)a).impl);
	}
	public void add(geogebra.common.awt.Area a) {
		if(!(a instanceof Area))
			return;
		impl.add(((Area)a).impl);
	}
	public void intersect(geogebra.common.awt.Area a) {
		if(!(a instanceof Area))
			return;
		impl.intersect(((Area)a).impl);
	}
	public void exclusiveOr(geogebra.common.awt.Area a) {
		if(!(a instanceof Area))
			return;
		impl.exclusiveOr(((Area)a).impl);
	}

	public boolean intersects(int x, int y, int w, int h) {
		return impl.intersects(x, y, w, h);
	}

	public boolean contains(int x, int y) {
		return impl.contains(x, y);
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

	public PathIterator getPathIterator(AffineTransform affineTransform) {
		// TODO Auto-generated method stub
		return new geogebra.awt.PathIterator(impl.getPathIterator(geogebra.awt.AffineTransform.getAwtAffineTransform(affineTransform)));
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		// TODO Auto-generated method stub
		return new geogebra.awt.PathIterator(impl.getPathIterator(geogebra.awt.AffineTransform.getAwtAffineTransform(at), flatness));
	}

	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	public boolean intersects(Rectangle2D r) {
		return impl.intersects(geogebra.awt.GenericRectangle2D.getAWTRectangle2D(r));
	}

	public boolean isEmpty() {
		return impl.isEmpty();
	}
}
