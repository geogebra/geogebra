package org.geogebra.web.awt;

import org.geogebra.common.awt.GAlphaComposite;

public class GAlphaCompositeW implements GAlphaComposite {

	final public static GAlphaCompositeW SRC = new GAlphaCompositeW(1.0);
	private double alpha;

	public GAlphaCompositeW(double alpha) {
		this.alpha = alpha;
	}

	public double getAlpha() {
		return alpha;
	}

}
