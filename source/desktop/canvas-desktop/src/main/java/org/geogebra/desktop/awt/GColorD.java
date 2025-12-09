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

import java.awt.Color;
import java.util.HashMap;

import org.geogebra.common.awt.GColor;

/**
 * Static methods for AWT &lt;-&gt; multiplatform color conversion
 *
 */
public class GColorD {

	private static HashMap<GColor, Color> map = new HashMap<>();

	/**
	 * @param gColor
	 *            multiplatform color
	 * @return awt color
	 */
	public static Color getAwtColor(GColor gColor) {
		Color ret = map.get(gColor);

		if (ret == null && gColor != null) {
			// color hasn't been used yet, need to create it
			ret = new Color(gColor.getRed(), gColor.getGreen(),
					gColor.getBlue(), gColor.getAlpha());
			synchronized (map) {
				map.put(gColor, ret);
			}

		}

		return ret;
	}

	/**
	 * @param color
	 *            awt color
	 * @return multiplatform color
	 */
	public static GColor newColor(Color color) {
		return color == null ? null
				: GColor.newColor(color.getRed(), color.getGreen(),
						color.getBlue());
	}

}
