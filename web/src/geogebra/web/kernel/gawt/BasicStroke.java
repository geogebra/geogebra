package geogebra.web.kernel.gawt;

/**
 * A class that reassembles {@link java.awt.BasicStroke} for use with GWT.
 * This class is immutable.
 */
public class BasicStroke {

	// Constants
	public static final String CAP_BUTT = "butt";
	public static final String CAP_ROUND = "round";
	public static final String CAP_SQUARE = "square";
	public static final String JOIN_BEVEL = "bevel";
	public static final String JOIN_MITER = "miter";
	public static final String JOIN_ROUND ="round";
	// Private fields
	private float lineWidth = 1;
	private String lineCap = CAP_BUTT;
	private String lineJoin = JOIN_MITER;
	private float miterLimit = 10;

	// Constructors
	public BasicStroke() {
		this(1.0f);
	}

	public BasicStroke(float width) {
		this(width, CAP_BUTT, JOIN_MITER);
	}

	public BasicStroke(float lineWidth, String lineCap, String lineJoin) {
		this.lineWidth = lineWidth;
		this.lineCap = lineCap;
		this.lineJoin = lineJoin;
	}

	public BasicStroke(float width, String endCap, String lineJoin2,
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

	public String getLineCap() {
		return lineCap;
	}


	public String getLineJoin() {
		return lineJoin;
	}
	
	public float getMiterLimit() {
		// TODO Auto-generated method stub
		return miterLimit;
	}

}
