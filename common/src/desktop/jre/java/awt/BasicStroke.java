package java.awt;

/**
 * A class that reassembles {@link java.awt.BasicStroke} for use with GWT.
 * This class is immutable.
 */
public class BasicStroke {

	// Constants
	public static final String BEVEL = "bevel";
	public static final String BUTT = "butt";
	public static final String MITER = "miter";
	public static final String ROUND = "round";
	public static final String SQUARE = "square";

	// Private fields
	private final float lineWidth;
	private final String lineCap;
	private final String lineJoin;

	// Constructors
	public BasicStroke() {
		this(1.0f);
	}

	public BasicStroke(float width) {
		this(width, BUTT, MITER);
	}

	public BasicStroke(float lineWidth, String lineCap, String lineJoin) {
		this.lineWidth = lineWidth;
		this.lineCap = lineCap;
		this.lineJoin = lineJoin;
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

	//FIXME: Gabor, please evaluate
	public String getLineCap() {
		// return lineCap;
		return ROUND;
	}

	//FIXME: Gabor, please evaluate
	public String getLineJoin() {
		// return lineJoin;
		return ROUND;
	}

}
