package org.geogebra.desktop.awt;

import java.awt.Color;
import java.awt.GradientPaint;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGradientPaint;

public class GGradientPaintD implements GGradientPaint {
	private GradientPaint impl;

	public GGradientPaintD(GradientPaint copyg) {
		impl = new GradientPaint((float) copyg.getPoint1().getX(),
				(float) copyg.getPoint1().getY(),
				new Color(copyg.getColor1().getRed(),
						copyg.getColor1().getGreen(),
						copyg.getColor1().getBlue(),
						copyg.getColor1().getAlpha()),
				(float) copyg.getPoint2().getX(),
				(float) copyg.getPoint2().getY(),
				new Color(copyg.getColor2().getRed(),
						copyg.getColor2().getGreen(),
						copyg.getColor2().getBlue(),
						copyg.getColor2().getAlpha()));
	}

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
