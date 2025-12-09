/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
