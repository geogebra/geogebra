package org.geogebra.desktop.awt;

import java.awt.Shape;
import java.awt.geom.Area;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.desktop.main.AppD;

public class GAreaD implements GArea, GShapeD {
	private Area impl;

	/*
	 * public Area(GeneralPathClipped boundingPath) { impl = new
	 * java.awt.geom.Area(geogebra.awt.GenericShape.getAwtShape(boundingPath));
	 * }
	 */

	public GAreaD() {
		impl = new Area();
	}

	public GAreaD(Shape shape) {
		impl = new Area(shape);
	}

	public GAreaD(org.geogebra.common.awt.GShape shape) {
		impl = new Area(
				org.geogebra.desktop.awt.GGenericShapeD.getAwtShape(shape));
	}

	public static java.awt.geom.Area getAWTArea(GArea a) {
		if (!(a instanceof GAreaD)) {
			if (a != null)
				AppD.debug("other type");
			return null;
		}
		return ((GAreaD) a).impl;
	}

	public void subtract(GArea a) {
		if (!(a instanceof GAreaD))
			return;
		impl.subtract(((GAreaD) a).impl);
	}

	public void add(GArea a) {
		if (!(a instanceof GAreaD))
			return;
		impl.add(((GAreaD) a).impl);
	}

	public void intersect(GArea a) {
		if (!(a instanceof GAreaD))
			return;
		impl.intersect(((GAreaD) a).impl);
	}

	public void exclusiveOr(GArea a) {
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
		return new org.geogebra.desktop.awt.GRectangleD(impl.getBounds());
	}

	public GRectangle2D getBounds2D() {
		return new org.geogebra.desktop.awt.GGenericRectangle2DD(impl.getBounds2D());
	}

	public boolean contains(GRectangle2D rectangle) {
		return impl.contains(GRectangleD
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
		return new org.geogebra.desktop.awt.GPathIteratorD(impl.getPathIterator(
				GAffineTransformD
						.getAwtAffineTransform(affineTransform)));
	}

	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		// TODO Auto-generated method stub
		return new GPathIteratorD(impl.getPathIterator(
				GAffineTransformD.getAwtAffineTransform(at),
				flatness));
	}

	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	public boolean intersects(GRectangle2D r) {
		return impl.intersects(GGenericRectangle2DD.getAWTRectangle2D(r));
	}

	public boolean isEmpty() {
		return impl.isEmpty();
	}
}
