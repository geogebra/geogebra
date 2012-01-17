package geogebra.web.awt;

import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;

import geogebra.common.main.AbstractApplication;
import geogebra.web.kernel.gawt.Shape;

/**
 * A class that reassembles {@link java.awt.BasicStroke} for use with GWT.
 * This class is immutable.
 */
public class BasicStroke implements geogebra.common.awt.BasicStroke {

	// Constants
	public static LineJoin[] gwtJoins = {LineJoin.MITER,LineJoin.ROUND,
		LineJoin.BEVEL};
	public static LineCap[] gwtCaps = {LineCap.BUTT,LineCap.ROUND,
		LineCap.SQUARE};
	
	// Private fields
	private float lineWidth = 1;
	private int lineCap = CAP_SQUARE;
	private int lineJoin = JOIN_MITER;
	private float miterLimit = 10;

	// Constructors
	public BasicStroke() {
		this(1.0f);
	}

	public BasicStroke(float width) {
		this(width, CAP_SQUARE, JOIN_MITER);
	}

	public BasicStroke(float lineWidth, int lineCap, int lineJoin) {
		this.lineWidth = lineWidth;
		this.lineCap = lineCap;
		this.lineJoin = lineJoin;
	}

	public BasicStroke(float width, int endCap, int lineJoin,
			float miterLimit2, float[] dash, float f) {
		this.lineWidth = width;
		this.lineCap = endCap;
		this.lineJoin = lineJoin;
	}

	public BasicStroke(geogebra.common.awt.BasicStroke objStroke) {
	   this.lineWidth = ((BasicStroke) objStroke).getLineWidth();
	   this.lineCap = ((BasicStroke) objStroke).getLineCap();
	   this.lineJoin = ((BasicStroke) objStroke).getLineJoin();
    }

	public int getLineCap() {
	 return lineCap;
    }

	// Methods
	public Shape createStrokedShape(Shape shape) {
		return shape;
	}
	
	// Getter
	public float getLineWidth() {
		return lineWidth;
	}

	public int getEndCap() {
		return lineCap;
	}


	public int getLineJoin() {
		return lineJoin;
	}
	
	public float getMiterLimit() {
		return miterLimit;
	}

	public geogebra.common.awt.Shape createStrokedShape(
            geogebra.common.awt.Shape shape) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return shape;
    }
	
	public static int getJoin(String join){
		switch(join.charAt(0)){
		case 'r':return JOIN_ROUND;
		case 'b':return JOIN_BEVEL;
		}
		return JOIN_MITER;
	}
	public static int getCap(String join){
		switch(join.charAt(0)){
		case 'r':return CAP_ROUND;
		case 's':return CAP_SQUARE;
		}
		return CAP_BUTT;
	}

	public LineCap getEndCapString() {
	    return gwtCaps[getEndCap()];
    }
	public LineJoin getLineJoinString() {
	    return gwtJoins[getEndCap()];
    }

	public float[] getDashArray() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

}
