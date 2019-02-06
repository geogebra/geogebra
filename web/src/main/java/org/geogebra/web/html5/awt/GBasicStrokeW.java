package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.ggbjdk.java.awt.DefaultBasicStroke;

import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;

public class GBasicStrokeW {
	// Constants
	final private static LineJoin[] GWT_JOINS = { LineJoin.MITER,
			LineJoin.ROUND,
	        LineJoin.BEVEL };
	final private static LineCap[] GWT_CAPS = { LineCap.BUTT, LineCap.ROUND,
	        LineCap.SQUARE };

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
	public static LineCap getEndCapString(GBasicStroke stroke) {
		return GWT_CAPS[stroke.getEndCap()];
	}

	/**
	 * @param stroke
	 *            stroke
	 * @return GWT join
	 */
	public static LineJoin getLineJoinString(GBasicStroke stroke) {
		return GWT_JOINS[stroke.getLineJoin()];
	}

}
