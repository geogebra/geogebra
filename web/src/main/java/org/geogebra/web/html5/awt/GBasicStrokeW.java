package org.geogebra.web.html5.awt;

import org.geogebra.ggbjdk.java.awt.DefaultBasicStroke;
import org.geogebra.ggbjdk.java.awt.geom.Shape;

import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;

public class GBasicStrokeW extends DefaultBasicStroke {
	// Constants
	final private static LineJoin[] gwtJoins = { LineJoin.MITER, LineJoin.ROUND,
	        LineJoin.BEVEL };
	final private static LineCap[] gwtCaps = { LineCap.BUTT, LineCap.ROUND,
	        LineCap.SQUARE };

	// Private fields
	private int lineCap = CAP_BUTT;

	public GBasicStrokeW(double width, int cap, int join, double miterLimit,
			double[] dash, double dashPhase) {
		super(width, cap, join, miterLimit, dash, dashPhase);
	}

	public GBasicStrokeW(double width, int cap, int join, double miterLimit) {
		super(width, cap, join, miterLimit);
	}

	public GBasicStrokeW(double width, int cap, int join) {
		super(width, cap, join);
	}

	public GBasicStrokeW(double width) {
		super(width);
	}

	public int getLineCap() {
		return lineCap;
	}

	// Methods
	public Shape createStrokedShape(Shape shape) {
		return shape;
	}

	public static int getJoin(String join) {
		switch (join.charAt(0)) {
		case 'r':
			return JOIN_ROUND;
		case 'b':
			return JOIN_BEVEL;
		}
		return JOIN_MITER;
	}

	public static int getCap(String join) {
		switch (join.charAt(0)) {
		case 'r':
			return CAP_ROUND;
		case 's':
			return CAP_SQUARE;
		}
		return CAP_BUTT;
	}

	public LineCap getEndCapString() {
		return gwtCaps[getEndCap()];
	}

	public LineJoin getLineJoinString() {
		return gwtJoins[getLineJoin()];
	}

}
