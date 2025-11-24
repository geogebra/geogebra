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
