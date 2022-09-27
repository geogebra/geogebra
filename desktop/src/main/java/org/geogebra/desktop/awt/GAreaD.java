package org.geogebra.desktop.awt;

import java.awt.Shape;
import java.awt.geom.Area;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;

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

	public GAreaD(GShape shape) {
		impl = new Area(GGenericShapeD.getAwtShape(shape));
	}

	@Override
	public void subtract(GArea a) {
		if (!(a instanceof GAreaD)) {
			return;
		}
		impl.subtract(((GAreaD) a).impl);
	}

	@Override
	public void add(GArea a) {
		if (!(a instanceof GAreaD)) {
			return;
		}
		impl.add(((GAreaD) a).impl);
	}

	@Override
	public void intersect(GArea a) {
		if (!(a instanceof GAreaD)) {
			return;
		}
		impl.intersect(((GAreaD) a).impl);
	}

	@Override
	public void exclusiveOr(GArea a) {
		if (!(a instanceof GAreaD)) {
			return;
		}
		impl.exclusiveOr(((GAreaD) a).impl);
	}

	@Override
	public boolean intersects(int x, int y, int w, int h) {
		return impl.intersects(x, y, w, h);
	}

	@Override
	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	@Override
	public GRectangle getBounds() {
		return new GRectangleD(impl.getBounds());
	}

	@Override
	public GRectangle2D getBounds2D() {
		return new GGenericRectangle2DD(impl.getBounds2D());
	}

	@Override
	public boolean contains(GRectangle2D rectangle) {
		return impl.contains(GRectangleD.getAWTRectangle2D(rectangle));
	}

	@Override
	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	@Override
	public Shape getAwtShape() {
		return impl;
	}

	@Override
	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new GPathIteratorD(impl.getPathIterator(
				GAffineTransformD.getAwtAffineTransform(affineTransform)));
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	@Override
	public boolean intersects(GRectangle2D r) {
		return impl.intersects(GGenericRectangle2DD.getAWTRectangle2D(r));
	}

	@Override
	public boolean isEmpty() {
		return impl.isEmpty();
	}

}
