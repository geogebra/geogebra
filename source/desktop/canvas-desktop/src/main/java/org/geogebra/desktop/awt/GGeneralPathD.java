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

import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;

public class GGeneralPathD implements GGeneralPath, GShapeD {

	private final GeneralPath impl;

	/**
	 * Path using even-odd winding
	 */
	public GGeneralPathD() {
		// default winding rule changed for ggb50 (for Polygons) #3983
		impl = new GeneralPath(Path2D.WIND_EVEN_ODD);
	}

	public GGeneralPathD(int rule) {
		impl = new GeneralPath(rule);
	}

	/**
	 * @param gp cross-platform path
	 * @return native path
	 */
	public static GeneralPath getAwtGeneralPath(GGeneralPath gp) {
		if (!(gp instanceof GGeneralPathD)) {
			if (gp != null) {
				Log.debug("other type");
			}
			return null;
		}
		return ((GGeneralPathD) gp).impl;
	}

	@Override
	public synchronized void moveTo(double f, double g) {
		impl.moveTo(f, g);
	}

	@Override
	public synchronized void reset() {
		impl.reset();
	}

	@Override
	public synchronized void lineTo(double x, double y) {
		impl.lineTo(x, y);
	}

	@Override
	public synchronized void closePath() {
		impl.closePath();
	}

	@Override
	public synchronized void append(GShape s, boolean connect) {
		if (!(s instanceof GShapeD)) {
			return;
		}
		impl.append(((GShapeD) s).getAwtShape(), connect);

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

	/**
	 * @param rectangle rectangle
	 * @return whether the rectangle is inside this path
	 */
	public boolean contains(GRectangle rectangle) {
		return impl.contains(GRectangleD.getAWTRectangle(rectangle));
	}

	@Override
	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	@Override
	public java.awt.Shape getAwtShape() {
		return impl;
	}

	@Override
	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		// TODO Auto-generated method stub
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
	public GShape createTransformedShape(GAffineTransform affineTransform) {
		return new GGenericShapeD(impl.createTransformedShape(
				((GAffineTransformD) affineTransform).getImpl()));
	}

	@Override
	public GPoint2D getCurrentPoint() {
		if (impl.getCurrentPoint() == null) {
			return null;
		}
		return new GPoint2D(impl.getCurrentPoint().getX(),
				impl.getCurrentPoint().getY());
	}

	@Override
	public boolean contains(GRectangle2D p) {
		return impl.contains(GGenericRectangle2DD.getAWTRectangle2D(p));
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return impl.contains(x, y, w, h);
	}

	@Override
	public boolean contains(GPoint2D p) {
		if (p == null) {
			return false;
		}
		return impl.contains(p.getX(), p.getY());
	}

	@Override
	public void curveTo(double x1, double y1, double x2, double y2, double x3,
			double y3) {
		impl.curveTo(x1, y1, x2, y2, x3, y3);

	}

	@Override
	public void quadTo(double x1, double y1, double x2, double y2) {
		impl.quadTo(x1, y1, x2, y2);
	}
}
