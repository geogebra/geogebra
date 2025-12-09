/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.awt;

import java.awt.Shape;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;

public class GGenericShapeD implements GShapeD {
	private Shape impl;

	private GGenericShapeD() {
	}

	@Override
	public boolean intersects(int x, int y, int w, int h) {
		return impl.intersects(x, y, w, h);
	}

	/**
	 * @param s cross-platform shape
	 * @return native shape
	 */
	public static Shape getAwtShape(GShape s) {
		if (s instanceof GShapeD) {
			return ((GShapeD) s).getAwtShape();
		}
		if (s != null) {
			Log.debug("other type " + s);
		}
		return null;
	}

	/**
	 * @param s native shape
	 */
	public GGenericShapeD(Shape s) {
		this();
		impl = s;
	}

	@Override
	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	@Override
	public GRectangleD getBounds() {
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

	public void setImpl(Shape newImpl) {
		impl = newImpl;
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
}
