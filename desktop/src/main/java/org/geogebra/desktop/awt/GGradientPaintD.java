package org.geogebra.desktop.awt;

import java.awt.Color;
import java.awt.GradientPaint;

import org.geogebra.common.awt.GColor;

public class GGradientPaintD implements org.geogebra.common.awt.GGradientPaint {
	private java.awt.GradientPaint impl;

	public GGradientPaintD(java.awt.GradientPaint copyg) {
		impl = new GradientPaint((float) copyg.getPoint1().getX(),
				(float) copyg.getPoint1().getY(),
				new Color(copyg
						.getColor1().getRed(), copyg.getColor1().getGreen(),
						copyg.getColor1().getBlue(), copyg.getColor1()
								.getAlpha()), (float) copyg.getPoint2().getX(),
				(float) copyg.getPoint2().getY(),
				new Color(copyg
						.getColor2().getRed(), copyg.getColor2().getGreen(),
						copyg.getColor2().getBlue(), copyg.getColor2()
								.getAlpha()));
	}

	public GGradientPaintD(float x1, float y1, GColor color1, float x2,
			float y2, GColor color2) {
		impl = new GradientPaint(x1, y1, GColorD.getAwtColor(color1),
				x2, y2, GColorD.getAwtColor(color2));
	}

	public java.awt.GradientPaint getPaint() {
		return impl;
	}
}
