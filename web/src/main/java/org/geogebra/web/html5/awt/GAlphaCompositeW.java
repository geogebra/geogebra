package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GAlphaComposite;

public class GAlphaCompositeW implements GAlphaComposite {

	public static GAlphaCompositeW Src = new GAlphaCompositeW(SRC, 1.0f);
	private int srcOver;
	private float alpha;

	public GAlphaCompositeW(int srcOver, float alpha) {
		this.srcOver = srcOver;
		this.alpha = alpha;
	}

	public float getAlpha() {
		return alpha;
	}

	public static GAlphaCompositeW getInstance(int srcO, float a) {
		return new GAlphaCompositeW(srcO, a);
	}

}
