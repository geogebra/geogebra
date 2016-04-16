package org.geogebra.desktop.awt;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Path2D;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.main.App;

/**
 * @author kondr
 * 
 */
public class GBasicStrokeD implements org.geogebra.common.awt.GBasicStroke {
	private BasicStroke impl;

	public GBasicStrokeD(BasicStroke basicStroke) {
		impl = basicStroke;
	}

	public GBasicStrokeD(float f, int cap, int join) {
		impl = new BasicStroke(f, cap, join);
	}

	public GBasicStrokeD(float f) {
		impl = new BasicStroke(f);
	}

	public static java.awt.BasicStroke getAwtStroke(GBasicStroke s) {
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
		Shape shapeD = GGenericShapeD.getAwtShape(shape);
		if (shapeD instanceof Path2D) {
			Path2D p2d = (Path2D) shapeD;
			if (p2d.getCurrentPoint() != null
					&& Double.isNaN(p2d.getCurrentPoint().getX())) {
				App.debug("fix kicks in");
				return new GGenericShapeD(shapeD);
			}
		}
		return new GGenericShapeD(impl.createStrokedShape(shapeD));
	}

	public float getLineWidth() {
		return impl.getLineWidth();
	}

	public float[] getDashArray() {
		return impl.getDashArray();
	}

}
