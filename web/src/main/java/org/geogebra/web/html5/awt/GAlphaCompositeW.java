package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GAlphaComposite;

public class GAlphaCompositeW implements GAlphaComposite {

	final public static GAlphaCompositeW Src = new GAlphaCompositeW(1.0f);
	private double alpha;

	public GAlphaCompositeW(double alpha) {
		this.alpha = alpha;
	}

	public double getAlpha() {
		return alpha;
	}

	public static GAlphaCompositeW getInstance(double a) {
		return new GAlphaCompositeW(a);
	}

}
