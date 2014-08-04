package geogebra.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GRectangle2D;
import geogebra.main.AppD;

import java.awt.Shape;

public class GAreaD implements geogebra.common.awt.GArea, geogebra.awt.GShapeD {
	private java.awt.geom.Area impl;

	/*
	 * public Area(GeneralPathClipped boundingPath) { impl = new
	 * java.awt.geom.Area(geogebra.awt.GenericShape.getAwtShape(boundingPath));
	 * }
	 */

	public GAreaD() {
		impl = new java.awt.geom.Area();
	}

	public GAreaD(Shape shape) {
		impl = new java.awt.geom.Area(shape);
	}

	public GAreaD(geogebra.common.awt.GShape shape) {
		impl = new java.awt.geom.Area(
				geogebra.awt.GGenericShapeD.getAwtShape(shape));
	}

	public static java.awt.geom.Area getAWTArea(geogebra.common.awt.GArea a) {
		if (!(a instanceof GAreaD)) {
			if (a != null)
				AppD.debug("other type");
			return null;
		}
		return ((GAreaD) a).impl;
	}

	public void subtract(geogebra.common.awt.GArea a) {
		if (!(a instanceof GAreaD))
			return;
		impl.subtract(((GAreaD) a).impl);
	}

	public void add(geogebra.common.awt.GArea a) {
		if (!(a instanceof GAreaD))
			return;
		impl.add(((GAreaD) a).impl);
	}

	public void intersect(geogebra.common.awt.GArea a) {
		if (!(a instanceof GAreaD))
			return;
		impl.intersect(((GAreaD) a).impl);
	}

	public void exclusiveOr(geogebra.common.awt.GArea a) {
		if (!(a instanceof GAreaD))
			return;
		impl.exclusiveOr(((GAreaD) a).impl);
	}

	public boolean intersects(int x, int y, int w, int h) {
		return impl.intersects(x, y, w, h);
	}

	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	public GRectangle getBounds() {
		return new geogebra.awt.GRectangleD(impl.getBounds());
	}

	public GRectangle2D getBounds2D() {
		return new geogebra.awt.GGenericRectangle2DD(impl.getBounds2D());
	}

	public boolean contains(GRectangle2D rectangle) {
		return impl.contains(geogebra.awt.GRectangleD
				.getAWTRectangle2D(rectangle));
	}

	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	public Shape getAwtShape() {
		return impl;
	}

	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		// TODO Auto-generated method stub
		return new geogebra.awt.GPathIteratorD(
				impl.getPathIterator(geogebra.awt.GAffineTransformD
						.getAwtAffineTransform(affineTransform)));
	}

	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		// TODO Auto-generated method stub
		return new geogebra.awt.GPathIteratorD(impl.getPathIterator(
				geogebra.awt.GAffineTransformD.getAwtAffineTransform(at),
				flatness));
	}

	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	public boolean intersects(GRectangle2D r) {
		return impl.intersects(geogebra.awt.GGenericRectangle2DD
				.getAWTRectangle2D(r));
	}

	public boolean isEmpty() {
		return impl.isEmpty();
	}
}
