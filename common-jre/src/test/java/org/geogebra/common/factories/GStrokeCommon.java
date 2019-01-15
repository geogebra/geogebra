package org.geogebra.common.factories;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GShape;

/**
 * Dummy implementation of GStroke
 */
public class GStrokeCommon implements GBasicStroke {

	@Override
	public GShape createStrokedShape(GShape shape, int capacity) {
		return shape;
	}

	@Override
	public int getEndCap() {
		return 0;
	}

	@Override
	public double getMiterLimit() {
		return 0;
	}

	@Override
	public int getLineJoin() {
		return 0;
	}

	@Override
	public double getLineWidth() {
		return 0;
	}

	@Override
	public double[] getDashArray() {
		return null;
	}

}
