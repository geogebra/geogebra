package org.geogebra.desktop.awt;

import java.awt.GradientPaint;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGradientPaint;

public class GGradientPaintD implements GGradientPaint {
	private final GradientPaint impl;

	/**
	 * @param x1 start point x
	 * @param y1 start point y
	 * @param color1 start color
	 * @param x2 end point x
	 * @param y2 end point y
	 * @param color2 end color
	 */
	public GGradientPaintD(double x1, double y1, GColor color1, double x2,
			double y2, GColor color2) {
		impl = new GradientPaint((float) x1, (float) y1,
				GColorD.getAwtColor(color1), (float) x2, (float) y2,
				GColorD.getAwtColor(color2));
	}

	public GradientPaint getPaint() {
		return impl;
	}
}
