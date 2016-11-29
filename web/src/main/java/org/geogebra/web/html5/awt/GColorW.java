package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GColorN;

public class GColorW extends GColorN {

	private final int value;


	/**
	 * @param r
	 * @param g
	 * @param b
	 * @param alpha
	 */
	public GColorW(int r, int g, int b, int alpha) {
		this.value = GColor.hashRGBA(r & 0xff, g & 0xff, b & 0xff,
				alpha & 0xff);
	}

	/**
	 * @return red (0 - 255)
	 */
	@Override
	public int getRed() {
		return (value >> 16) & 0xFF;
	}

	/**
	 * @return green (0 - 255)
	 */
	@Override
	public int getGreen() {
		return (value >> 8) & 0xFF;
	}

	/**
	 * @return blue (0 - 255)
	 */
	@Override
	public int getBlue() {
		return (value >> 0) & 0xFF;
	}

	/**
	 * @return alpha (0 - 255)
	 */
	@Override
	public int getAlpha() {
		return (value >> 24) & 0xff;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof GColorW)) {
			return false;
		}
		GColorW other = (GColorW) object;
		return other.value == this.value;
	}

	@Override
	public int hashCode() {
		return value;
	}



}
