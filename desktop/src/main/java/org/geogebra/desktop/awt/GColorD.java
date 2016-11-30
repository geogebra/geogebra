package org.geogebra.desktop.awt;

import java.awt.Color;
import java.util.HashMap;

import org.geogebra.common.awt.GColor;

public class GColorD {


	private static HashMap<GColor, Color> map = new HashMap<GColor, Color>();


	/**
	 * @param color
	 * @return
	 */
	public static Color getAwtColor(GColor that) {
		Color ret = map.get(that);

		if (ret == null) {
			// color hasn't been used yet, need to create it
			ret = new Color(that.getRed(),
					that.getGreen(), that.getBlue(), that.getAlpha());
			synchronized (map) {
				map.put(that, ret);
			}

		}

		return ret;
	}

	public static GColor newColor(Color c) {
		return c == null ? null : GColor.newColor(c.getRed(), c.getGreen(),
				c.getBlue());
	}

}
