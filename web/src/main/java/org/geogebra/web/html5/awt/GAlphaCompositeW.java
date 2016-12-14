package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GAlphaComposite;

public class GAlphaCompositeW implements GAlphaComposite {

	final public static GAlphaCompositeW Src = new GAlphaCompositeW(1.0f);
	private float alpha;

	public GAlphaCompositeW(float alpha) {
		this.alpha = alpha;
	}

	public float getAlpha() {
		return alpha;
	}

	public static GAlphaCompositeW getInstance(float a) {
		return new GAlphaCompositeW(a);
	}

}
