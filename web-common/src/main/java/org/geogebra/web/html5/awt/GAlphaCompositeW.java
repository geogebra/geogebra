package org.geogebra.web.html5.awt;

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
