package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GColorN;

public class GColorW extends GColorN {

	private final int r;
	private final int g;
	private final int b;
	private final int alpha;


	public GColorW(int r, int g, int b, int alpha) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.alpha = alpha;
	}

	@Override
	public int getRed() {
		return r;
	}

	@Override
	public int getGreen() {
		return g;
	}

	@Override
	public int getBlue() {
		return b;
	}

	@Override
	public int getAlpha() {
		return alpha;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof GColorW)) {
			return false;
		}
		GColorW other = (GColorW) object;
		return other.r == this.r && other.g == this.g && other.b == this.b
		        && other.alpha == this.alpha;
	}

	@Override
	public int hashCode() {
		return ((((getRed() * 256) + getGreen()) * 256) + getBlue()) * 256
				+ getAlpha();
	}



}
