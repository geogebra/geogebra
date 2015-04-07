package org.geogebra.desktop.awt;

import java.awt.Shape;
import java.awt.geom.Path2D;

import org.geogebra.common.awt.GShape;
import org.geogebra.common.main.App;

/**
 * @author kondr
 * 
 */
public class GBasicStrokeD implements org.geogebra.common.awt.GBasicStroke {
	private java.awt.BasicStroke impl;

	public GBasicStrokeD(java.awt.BasicStroke basicStroke) {
		impl = basicStroke;
	}

	public GBasicStrokeD(float f, int cap, int join) {
		impl = new java.awt.BasicStroke(f, cap, join);
	}

	public GBasicStrokeD(float f) {
		impl = new java.awt.BasicStroke(f);
	}

	public static java.awt.BasicStroke getAwtStroke(
			org.geogebra.common.awt.GBasicStroke s) {
		if (!(s instanceof GBasicStrokeD)) {
			if (s != null)
				App.debug("other type");
			return null;
		} else
			return ((GBasicStrokeD) s).impl;
	}

	public int getEndCap() {
		return impl.getEndCap();
	}

	public float getMiterLimit() {
		return impl.getMiterLimit();
	}

	public int getLineJoin() {
		return impl.getLineJoin();
	}

	public GShape createStrokedShape(GShape shape) {
		Shape shapeD = org.geogebra.desktop.awt.GGenericShapeD.getAwtShape(shape);
		if (shapeD instanceof Path2D) {
			Path2D p2d = (Path2D) shapeD;
			if (p2d.getCurrentPoint() != null
					&& Double.isNaN(p2d.getCurrentPoint().getX())) {
				App.debug("fix kicks in");
				return new org.geogebra.desktop.awt.GGenericShapeD(shapeD);
			}
		}
		return new org.geogebra.desktop.awt.GGenericShapeD(impl.createStrokedShape(shapeD));
	}

	public float getLineWidth() {
		return impl.getLineWidth();
	}

	public float[] getDashArray() {
		return impl.getDashArray();
	}

}
