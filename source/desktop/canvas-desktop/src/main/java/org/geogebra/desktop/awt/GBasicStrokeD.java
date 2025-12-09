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

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Path2D;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GShape;

/**
 * Desktop wrapper for stroke
 * 
 */
public class GBasicStrokeD implements GBasicStroke {
	private BasicStroke impl;

	public GBasicStrokeD(BasicStroke basicStroke) {
		impl = basicStroke;
	}

	public GBasicStrokeD(double f, int cap, int join) {
		impl = new BasicStroke((float) f, cap, join);
	}

	public GBasicStrokeD(double f) {
		impl = new BasicStroke((float) f);
	}

	@Override
	public int getEndCap() {
		return impl.getEndCap();
	}

	@Override
	public double getMiterLimit() {
		return impl.getMiterLimit();
	}

	@Override
	public int getLineJoin() {
		return impl.getLineJoin();
	}

	@Override
	public GShape createStrokedShape(GShape shape, int capacity) {
		Shape shapeD = GGenericShapeD.getAwtShape(shape);
		if (shapeD instanceof Path2D) {
			Path2D p2d = (Path2D) shapeD;
			if (p2d.getCurrentPoint() != null
					&& Double.isNaN(p2d.getCurrentPoint().getX())) {
				// Log.debug("fix kicks in");
				return new GGenericShapeD(shapeD);
			}
		}
		return new GGenericShapeD(impl.createStrokedShape(shapeD));
	}

	@Override
	public double getLineWidth() {
		return impl.getLineWidth();
	}

	@Override
	public double[] getDashArray() {
		return AwtFactory.floatToDouble(impl.getDashArray());
	}

	public BasicStroke getImpl() {
		return impl;
	}

}
