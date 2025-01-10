package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.ggbjdk.java.awt.DefaultBasicStroke;

public class GBasicStrokeW {
	// Constants
	final private static String[] GWT_JOINS = { "miter", "round", "bevel" };
	final private static String[] GWT_CAPS = { "butt", "round", "square" };

	/**
	 * @param join
	 *            native join from context
	 * @return join type
	 */
	public static int getJoin(String join) {
		switch (join.charAt(0)) {
		case 'r':
			return DefaultBasicStroke.JOIN_ROUND;
		case 'b':
			return DefaultBasicStroke.JOIN_BEVEL;
		default:
			return DefaultBasicStroke.JOIN_MITER;
		}
	}

	/**
	 * @param cap
	 *            native join from context
	 * @return cap type
	 */
	public static int getCap(String cap) {
		switch (cap.charAt(0)) {
		case 'r':
			return DefaultBasicStroke.CAP_ROUND;
		case 's':
			return DefaultBasicStroke.CAP_SQUARE;
		default:
			return DefaultBasicStroke.CAP_BUTT;
		}
	}

	/**
	 * @param stroke
	 *            stroke
	 * @return GWT cap
	 */
	public static String getEndCapString(GBasicStroke stroke) {
		return GWT_CAPS[stroke.getEndCap()];
	}

	/**
	 * @param stroke
	 *            stroke
	 * @return GWT join
	 */
	public static String getLineJoinString(GBasicStroke stroke) {
		return GWT_JOINS[stroke.getLineJoin()];
	}

}
