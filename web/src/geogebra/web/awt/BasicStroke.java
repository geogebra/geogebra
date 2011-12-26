package geogebra.web.awt;

import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;

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
	private int lineCap = CAP_BUTT;
	private int lineJoin = JOIN_MITER;
	private float miterLimit = 10;

	// Constructors
	public BasicStroke() {
		this(1.0f);
	}

	public BasicStroke(float width) {
		this(width, CAP_BUTT, JOIN_MITER);
	}

	public BasicStroke(float lineWidth, int lineCap, int lineJoin) {
		this.lineWidth = lineWidth;
		this.lineCap = lineCap;
		this.lineJoin = lineJoin;
	}

	public BasicStroke(float width, int endCap, int lineJoin2,
			float miterLimit2, float[] dash, float f) {
		
	}

	// Methods
	public Shape createStrokedShape(Shape shape) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return miterLimit;
	}

	public geogebra.common.awt.Shape createStrokedShape(
            geogebra.common.awt.Shape shape) {
	    // TODO Auto-generated method stub
	    return null;
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

}
